package cluster.management;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

public class LeaderElection implements Watcher {

    private static final String ELECTION_NAMESPACE_ZNODE = "/election";
    private final OnElectionCallback electionCallback;
    private ZooKeeper zookeeper;
    private String currentZNodeName;


    public LeaderElection(final ZooKeeper zookeeper, final OnElectionCallback electionCallback) {
        this.zookeeper = zookeeper;
        this.electionCallback = electionCallback;
        createElectionZnode();
    }

    public void volunteerForLeadership() throws InterruptedException, KeeperException {
        String zNodePrefix = ELECTION_NAMESPACE_ZNODE + "/c_";
        String zNodeFullPath = zookeeper.create(zNodePrefix, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        this.currentZNodeName = zNodeFullPath.replace(ELECTION_NAMESPACE_ZNODE + "/", "");
    }

    private void createElectionZnode(){
        try{
            if (zookeeper.exists(ELECTION_NAMESPACE_ZNODE, false) == null){
                zookeeper.create(ELECTION_NAMESPACE_ZNODE, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (KeeperException e) {
            throw new RuntimeException(e);
        }
    }


    public void electLeader() throws InterruptedException, KeeperException, UnknownHostException {
        String predecessorZnodeName = null;
        Stat predecessorStat = null;
        while (predecessorStat == null) {
            List<String> children = zookeeper.getChildren(ELECTION_NAMESPACE_ZNODE, false);
            Collections.sort(children);
            String smallestChildren = children.get(0);
            if (smallestChildren.equals(currentZNodeName)) {
                System.out.println("I am the Leader");
                electionCallback.onElectedToBeLeader();
                return;
            } else {
                System.out.println("The leader is" + smallestChildren);
                int predecessorIndex = Collections.binarySearch(children, currentZNodeName) - 1;
                predecessorZnodeName = children.get(predecessorIndex);
                predecessorStat = zookeeper.exists(ELECTION_NAMESPACE_ZNODE + "/" + predecessorZnodeName, this);
            }
            electionCallback.onWorker();
            System.out.println("Watching znode: " + predecessorZnodeName);
        }
    }

    /**
     * @param watchedEvent
     */
    @Override
    public void process(final WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()) {
            case NodeDeleted:
                try {
                    electLeader();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (KeeperException e) {
                    throw new RuntimeException(e);
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
    }
}
