package ra.bisq;

import ra.common.Envelope;

public interface Bisq {
    boolean start();
    void setWalletPassword(Envelope envelope);
    void checkWalletBalance(Envelope envelope);
    void withdrawal(Envelope envelope);
    void exchange(Envelope envelope);
    void fundsVerified(Envelope envelope);
    void cryptoReceived(Envelope envelope);
    boolean shutdown();
    boolean gracefulShutdown();
}
