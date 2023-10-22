package cluster.management;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class Application implements Watcher {
    private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
    private static final int SESSION_TIMEOUT = 3000;
    private static final int DEFAULT_PORT = 8080;
    private ZooKeeper zooKeeper;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        int currentPort = args.length == 1 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        final Application application = new Application();
        final ZooKeeper zookeeper = application.connectToZookeeper();
        final ServiceRegistry serviceRegistry = new ServiceRegistry(zookeeper);
        final OnElectionAction electionAction = new OnElectionAction(serviceRegistry, currentPort);
        final LeaderElection leaderElection = new LeaderElection(zookeeper, electionAction);
        leaderElection.volunteerForLeadership();
        leaderElection.electLeader();
        application.run();
        application.close();
        System.out.println("Disconnected from zookeeper, exiting application");
    }

    public void run() throws InterruptedException {
        synchronized (zooKeeper) {
            zooKeeper.wait();
        }
    }

    public void close() throws InterruptedException {
        zooKeeper.close();
    }

    public ZooKeeper connectToZookeeper() throws IOException {
        this.zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT, this);
        return this.zooKeeper;
    }

    @Override
    public void process(final WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()) {
            case None:
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("Successfully connected to Zookeeper");
                } else {
                    System.out.println("Disconnected from zookeeper event");
                    zooKeeper.notifyAll();
                }
                break;
        }
    }
}
