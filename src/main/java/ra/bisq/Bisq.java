package ra.bisq;

import ra.common.Envelope;

public interface Bisq {
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
     * Request an Exchange.
     * Requires in NVP:
     *      toCurrency:String (e.g. BTC, USD, EUR)
     *      fromCurrency: String (e.g. BTC, USD, EUR)
     *      amountSats:Long
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
