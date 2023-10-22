package cluster.management;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceRegistry implements Watcher {

    private static final String REGISTRY_NAMESPACE_ZNODE = "/service_registry";
    private final ZooKeeper zookeeper;
    private String currentZnode;
    List<String> allServicesAddresses;

    public ServiceRegistry(final ZooKeeper zooKeeper){
        this.zookeeper = zooKeeper;
        createServiceRegistryZnode();
    }

    public void registerToCluster(final String data) throws InterruptedException, KeeperException {
        this.currentZnode = zookeeper.create(REGISTRY_NAMESPACE_ZNODE + "/n_", data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("Registered with the service registry: " + this.currentZnode);
    }

    public void registerForUpdateAddresses() throws InterruptedException, KeeperException {
        updateAddresses();
    }

    public synchronized List<String> getAllServicesAddresses() throws InterruptedException, KeeperException {
        if(allServicesAddresses == null){
            updateAddresses();
        }
        return allServicesAddresses;
    }

    public void unregisterFromCluster() throws InterruptedException, KeeperException {
        System.out.println("*****************************Inside unregister*****************************************");
        if(currentZnode != null && zookeeper.exists(currentZnode, false) != null){
            zookeeper.delete(currentZnode, -1);
        }
    }

    private void createServiceRegistryZnode(){
        try{
            if (zookeeper.exists(REGISTRY_NAMESPACE_ZNODE, false) == null){
                zookeeper.create(REGISTRY_NAMESPACE_ZNODE, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (KeeperException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void process(WatchedEvent event) {
        try {
            updateAddresses();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (KeeperException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void updateAddresses() throws InterruptedException, KeeperException {
        System.out.println("********************Inside update address***********************");
        List<String> znodeChildren = zookeeper.getChildren(REGISTRY_NAMESPACE_ZNODE, this);
        List<String> addresses = new ArrayList<>(znodeChildren.size());
        for(String zNode: znodeChildren){
            String fullPathOfZnode = REGISTRY_NAMESPACE_ZNODE + "/" + zNode;
            Stat stat = zookeeper.exists(fullPathOfZnode, false);
            if(stat == null){
                continue;
            }
            byte[] addressBytes = zookeeper.getData(fullPathOfZnode, false, stat);
            addresses.add(new String((addressBytes)));
        }
        this.allServicesAddresses = Collections.unmodifiableList(addresses);
        System.out.println("The cluster address are: " + this.allServicesAddresses);
    }
}
