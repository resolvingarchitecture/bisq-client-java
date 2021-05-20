package ra.bisq.local;

import ra.bisq.Bisq;
import ra.bisq.BisqClientService;
import ra.common.Envelope;

import java.util.Properties;

/**
 * Forwards all API calls to the local
 * Bisq http server on localhost via the
 * GRPC API.
 *
 * Requirements: ra.http.HTTPService
 */
public class BisqLocal implements Bisq {

    private BisqClientService service;
    private Properties config;

    public BisqLocal(BisqClientService service, Properties config) {
        this.service = service;
        this.config = config;
    }

    @Override
    public void setWalletPassword(Envelope envelope) {

    }

    @Override
    public void checkWalletBalance(Envelope envelope) {

    }

    @Override
    public void getWalletAddress(Envelope envelope) {

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
        return false;
    }

    @Override
    public boolean shutdown() {
        return false;
    }

    @Override
    public boolean gracefulShutdown() {
        return false;
    }
}
