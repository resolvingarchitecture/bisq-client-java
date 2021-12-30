package ra.bisq;

import ra.common.Envelope;

public interface Bisq {

    String CURRENCY = "currency";
    String CURRENCY_CODE_BTC = "btc";
    String CURRENCY_CODE_BSQ = "bsq";

    String DIRECTION = "direction";
    String DIRECTION_BUY = "BUY";
    String DIRECTION_SELL = "SELL";

    String METHOD = "method";

    String AMOUNT = "amount";

    // Simplified API Methods
    /**
     * Provide old and new passwords from user
     * for the Bisq wallet.
     * Requires in NVP: password, newPassword
     * @param envelope
     */
    void setWalletPassword(Envelope envelope);

    /**
     * Get the current BTC balance of the Bisq wallet.
     * Returns value in Satoshis: balance:Long
     * @param envelope
     */
    void checkWalletBalance(Envelope envelope);

    /**
     * Shows wallet address as public string and QR code.
     * Returns values: publicKey:String, qrCode:String
     * @param envelope
     */
    void getWalletAddress(Envelope envelope);

    /**
     * Send BTC to an address.
     * Requires in NVP: address:String, amount:Long
     * @param envelope
     */
    void withdrawal(Envelope envelope);

    /**
     *
     *
     * @param envelope
     */
    void exchange(Envelope envelope);

    /**
     * Funds verified.
     * @param envelope
     */
    void fundsVerified(Envelope envelope);

    /**
     * Crypto received.
     * @param envelope
     */
    void cryptoReceived(Envelope envelope);

    // LifeCycle Methods
    boolean start();
    boolean shutdown();
    boolean gracefulShutdown();
}
