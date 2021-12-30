package ra.bisq;

import ra.common.tasks.BaseTask;
import ra.common.tasks.TaskRunner;

public class BisqNodeStatusChecker extends BaseTask {

    private BisqClientService service;

    public BisqNodeStatusChecker(BisqClientService service, TaskRunner taskRunner) {
        super(BisqNodeStatusChecker.class.getSimpleName(), taskRunner);
        this.service = service;
    }

    @Override
    public Boolean execute() {
        // Send Requests to determine status of Bisq node

        return null;
    }
}
