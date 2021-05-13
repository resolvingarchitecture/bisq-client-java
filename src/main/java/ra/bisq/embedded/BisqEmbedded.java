package ra.bisq.embedded;

import bisq.common.UserThread;
import bisq.common.app.AppModule;
import bisq.common.app.Version;
import bisq.common.config.BisqHelpFormatter;
import bisq.common.config.Config;
import bisq.common.config.ConfigException;
import bisq.common.handlers.ErrorMessageHandler;
import bisq.common.handlers.ResultHandler;
import bisq.common.persistence.PersistenceManager;
import bisq.common.setup.GracefulShutDownHandler;
import bisq.core.api.CoreApi;
import bisq.core.api.model.BalancesInfo;
import bisq.core.app.*;
import bisq.core.btc.setup.WalletsSetup;
import bisq.core.btc.wallet.BsqWalletService;
import bisq.core.btc.wallet.BtcWalletService;
import bisq.core.dao.DaoSetup;
import bisq.core.dao.node.full.RpcService;
import bisq.core.offer.Offer;
import bisq.core.offer.OpenOfferManager;
import bisq.core.offer.availability.OfferAvailabilityModel;
import bisq.core.payment.payload.PaymentMethod;
import bisq.core.provider.price.PriceFeedService;
import bisq.core.support.dispute.arbitration.arbitrator.ArbitratorManager;
import bisq.core.trade.statistics.TradeStatisticsManager;
import bisq.core.trade.txproof.xmr.XmrTxProofService;
import bisq.network.p2p.P2PService;
import com.google.common.util.concurrent.FutureCallback;
import org.bitcoinj.core.Transaction;
import org.checkerframework.checker.nullness.qual.Nullable;
import ra.bisq.Bisq;
import ra.bisq.BisqClientService;
import ra.common.Envelope;
import ra.util.AppThread;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Embedded instance of Bisq.
 * Pulls in Core library with dependencies
 * and launches in separate background thread.
 * All api calls are through Bisq CoreApi.
 */
public class BisqEmbedded extends BisqExecutable implements Bisq, GracefulShutDownHandler, BisqSetup.BisqSetupListener, Runnable {

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
    private AppThread bisqThread;

    private String password;
    private Long timeoutSeconds;
    private final String feeAddress = "B12LksWgq7wnqe4243Fej1xk5A7N3Pn3YoC";
    private BalancesInfo balances;
    private CoreApi coreApi;

    public BisqEmbedded(BisqClientService service, Properties properties) {
        super("Bisq Client Service Daemon", "bisqds", "Bisq-Service", Version.VERSION);
        this.service = service;
        this.properties = properties;
    }

    @Override
    public void setWalletPassword(Envelope envelope) {
        String password = (String)envelope.getValue("password");
        String newPassword = (String)envelope.getValue("newPassword");
        coreApi.setWalletPassword(password, newPassword);
        this.password = newPassword;
    }

    @Override
    public void checkWalletBalance(Envelope envelope) {
        BalancesInfo balances = coreApi.getBalances("btc");
        this.balances = balances;
        envelope.addNVP("totalAvailableBalance", balances.getBtc().getTotalAvailableBalance());
        LOG.info("Available BTC Balance: "+balances.getBtc().getAvailableBalance());
    }

    @Override
    public void getWalletAddress(Envelope envelope) {

    }

    @Override
    public void withdrawal(Envelope envelope) {
        String address = (String)envelope.getValue("address");
        String amountSats = (String)envelope.getValue("amount");
        String txFeeRate = ""; // Leave empty so tx fee is determined in-flight
        coreApi.unlockWallet(password, timeoutSeconds);
        coreApi.sendBtc(address, amountSats, txFeeRate, null, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Transaction tx) {

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
        coreApi.lockWallet();
    }

    @Override
    public void exchange(Envelope envelope) {
        // Unpack request
        String direction = (String)envelope.getValue("direction");
        if(direction==null || direction.isEmpty()) { envelope.addErrorMessage("direction nvp is required");return; }
        if(!direction.equals("BUY") && !direction.equals("SELL")) { envelope.addErrorMessage("direction must be either BUY or SELL");return; }

        String currency = (String)envelope.getValue("currency");
        if(currency==null || currency.isEmpty()) { envelope.addErrorMessage("currencyPair nvp is required");return; }
        if(!service.supportedCurrenciesAndMethods.containsKey(currency)) { envelope.addErrorMessage(currency+" not supported. Please use one of: "+service.supportedCurrenciesAndMethods.keySet().toString());return; }

        String methodId = (String)envelope.getValue("method");
        if(methodId==null || methodId.isEmpty()) { envelope.addErrorMessage("method is required");return; }
        if(!service.supportedCurrenciesAndMethods.get(currency).contains(methodId)) { envelope.addErrorMessage(methodId+" not supported with currency "+currency+". Choose one of: "+service.supportedCurrenciesAndMethods.get(currency).toString());return; }
        PaymentMethod paymentMethod = PaymentMethod.getPaymentMethodById(methodId);

        String amountStr = (String)envelope.getValue("amount");
        if(amountStr==null || amountStr.isEmpty()) { envelope.addErrorMessage("amount is required");return; }
        Long amount = Long.parseLong(amountStr);

        // Verify enough BSQ available
        balances = coreApi.getBalances("btc");
        long bsqBalance = balances.getBsq().getAvailableConfirmedBalance();

        // If not, get BSQ

        // Look up offer
        List<Offer> offers = coreApi.getOffers(direction, currency);
        for(Offer offer : offers) {
            if(offer.getDirection().name().equals(direction)
                && offer.getCurrencyCode().equals(currency)
                && offer.getPaymentMethod().equals(paymentMethod)
                && offer.getAmount().value == amount) {
                // If found, make sure it's still valid
//                offer.checkOfferAvailability();
//                coreApi.takeOffer();
            }
        }

        // If Offer is valid, take it

        // If no Offer found or no longer valid, create an Offer and wait for it to be taken

        // Once Offer is taken, commit funds/crypto

        // Once funds/crypto by other party is completed, complete the exchange

    }

    @Override
    public void fundsVerified(Envelope envelope) {

    }

    @Override
    public void cryptoReceived(Envelope envelope) {

    }

    @Override
    public boolean start() {
        execute(new String[]{});
        return started;
    }

    @Override
    public void execute(String[] args) {
        try {
            // When using Embedded Bisq, use Bisq Service directory as home
            this.config = new Config(appName, service.getServiceDirectory(), args);
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
        started = true;
        this.doExecute();
    }

    @Override
    protected void doExecute() {
        // Launch into separate thread to allow starting thread to return while ensuring
        // this service can run as a daemon separately
        bisqThread = new AppThread(this, "Bisq-Service-Thread", true);
        bisqThread.start();
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
        coreApi = injector.getInstance(CoreApi.class);
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

    @Override
    public void run() {
        super.doExecute();
        // Now setup environment

        while (true) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException ignore) {
            }
        }
    }
}
