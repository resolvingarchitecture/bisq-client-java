package ra.bisq;

import bisq.common.handlers.ErrorMessageHandler;
import bisq.core.api.CoreApi;

public class BisqClientErrorMessageHandler extends BisqHandler implements ErrorMessageHandler {

    public BisqClientErrorMessageHandler(Bisq bisq, CoreApi coreApi) {
        super(bisq, coreApi);
    }

    @Override
    public void handleErrorMessage(String message) {

    }
}
