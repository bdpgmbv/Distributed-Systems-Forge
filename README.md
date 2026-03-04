# Distributed Systems — Hands-On Practice Guide

### Java • Spring Boot • Kafka

> **For Senior Technical Architects** — 13 Domains • 92 Topics • All Production-Ready

This guide covers every distributed systems topic and technique that a senior technical architect should master. Each item is designed to be practiced by building real, end-to-end implementations in Java, Spring Boot, and Apache Kafka. All patterns listed are used in production systems at scale.

---

## 1. Messaging & Event-Driven Architecture

- **Event Sourcing** — Persist state as an append-only sequence of events in Kafka; rebuild state by replaying
- **CQRS** (Command Query Responsibility Segregation) — Separate write models (command side publishing to Kafka) from read models (materialized views in Elasticsearch/Redis)
- **Transactional Outbox Pattern** — Write to DB + outbox table atomically, then relay events to Kafka (use Debezium CDC or a poller)
- **Saga Pattern (Choreography)** — Coordinate multi-service transactions via Kafka events with compensating actions on failure
- **Saga Pattern (Orchestration)** — Central orchestrator service managing saga steps, timeouts, and rollbacks
- **Dead Letter Queue (DLQ) Handling** — Route poison-pill messages, implement retry policies, manual re-drive tooling
- **Idempotent Consumer** — Deduplicate Kafka messages using idempotency keys stored in DB/Redis
- **Exactly-Once Semantics** — Kafka transactions + `read_committed` isolation + idempotent producers
- **Event Schema Evolution** — Use Avro/Protobuf with Confluent Schema Registry, handle backward/forward compatibility
- **Change Data Capture (CDC)** — Debezium capturing DB changes → Kafka → downstream consumers
- **Competing Consumers with Partitioned Ordering** — Consumer groups, partition assignment strategies, rebalancing
- **Event-Driven Notification System** — Fan-out pattern, topic-per-event-type vs single topic with routing
- **Delayed/Scheduled Message Processing** — Implement delay queues using Kafka + timestamp-based polling or DB-backed scheduler
- **Event Replay & Temporal Queries** — Replay from earliest offset to rebuild state or audit

---

## 2. Data Consistency & Distributed Transactions

- **Two-Phase Commit (2PC) Simulation** — Implement coordinator + participant protocol to understand why it's avoided in microservices
- **Try-Confirm/Cancel (TCC)** — Reservation-based distributed transaction pattern across services
- **Distributed Locking** — Redis (Redisson/RedLock), ZooKeeper, or DB-based pessimistic locks
- **Optimistic Concurrency Control** — Version fields, CAS (compare-and-swap) in DB updates, conflict resolution
- **Consistent Hashing** — Implement a hash ring for partitioning data across nodes, virtual nodes for balance
- **Vector Clocks / Lamport Timestamps** — Implement causal ordering of events across services
- **CRDTs (Conflict-Free Replicated Data Types)** — Build a G-Counter, PN-Counter, LWW-Register for eventual consistency
- **Read-Your-Writes Consistency** — Sticky sessions or version-token forwarding after writes

---

## 3. Resilience & Fault Tolerance

- **Circuit Breaker** — Resilience4j integration, state transitions (closed → open → half-open), fallback methods
- **Retry with Exponential Backoff + Jitter** — Resilience4j or Spring Retry, configurable policies
- **Bulkhead Pattern** — Thread-pool isolation and semaphore isolation per downstream dependency
- **Rate Limiting** — Token bucket / sliding window implemented in Redis or in-memory (Resilience4j, Bucket4j)
- **Timeout Pattern** — Per-call timeouts, timeout propagation across service chains
- **Fallback & Graceful Degradation** — Serve cached/default responses when dependencies are down
- **Health Checks & Readiness/Liveness Probes** — Spring Boot Actuator custom health indicators, dependency checks
- **Chaos Engineering** — Inject latency, exceptions, kill consumers programmatically, observe system behavior

---

## 4. Service Communication Patterns

- **Synchronous REST with Service Discovery** — Spring Cloud + Eureka/Consul, client-side load balancing
- **gRPC Inter-Service Communication** — Protobuf contracts, bidirectional streaming, deadlines
- **API Gateway** — Spring Cloud Gateway with routing, filtering, rate limiting, authentication
- **Backend for Frontend (BFF)** — Per-client aggregation layer
- **Request-Reply over Kafka** — `ReplyingKafkaTemplate`, correlation IDs, reply topics
- **API Versioning Strategies** — URI, header, content negotiation, parallel deployment
- **Service Mesh Concepts** — Implement sidecar proxy behavior (mTLS, retries, observability) at application level

---

## 5. Distributed Caching

- **Cache-Aside (Lazy Loading)** — Spring Cache + Redis, TTL management
- **Write-Through / Write-Behind Cache** — Synchronous vs async cache population
- **Cache Invalidation via Kafka Events** — Event-driven cache busting across instances
- **Distributed Cache with Near-Cache** — Hazelcast/Caffeine L1 + Redis L2 tiered caching
- **Cache Stampede Prevention** — Distributed locks on cache miss, probabilistic early expiration

