package ra.bisq;

import bisq.core.api.CoreApi;
import bisq.core.offer.Offer;

import java.util.function.Consumer;

public class AcceptOffer extends BisqHandler implements Consumer<Offer> {

    public AcceptOffer(Bisq bisq, CoreApi coreApi) {
        super(bisq, coreApi);
    }

    @Override
    public void accept(Offer offer) {

    }
}
