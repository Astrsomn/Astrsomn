package com.astrsomn.vector.qdrant.test;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Standalone Qdrant gRPC connectivity test.
 * <p>
 * Usage via Maven:
 * <pre>
 * mvn -f Astrsomn/pom.xml compile exec:java \
 *     -pl astrsomn-plugins/astrsomn-vector/astrsomn-vector-qdrant \
 *     -Dexec.mainClass="com.astrsomn.vector.qdrant.test.QdrantConnectivityTest"
 * </pre>
 */
public final class QdrantConnectivityTest {

    private QdrantConnectivityTest() {
    }

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 6334;
        boolean useTls = args.length > 2 && Boolean.parseBoolean(args[2]);

        System.out.println("=== Qdrant gRPC Connectivity Test ===");
        System.out.printf("Target: %s:%d (TLS=%b)%n%n", host, port, useTls);

        // Step 1: test QdrantGrpcClient (exact same API as QdrantVecSourceHandler)
        System.out.println("[1] QdrantGrpcClient.newBuilder(" + host + ", " + port + ", " + useTls + ", false)");
        QdrantGrpcClient.Builder builder = QdrantGrpcClient.newBuilder(host, port, useTls, false);
        System.out.println("    Builder: " + builder);

        System.out.println("[2] Calling grpc.build() ...");
        QdrantGrpcClient grpcClient = builder.build();
        System.out.println("    QdrantGrpcClient: " + grpcClient);

        System.out.println("[3] Creating QdrantClient ...");
        QdrantClient client = new QdrantClient(grpcClient);
        System.out.println("    QdrantClient: " + client);

        System.out.println("[4] Calling listCollectionsAsync() ...");
        long t0 = System.nanoTime();
        List<String> collections = client.listCollectionsAsync().get(10, TimeUnit.SECONDS);
        long elapsed = (System.nanoTime() - t0) / 1_000_000L;

        System.out.println("    SUCCESS in " + elapsed + "ms");
        System.out.println("    Collection count: " + collections.size());
        for (String name : collections) {
            System.out.println("      - " + name);
        }

        client.close();
        System.out.println("[5] Client closed.");
        System.out.println();
        System.out.println("=== Test PASSED ===");
    }
}
