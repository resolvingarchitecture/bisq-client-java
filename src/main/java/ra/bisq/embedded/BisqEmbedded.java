package ra.bisq.embedded;

import bisq.common.UserThread;
import bisq.common.app.AppModule;
import bisq.common.app.Version;
import bisq.common.config.BisqHelpFormatter;
import bisq.common.config.Config;
import bisq.common.config.ConfigException;
import bisq.common.handlers.ResultHandler;
import bisq.common.persistence.PersistenceManager;
import bisq.common.setup.GracefulShutDownHandler;
import bisq.common.util.Utilities;
import bisq.core.api.CoreApi;
import bisq.core.api.model.BalancesInfo;
import bisq.core.app.*;
import bisq.core.btc.setup.WalletsSetup;
import bisq.core.btc.wallet.BsqWalletService;
import bisq.core.btc.wallet.BtcWalletService;
import bisq.core.dao.DaoSetup;
import bisq.core.dao.node.full.RpcService;
import bisq.core.offer.OpenOfferManager;
import bisq.core.provider.price.PriceFeedService;
import bisq.core.support.dispute.arbitration.arbitrator.ArbitratorManager;
import bisq.core.trade.statistics.TradeStatisticsManager;
import bisq.core.trade.txproof.xmr.XmrTxProofService;
import bisq.network.p2p.P2PService;
import ra.bisq.Bisq;
import ra.bisq.BisqClientService;
import ra.common.Envelope;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

public class BisqEmbedded extends BisqExecutable implements Bisq, GracefulShutDownHandler, BisqSetup.BisqSetupListener {

    private static final Logger LOG = Logger.getLogger(BisqEmbedded.class.getName());

    private BisqClientService service;
    private Properties properties;

    private final String fullName = "Bisq Client Service Daemon";
    private final String scriptName = "bisqds";
    private final String appName = "Bisq-Service";
    private final String version = Version.VERSION;

    private BisqHeadlessApp headlessApp;
    private boolean started = false;
    private boolean isShutdownInProgress = false;
    private boolean hasDowngraded;
    private ResultHandler shutdownHandler;

    private CoreApi coreApi;

    public BisqEmbedded(BisqClientService service, Properties properties) {
        super("Bisq Client Service Daemon", "bisqds", "Bisq-Service", Version.VERSION);
        this.service = service;
        this.properties = properties;
    }

    @Override
    public void createWallet(Envelope envelope) {

    }

    @Override
    public void checkWalletBalance(Envelope envelope) {
        BalancesInfo info = coreApi.getBalances("btc");
        LOG.info("Available BTC Balance: "+info.getBtc().getAvailableBalance());
    }

    @Override
    public void withdrawal(Envelope envelope) {

    }

    @Override
    public void exchange(Envelope envelope) {

    }

    @Override
    public void fundsVerified(Envelope envelope) {

    }

    @Override
    public void cryptoReceived(Envelope envelope) {

    }

    @Override
    public boolean start() {
        execute(null);
        return started;
    }

    @Override
    public void execute(String[] args) {
        try {
            this.config = new Config(appName, Utilities.getUserDataDir(), args);
            if (this.config.helpRequested) {
                this.config.printHelp(System.out, new BisqHelpFormatter(this.fullName, this.scriptName, this.version));
                return;
            }
        } catch (ConfigException var3) {
            System.err.println("error: " + var3.getMessage());
            return;
        } catch (Throwable var4) {
            System.err.println("fault: An unexpected error occurred. Please file a report at https://bisq.network/issues");
            var4.printStackTrace(System.err);
            return;
        }

        this.doExecute();
    }

    @Override
    protected void doExecute() {
        super.doExecute();
        coreApi = injector.getInstance(CoreApi.class);
        checkWalletBalance(null);
        started = true;
        keepRunning();
    }

    @Override
    protected void configUserThread() {
        UserThread.setExecutor(Executors.newSingleThreadExecutor());
    }

    @Override
    protected void launchApplication() {
        headlessApp = new BisqHeadlessApp();
        onApplicationLaunched();
    }

