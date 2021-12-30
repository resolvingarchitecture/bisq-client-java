package ra.bisq;

import ra.common.Config;
import ra.common.Envelope;
import ra.common.messaging.MessageProducer;
import ra.common.route.Route;
import ra.common.service.BaseService;
import ra.common.service.ServiceStatus;
import ra.common.service.ServiceStatusObserver;

import java.util.*;
import java.util.logging.Logger;

/**
 * Provides a Client API for Bisq as a Service.
 * Requires local Bisq Node.
 */
public final class BisqClientService extends BaseService {

    private static final Logger LOG = Logger.getLogger(BisqClientService.class.getName());

    public enum Mode {Local, Remote, Embedded}

    public static final String OPERATION_SET_WALLET_PASSWORD = "SET_WALLET_PASSWORD";
    public static final String OPERATION_CHECK_WALLET_BALANCE = "CHECK_WALLET_BALANCE";
    public static final String OPERATION_WITHDRAWAL_FROM_WALLET = "WITHDRAWAL_FROM_WALLET";
    public static final String OPERATION_GET_WALLET_ADDRESS = "GET_WALLET_ADDRESS";
    public static final String OPERATION_EXCHANGE = "EXCHANGE";
    public static final String OPERATION_FUNDS_VERIFIED = "FUNDS_VERIFIED";
    public static final String OPERATION_CRYPTO_RECEIVED = "CRYPTO_RECEIVED";

    private Mode mode = Mode.Embedded; // default

    public Map<String,List<String>> supportedCurrenciesAndMethods = new HashMap<>();

    public BisqClientService() {}

    public BisqClientService(MessageProducer messageProducer, ServiceStatusObserver observer) {
        super(messageProducer, observer);
    }

    public Mode getMode() {
        return mode;
    }

    @Override
    public void handleDocument(Envelope e) {
        Route r = e.getRoute();
        switch(r.getOperation()) {
            case OPERATION_SET_WALLET_PASSWORD: {
                LOG.warning("Not yet implemented.");
                break;
            }
            case OPERATION_CHECK_WALLET_BALANCE: {
                LOG.warning("Not yet implemented.");
                break;
            }
            case OPERATION_GET_WALLET_ADDRESS: {
                LOG.warning("Not yet implemented.");
                break;
            }
            case OPERATION_WITHDRAWAL_FROM_WALLET: {
                LOG.warning("Not yet implemented.");
                break;
            }
            case OPERATION_EXCHANGE: {
                LOG.warning("Not yet implemented.");
                break;
            }
            case OPERATION_FUNDS_VERIFIED: {
                LOG.warning("Not yet implemented.");
                break;
            }
            case OPERATION_CRYPTO_RECEIVED: {
                LOG.warning("Not yet implemented.");
                break;
            }
            default: {
                LOG.warning("Operation ("+r.getOperation()+") not supported. Sending to Dead Letter queue.");
                deadLetter(e);
            }
        }
    }

    public boolean start(Properties p) {
        LOG.info("Starting...");
        updateStatus(ServiceStatus.INITIALIZING);
        if(!super.start(p))
            return false;
        LOG.info("Loading properties...");
        try {
            // Load environment variables first
            config = Config.loadAll(p, "bisq-client.config");
        } catch (Exception e) {
            LOG.severe(e.getLocalizedMessage());
            return false;
        }

        updateStatus(ServiceStatus.STARTING);



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
        LOG.info("restart commencing...");
        if(gracefulShutdown()) {
            start(config);
            LOG.info("restart completed.");
        }
        return true;
    }

    @Override
    public boolean shutdown() {
        updateStatus(ServiceStatus.SHUTTING_DOWN);
        LOG.info("stopping...");

        updateStatus(ServiceStatus.SHUTDOWN);
        LOG.info("Stopped.");
        return true;
    }

    @Override
    public boolean gracefulShutdown() {
        updateStatus(ServiceStatus.GRACEFULLY_SHUTTING_DOWN);
        LOG.info("gracefully stopping...");

        updateStatus(ServiceStatus.GRACEFULLY_SHUTDOWN);
        LOG.info("Gracefully stopped.");
        return true;
    }

}
