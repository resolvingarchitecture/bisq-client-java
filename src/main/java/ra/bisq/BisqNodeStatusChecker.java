package ra.bisq;

import ra.util.tasks.BaseTask;
import ra.util.tasks.TaskRunner;

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
