package cluster.management;

import org.apache.zookeeper.KeeperException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class OnElectionAction implements OnElectionCallback{
    private final ServiceRegistry serviceRegistry;
    private final int port;

    public OnElectionAction(final ServiceRegistry serviceRegistry, final int port){
        this.serviceRegistry =serviceRegistry;
        this.port = port;
    }

    @Override
    public void onElectedToBeLeader() throws InterruptedException, KeeperException {
        serviceRegistry.unregisterFromCluster();
        serviceRegistry.registerForUpdateAddresses();
    }

    @Override
    public void onWorker() throws InterruptedException, KeeperException, UnknownHostException {
        String currentServerAddress = String.format("https://%s:%d", InetAddress.getLocalHost().getCanonicalHostName(), port);
        serviceRegistry.registerToCluster(currentServerAddress);
    }
}
