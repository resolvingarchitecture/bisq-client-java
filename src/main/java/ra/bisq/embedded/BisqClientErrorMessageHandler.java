package ra.bisq.embedded;

import bisq.common.handlers.ErrorMessageHandler;

public class BisqClientErrorMessageHandler implements ErrorMessageHandler {

    private final BisqEmbedded bisqEmbedded;

    public BisqClientErrorMessageHandler(BisqEmbedded bisqEmbedded) {
        this.bisqEmbedded = bisqEmbedded;
    }

    @Override
    public void handleErrorMessage(String message) {

    }
}