---

## 6. Distributed Configuration & Coordination

- **Centralized Configuration** — Spring Cloud Config Server backed by Git, refresh scoping, encryption
- **Feature Flags** — Runtime toggle system with DB/Config server, gradual rollouts
- **Leader Election** — ZooKeeper or Spring Integration `LockRegistry` for singleton tasks in a cluster
- **Distributed Scheduler** — ShedLock or Quartz Cluster for ensuring a scheduled job runs on only one instance
- **Dynamic Consumer Scaling** — Programmatically adjust Kafka consumer concurrency based on lag metrics

---

## 7. Observability & Monitoring

- **Distributed Tracing** — Micrometer Tracing + OpenTelemetry + Zipkin/Jaeger, trace context propagation through Kafka headers
- **Structured Logging with Correlation IDs** — MDC propagation across threads and async boundaries, ELK stack
- **Metrics Collection & Dashboarding** — Micrometer → Prometheus → Grafana, custom business metrics
- **Consumer Lag Monitoring & Alerting** — Expose Kafka consumer lag as metrics, auto-scaling triggers
- **Audit Trail System** — Immutable event log for compliance, queryable audit store

---

## 8. Security in Distributed Systems

- **OAuth2 / JWT Token Propagation** — Spring Security Resource Server, token relay across services
- **mTLS Between Services** — Mutual TLS for service-to-service authentication
- **API Key Management & Rotation** — Key versioning, graceful rotation without downtime
- **Kafka Security** — SASL/SCRAM or SSL authentication, ACL-based topic authorization
- **Secrets Management** — HashiCorp Vault integration with Spring Cloud Vault, dynamic credentials
- **Rate Limiting per Tenant** — Multi-tenant throttling at API gateway level

---

## 9. Data Partitioning & Storage Patterns

- **Database Sharding** — Implement shard routing by key (tenant ID, user ID) with multiple DataSources
- **Read Replicas with Write/Read Splitting** — Route reads to replicas, writes to primary via Spring `AbstractRoutingDataSource`
- **Polyglot Persistence** — Different stores per service (Postgres, MongoDB, Elasticsearch, Redis), sync via events
- **Outbox + Polling Publisher** — Alternative to CDC, poll outbox table and publish to Kafka
- **Data Archival & Partitioning** — Time-based table partitioning, moving old data to cold storage
- **Multi-Tenancy** — Schema-per-tenant or discriminator column, tenant context propagation

---

## 10. Stream Processing

- **Kafka Streams Stateful Processing** — KTable, windowed aggregations, joins (KStream-KTable, KStream-KStream)
- **Kafka Streams Interactive Queries** — Expose local state store via REST for real-time lookups
- **Real-Time Fraud/Anomaly Detection Pipeline** — Sliding window aggregations, threshold alerting
- **Stream-Table Duality** — Materialize a compacted Kafka topic as a lookup table
- **Backpressure Handling** — Manage consumer throughput with pause/resume, bounded queues

---

## 11. Deployment & Delivery Patterns

- **Blue-Green Deployment** — Dual-environment switching, Kafka consumer group management during switchover
- **Canary Release** — Route percentage of traffic to new version, monitor error rates
- **Feature Toggle-Driven Deployment** — Deploy dark features, enable via flags
- **Database Migration with Zero Downtime** — Flyway/Liquibase, expand-contract pattern for schema changes
- **Graceful Shutdown** — Stop accepting new requests, drain Kafka consumers, complete in-flight work

---

## 12. Testing Distributed Systems

- **Contract Testing** — Spring Cloud Contract or Pact for producer/consumer API contracts
- **Testcontainers Integration Tests** — Spin up Kafka, PostgreSQL, Redis in Docker for realistic integration tests
- **Embedded Kafka Tests** — `@EmbeddedKafka` for fast unit/integration testing of Kafka producers/consumers
- **End-to-End Saga Testing** — Orchestrate multi-service test scenarios, verify compensations
- **Load & Stress Testing** — Gatling/JMeter scripts targeting your APIs, measure latency percentiles under load
- **Fault Injection Testing** — Simulate network partitions, slow consumers, broker failures

---

## 13. Advanced Distributed Algorithms

- **Raft Consensus** — Implement leader election, log replication, and commitment protocol
- **Gossip Protocol** — Epidemic-style membership and failure detection across nodes
- **Merkle Tree Sync** — Detect data inconsistencies between replicas efficiently
- **Bloom Filter** — Probabilistic membership check for deduplication or cache filtering
- **Token Bucket / Leaky Bucket** — Implement from scratch for deep understanding of rate limiting internals

---

## Next Steps

Pick any topic from this guide and build it end-to-end. Start with a multi-service Spring Boot project, use Docker Compose for Kafka + dependencies, and implement one pattern at a time. Layer patterns together as you progress — for example, combine Event Sourcing + CQRS + Saga + Distributed Tracing into a single e-commerce or banking system.
