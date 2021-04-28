package ra.bisq.embedded;

import bisq.core.api.CoreApi;
import bisq.core.app.BisqHeadlessApp;
import ra.bisq.Bisq;
import ra.common.Envelope;

import java.util.Properties;

public class BisqEmbedded extends BisqHeadlessApp implements Bisq {

    private Properties config;
    private BisqHeadlessApp app;
    private CoreApi coreApi;

    public BisqEmbedded(Properties config) {
        this.config = config;
    }

    @Override
    public boolean start() {
        startApplication();
        coreApi = injector.getInstance(CoreApi.class);
        return true;
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
        app.stop();
        return true;
    }

    @Override
    public boolean gracefulShutdown() {
        app.stop();
        return true;
    }
}
