package ra.bisq;

import bisq.core.api.CoreApi;

public abstract class BisqHandler {

    protected final Bisq bisq;
    protected final CoreApi coreApi;

    public BisqHandler(Bisq bisq, CoreApi coreApi) {
        this.bisq = bisq;
        this.coreApi = coreApi;
    }
}
