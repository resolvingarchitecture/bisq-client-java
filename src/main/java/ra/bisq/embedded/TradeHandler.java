package ra.bisq.embedded;

import bisq.core.trade.Trade;

import java.util.function.Consumer;

public class TradeHandler implements Consumer<Trade> {

    private final BisqEmbedded bisqEmbedded;

    public TradeHandler(BisqEmbedded bisqEmbedded) {
        this.bisqEmbedded = bisqEmbedded;
    }

    @Override
    public void accept(Trade trade) {

    }
}
