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

    public static final String OPERATION_CREATE_WALLET = "CREATE_WALLET";
    public static final String OPERATION_CHECK_WALLET_BALANCE = "CHECK_WALLET_BALANCE";
    public static final String OPERATION_WITHDRAWAL_FROM_WALLET = "WITHDRAWAL_FROM_WALLET";
    public static final String OPERATION_SUBMIT_BUY_OFFER = "SUBMIT_BUY_OFFER";
    public static final String OPERATION_SUBMIT_SELL_OFFER = "SUBMIT_SELL_OFFER";
    public static final String OPERATION_FUNDS_VERIFIED = "FUNDS_VERIFIED";
    public static final String OPERATION_BITCOIN_RECEIVED = "BITCOIN_RECEIVED";

    private Thread taskRunnerThread;
    private TaskRunner taskRunner;

    public BisqClientService() {}

    public BisqClientService(MessageProducer messageProducer, ServiceStatusObserver observer) {
        super(messageProducer, observer);
    }

    @Override
    public void handleDocument(Envelope e) {
        super.handleDocument(e);
        Route r = e.getRoute();
        switch(r.getOperation()) {
            case OPERATION_CREATE_WALLET: {

                break;
            }
            case OPERATION_CHECK_WALLET_BALANCE: {

                break;
            }
            case OPERATION_WITHDRAWAL_FROM_WALLET: {

                break;
            }
            case OPERATION_SUBMIT_BUY_OFFER: {

                break;
            }
            case OPERATION_SUBMIT_SELL_OFFER: {

                break;
            }
            case OPERATION_FUNDS_VERIFIED: {

                break;
            }
            case OPERATION_BITCOIN_RECEIVED: {

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

        if(taskRunner==null) {
            taskRunner = new TaskRunner(2, 2);
            taskRunner.setPeriodicity(1000L); // Default check every second
            BisqNodeStatusChecker bisqNodeStatusChecker = new BisqNodeStatusChecker(this, taskRunner);

        }

        taskRunnerThread = new Thread(taskRunner);
        taskRunnerThread.setDaemon(true);
        taskRunnerThread.setName("BisqClientService-TaskRunnerThread");
        taskRunnerThread.start();

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
