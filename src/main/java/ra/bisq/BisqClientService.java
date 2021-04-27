package ra.bisq;

import ra.common.Envelope;
import ra.common.messaging.MessageProducer;
import ra.common.route.Route;
import ra.common.service.BaseService;
import ra.common.service.ServiceStatus;
import ra.common.service.ServiceStatusObserver;
import ra.util.Config;
import ra.util.tasks.TaskRunner;

import java.util.*;
import java.util.logging.Logger;

/**
 * Provides a Client API for Bisq as a Service.
 */
public final class BisqClientService extends BaseService {

    private static final Logger LOG = Logger.getLogger(BisqClientService.class.getName());

    public enum Mode {Unknown, Local, Remote, Embedded}

    public static final String OPERATION_CREATE_WALLET = "CREATE_WALLET";
    public static final String OPERATION_CHECK_WALLET_BALANCE = "CHECK_WALLET_BALANCE";
    public static final String OPERATION_WITHDRAWAL_FROM_WALLET = "WITHDRAWAL_FROM_WALLET";
    public static final String OPERATION_SUBMIT_BUY_OFFER = "SUBMIT_BUY_OFFER";
    public static final String OPERATION_SUBMIT_SELL_OFFER = "SUBMIT_SELL_OFFER";
    public static final String OPERATION_FUNDS_VERIFIED = "FUNDS_VERIFIED";
    public static final String OPERATION_BITCOIN_RECEIVED = "BITCOIN_RECEIVED";

    private Thread taskRunnerThread;
    private TaskRunner taskRunner;
    private Mode mode = Mode.Unknown;

    public BisqClientService() {}

    public BisqClientService(MessageProducer messageProducer, ServiceStatusObserver observer) {
        super(messageProducer, observer);
    }

    public Mode getMode() {
        return mode;
    }

    @Override
    public void handleDocument(Envelope e) {
        super.handleDocument(e);
        Route r = e.getRoute();
        switch(r.getOperation()) {
            case OPERATION_CREATE_WALLET: {
                LOG.warning("Create Wallet not yet implemented.");
                break;
            }
            case OPERATION_CHECK_WALLET_BALANCE: {
                LOG.warning("Check Wallet Balance not yet implemented.");
                break;
            }
            case OPERATION_WITHDRAWAL_FROM_WALLET: {
                LOG.warning("Withdrawal from Wallet not yet implemented.");
                break;
            }
            case OPERATION_SUBMIT_BUY_OFFER: {
                LOG.warning("Submit Buy Offer not yet implemented.");
                break;
            }
            case OPERATION_SUBMIT_SELL_OFFER: {
                LOG.warning("Submit Sell Offer not yet implemented.");
                break;
            }
            case OPERATION_FUNDS_VERIFIED: {
                LOG.warning("Funds Verified not yet implemented.");
                break;
            }
            case OPERATION_BITCOIN_RECEIVED: {
                LOG.warning("Bitcon Received not yet implemented.");
                break;
            }
            default: {
                LOG.warning("Operation ("+r.getOperation()+") not supported. Sending to Dead Letter queue.");
                deadLetter(e);
            }
        }
    }

    public boolean start(Properties p) {
        LOG.info("Starting Bisq Client Service...");
        updateStatus(ServiceStatus.INITIALIZING);
        LOG.info("Loading Bisq Client properties...");
        try {
            // Load environment variables first
            config = Config.loadAll(p, "bisq-client.config");
        } catch (Exception e) {
            LOG.severe(e.getLocalizedMessage());
            return false;
        }

        updateStatus(ServiceStatus.STARTING);

        if(config.get("ra.bisq.mode")!=null) {
            mode = Mode.valueOf(config.getProperty("ra.bisq.mode"));
        }

//        if(taskRunner==null) {
//            taskRunner = new TaskRunner(2, 2);
//            taskRunner.setPeriodicity(1000L); // Default check every second
//            BisqNodeStatusChecker bisqNodeStatusChecker = new BisqNodeStatusChecker(this, taskRunner);
//
//        }
//
//        taskRunnerThread = new Thread(taskRunner);
//        taskRunnerThread.setDaemon(true);
//        taskRunnerThread.setName("BisqClientService-TaskRunnerThread");
//        taskRunnerThread.start();

        updateStatus(ServiceStatus.RUNNING);

        return true;
    }

    @Override
    public boolean pause() {
        return false;
    }

    @Override
    public boolean unpause() {
        return false;
    }

    @Override
    public boolean restart() {
        LOG.info("Bisq client service soft restart commencing...");
        if(gracefulShutdown()) {
            start(config);
            LOG.info("Bisq client service soft restart completed.");
        }
        return true;
    }

    @Override
    public boolean shutdown() {
        updateStatus(ServiceStatus.SHUTTING_DOWN);
        LOG.info("Bisq client service stopping...");

        updateStatus(ServiceStatus.SHUTDOWN);
        LOG.info("Bisq client service stopped.");
        return true;
    }

    @Override
    public boolean gracefulShutdown() {
        updateStatus(ServiceStatus.GRACEFULLY_SHUTTING_DOWN);
        LOG.info("Bisq client service gracefully stopping...");

        updateStatus(ServiceStatus.GRACEFULLY_SHUTDOWN);
        LOG.info("Bisq client service gracefully stopped.");
        return true;
    }

}
