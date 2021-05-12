package ra.bisq.remote;

import ra.bisq.Bisq;
import ra.bisq.BisqClientService;
import ra.common.Envelope;

import java.util.Properties;

/**
 * Forwards all API requests to an external
 * Bisq instance set in configuration via
 * an expected Network Manager and Tor.
 * When Tor gets blocked, I2P is used
 * as a relay.
 *
 * Requirements:
 *      ra.networkmanager.NetworkManagerService
 *      ra.tor.TorClientService
 *      ra.i2p.I2PService
 */
public class BisqRemote implements Bisq {

    private BisqClientService service;
    private Properties config;

    public BisqRemote(BisqClientService service, Properties config) {
        this.service = service;
        this.config = config;
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public void setWalletPassword(Envelope envelope) {

    }

    @Override
    public void checkWalletBalance(Envelope envelope) {

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
    public boolean shutdown() {
        return false;
    }

    @Override
    public boolean gracefulShutdown() {
        return false;
    }
}
