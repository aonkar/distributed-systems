package cluster.management;

import org.apache.zookeeper.KeeperException;

import java.net.UnknownHostException;

public interface OnElectionCallback {
    void onElectedToBeLeader() throws InterruptedException, KeeperException;

    void onWorker() throws InterruptedException, KeeperException, UnknownHostException;
}
