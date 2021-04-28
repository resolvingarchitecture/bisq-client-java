package ra.bisq.local;

import ra.bisq.Bisq;
import ra.common.Envelope;

import java.util.Properties;

public class BisqLocal implements Bisq {

    private Properties config;

    public BisqLocal(Properties config) {
        this.config = config;
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public void createWallet(Envelope envelope) {

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
