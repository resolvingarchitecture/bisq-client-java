package ra.bisq;

import ra.util.tasks.BaseTask;
import ra.util.tasks.TaskRunner;

import java.util.logging.Logger;

class ExchangeRateChecker extends BaseTask {

    private static final Logger LOG = Logger.getLogger(ExchangeRateChecker.class.getName());

    private BisqClientService service;

    public ExchangeRateChecker(BisqClientService service, TaskRunner taskRunner) {
        super(ExchangeRateChecker.class.getSimpleName(), taskRunner);
        this.service = service;
    }

    @Override
    public Boolean execute() {
        service.checkExchangeRates();
        return true;
    }
}