    @Override
    protected void onApplicationLaunched() {
        super.onApplicationLaunched();
        headlessApp.setGracefulShutDownHandler(this);
    }

    @Override
    public void handleUncaughtException(Throwable throwable, boolean doShutDown) {
        headlessApp.handleUncaughtException(throwable, doShutDown);
    }

    @Override
    public void onSetupComplete() {
        LOG.info("onSetupComplete");
    }

    @Override
    protected AppModule getModule() {
        return new CoreModule(config);
    }

    @Override
    protected void applyInjector() {
        super.applyInjector();
        headlessApp.setInjector(injector);
    }

    @Override
    protected void startApplication() {
        headlessApp.startApplication();
        onApplicationStarted();
    }

    @Override
    public boolean shutdown() {
        return gracefulShutdown();
    }

    @Override
    public boolean gracefulShutdown() {
        LOG.info("Start graceful shutdown");
        if(isShutdownInProgress)
            return true;
        isShutdownInProgress = true;
        if (injector == null) {
            LOG.info("Shut down called before injector was created");
            shutdownHandler.handleResult();
            LOG.info("Graceful shutdown complete");
            return true;
        }
        try {
            injector.getInstance(PriceFeedService.class).shutDown();
            injector.getInstance(ArbitratorManager.class).shutDown();
            injector.getInstance(TradeStatisticsManager.class).shutDown();
            injector.getInstance(XmrTxProofService.class).shutDown();
            injector.getInstance(RpcService.class).shutDown();
            injector.getInstance(DaoSetup.class).shutDown();
            injector.getInstance(AvoidStandbyModeService.class).shutDown();
            LOG.info("OpenOfferManager shutdown started");
            injector.getInstance(OpenOfferManager.class).shutDown(() -> {
                LOG.info("OpenOfferManager shutdown completed");

                injector.getInstance(BtcWalletService.class).shutDown();
                injector.getInstance(BsqWalletService.class).shutDown();

                // We need to shutdown BitcoinJ before the P2PService as it uses Tor.
                WalletsSetup walletsSetup = injector.getInstance(WalletsSetup.class);
                walletsSetup.shutDownComplete.addListener((ov, o, n) -> {
                    LOG.info("WalletsSetup shutdown completed");

                    injector.getInstance(P2PService.class).shutDown(() -> {
                        LOG.info("P2PService shutdown completed");
                        module.close(injector);
                        if (!hasDowngraded) {
                            // If user tried to downgrade we do not write the persistable data to avoid data corruption
                            PersistenceManager.flushAllDataToDiskAtShutdown(() -> {
                                shutdownHandler.handleResult();
                            });
                        }
                    });
                });
                walletsSetup.shutDown();
            });

            // Wait max 20 sec.
            UserThread.runAfter(() -> {
                LOG.warning("Graceful shut down not completed in 20 sec. We trigger our timeout handler.");
                if (!hasDowngraded) {
                    // If user tried to downgrade we do not write the persistable data to avoid data corruption
                    PersistenceManager.flushAllDataToDiskAtShutdown(() -> {
                        LOG.info("Graceful shutdown resulted in a timeout");
                        shutdownHandler.handleResult();
                    });
                }
            }, 20);
        } catch (Throwable t) {
            LOG.warning("App shutdown failed with exception " + t.getLocalizedMessage());
            if (!hasDowngraded) {
                // If user tried to downgrade we do not write the persistable data to avoid data corruption
                PersistenceManager.flushAllDataToDiskAtShutdown(() -> {
                    LOG.info("Graceful shutdown resulted in an error");
                    shutdownHandler.handleResult();
                });
            }
        }
        LOG.info("Graceful shutdown complete");
        return true;
    }

    @Override
    public void gracefulShutDown(ResultHandler resultHandler) {
        shutdownHandler = resultHandler;
        gracefulShutdown();
    }

    private void keepRunning() {
        while (true) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException ignore) {
            }
        }
    }
}
