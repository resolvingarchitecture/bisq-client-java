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
     * Request an Exchange from/to a currency to/from BTC
     * using a supported method for a specified amount in BTC.
     *
     * Request Envelope Requires in NVP:
     *      toCurrency:String (e.g. BTC, USD, EUR) - Required
     *      fromCurrency: String (e.g. BTC, USD, EUR) - Required
     *      amountSats:Long - Required
     *
     * Supported methods:
     * LBP : F2F
     * USD : Zelle
     * BSQ : Altcoins
     *
     * Buy BTC as Taker is as follows:
     *  1. Look for Buy BTC offer for requested currency, amount, and method.
     *  2. If offer not found
     *      2.a. Continue to Buy BTC as Maker
     *  3. If offer found,
     *      3.a. Does current BSQ balance cover Taker costs?
     *          3.a.1. Yes
     *              3.a.1.a. Take Offer receiving Trade
     *              3.a.1.b. Wait for verification that BTC sent to Escrow
     *              3.a.1.c. Verify funds sent to Seller
     *              3.a.1.d. Wait for BTC funds released to provided address
     *              3.a.1.e. Close out Trade
     *          3.a.2. No
     *              3.a.2.a. Sell BTC for BSQ to meet minimum
     *              3.a.2.b. Start back over at step 1.
     *
     * Buy BTC as Maker is as follows:
     *  1.
     *
     * Sell BTC as Taker is as follows:
     *  1.
     *
     * Sell BTC as Maker is as follows:
     *  1.
     *
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
