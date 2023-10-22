Communication
    Reliable links
        TCP
    Secure links
        TLS over TCP
    Discovery
        DNS
    APIs
        HTTP, GRPC, REST, JSON, Protocol Buffers, Idempotency

Coordination
    System models
        Arbitary-fault
        Crash-Recovery
        Crash-stop
    Failure detection
        Pings
        Heartbeat
    Time
        Physical clock
            Quartz
            Atomic clock
            Monotonic clock
        Logical clock
            Lamport clock
            Vector clock
    Leader election
        Raft leader election
        Paxos
        Instead of implimenting any of the above, one can use a - Key value store that offers a linearizable compare and swap
    Replication
        State machine replication
        Raft leader election is used to elect a leader
        Consistency model 
            Sequential consistency
            Eventual consistency
                CAP Theorem
                PACELC Theorem
        Chain Replication    
    Coordination avoidance
        Broadcast
            Eager reliable broadcast
            Gossip broadcast
    Transactions
        ACID
            Atomicity
                WAL - Write Ahead Lock - Single datastore
                2PC - 2 Phase commit - Multiple processes
                    Coorinator
                    Participants
            Isolation
                Dirty read
                Fuzzy read
                Phantom read
            Concurrency
                Pessimistic - efficient for conflicting workload(writes)
                    2PL - 2 phase locking
                        Read lock
                        Write lock
                Optimistic - efficient for read heavy workloads
                    MVCC - Multi version concurrency control 
            
    Asynchronous transactions
        Outbox pattern
            Kafka, Message channels 
        Sagas pattern
            Local transactions
            Compensation transaction for each of the local transaction

Scalability
    HTTP caching
        Client caching
        Caching server - Reverse proxies
            NGINX & HA Proxy
    Content delivery networks
        Overlay network of geographically distributed caching servers(Reverse proxy)
    Partitioning
        Splitting data into partition or shards
        Gateway Service
            Reverse proxy
            Knowing the mapping interacting with etcd or Zookeeper
        Fault tolerant coordination service to maintain the configuration like etcd or Zookeeper
        Range Partitioning 
        Hash Partitioning
    File storage
        Blob stores
    Network load balancing
        Load balancing
            Service Discovery
            Health Checks
        DNS Load balancing
        Transport Layer Load balancing (L4)
        Application Layer load balancing (L7)
            Sidecar pattern where the L7 load balacer is the side car process in the client system/server/node (If both client and server are internal to the organization)
    Data storage
        Replication
        Partitioning and Sharding
        NoSQL
    Caching
        Local cache(In-memory)
        External cache(Distributed cahce)
    Microservices
    Control planes and data planes
    Messaging
        Kafka

Resiliency
    Common failure causes
        Hardware failures
        Incorrect error handling
        Configuration changes
        Single point of failure
        Network faults
        Resource leaks
        Load pressure
            Sudden burst increase
            Grdual increase
        Cascading failures
        Managing risks
    Redundancy
        Correlation
            Having multiple Regions, AZs
    Fault isolation
        Shuffle sharding - Making sure the likelihood of 2 users being on same shards(in case of redundancy) is rare
        Cellular architecture 
    Downstream resiliency
        Timeout
            Figure out the x% of requests that the downstream system result in timeout would be okay and then find what is the (100 - x)th percentile of the response time to configure timeout
            Have monitoring around this
        Retry
            Exponential backoff
            Jitter/Delay
            Retry Amplification - Problem of having retries at multiple downstream systems. Instead Have it at one place and fail fast at other places
        Circuit breaker
            Open, Closed, Half open
    Upstream resiliency
        Load shedding
        Load leveling
            Adding a queue in between
        Rate-limiting
            Leaky bucket
            Sliding window
        Constant work
            reduce the number of modes that the application operates in
    Always try to reduce the blast radius

Maintainability
    Testing
        Scope 
            The SUT represnts the scope of the test
            SUT determines the category of the test
        Unit test
        Integration test
        End-to-End test
        Fake, Stub and Mock
    Continuous delivery and deployment
        Review
        Build 
        Pre-production rollout
        Production rollout
        Infrastructure as Code using tools like Terraform
    Monitoring
        Metrics
        Service Level Indicators
            Response time
            Availability
        SLO
            Error budget
            Alert
            Dashboard
            
    Observability
    Manageability

Summary: https://understandingdistributed.systems/

The DynamoDB book
TLA


(9900 * 0.2 + 100 * 20)
