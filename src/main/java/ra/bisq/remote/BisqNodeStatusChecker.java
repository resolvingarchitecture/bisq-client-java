package ra.bisq.remote;

import ra.bisq.BisqClientService;
import ra.util.tasks.BaseTask;
import ra.util.tasks.TaskRunner;

/**
 * Checks status of remote node at randomized intervals.
 */
public class BisqNodeStatusChecker extends BaseTask {

    private final BisqClientService service;

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
