package dev.davidvieira.blog.postgresql.rewritebatchedinserts.postgresqlrewritebatchedinsertstest;

import dev.davidvieira.blog.postgresql.rewritebatchedinserts.postgresqlrewritebatchedinsertstest.config.TestcontainersConfiguration;
import dev.davidvieira.blog.postgresql.rewritebatchedinserts.postgresqlrewritebatchedinsertstest.service.PostService;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assumptions;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.BaseConsumer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.shaded.com.google.common.base.Charsets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;

@DisplayName("Testing batch insert")
public class ReWriteBatchedInsertsTest {

    public static final int INSERTS_QUANTITY = 100_000;
    public static final boolean SHOW_INTERNAL_POSTGRESQL_LOGS = false;
    public static final boolean LOG_JDBC_STATEMENTS = false;
    public static final int JDBC_BATCH_SIZE = 4096;

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ReWriteBatchedInsertsTest.class);

    @Nested
    @DisplayName("Running with reWriteBatchedInserts=true")
    @SpringBootTest
    @Import({TestcontainersConfiguration.class})
    class ReWriteBatchedInsertsTrue {
        @Autowired
        private TestComponent testComponent;
        
        @DynamicPropertySource
        static void registerPgProperties(DynamicPropertyRegistry registry) {
            registry.add("spring.jpa.properties.hibernate.jdbc.batch_size", () -> JDBC_BATCH_SIZE);
            registry.add("spring.datasource.hikari.data-source-properties", () -> "reWriteBatchedInserts=true");
            registry.add("decorator.datasource.datasource-proxy.query.enable-logging", () -> LOG_JDBC_STATEMENTS);
            registry.add("decorator.datasource.datasource-proxy.query.log-level", () -> "info");
        }

        @RepeatedTest(15)
        @DisplayName("Inserting " + INSERTS_QUANTITY + " registries")
        void testBatchInsertWithReWriteBatchedInserts(RepetitionInfo repetitionInfo) {
            testComponent.runTest();
            // ignore first 5 executions -> WARM UP PHASE
            Assumptions.assumeThat(repetitionInfo.getCurrentRepetition()).isGreaterThan(5);
        }
    }

    @Nested
    @DisplayName("Running with reWriteBatchedInserts=false")
    @SpringBootTest
    @Import({TestcontainersConfiguration.class})
    class ReWriteBatchedInsertsFalse {
        @Autowired
        private TestComponent testComponent;

        @DynamicPropertySource
        static void registerPgProperties(DynamicPropertyRegistry registry) {
            registry.add("spring.jpa.properties.hibernate.jdbc.batch_size", () -> JDBC_BATCH_SIZE);
            registry.add("spring.datasource.hikari.data-source-properties", () -> "reWriteBatchedInserts=false");
            registry.add("decorator.datasource.datasource-proxy.query.enable-logging", () -> LOG_JDBC_STATEMENTS);
            registry.add("decorator.datasource.datasource-proxy.query.log-level", () -> "info");
        }

        @RepeatedTest(15)
        @DisplayName("Inserting " + INSERTS_QUANTITY + " registries")
        void testBatchInsertWithReWriteBatchedInserts(RepetitionInfo repetitionInfo) {
            testComponent.runTest();
            // ignore first 5 executions -> WARM UP PHASE
            Assumptions.assumeThat(repetitionInfo.getCurrentRepetition()).isGreaterThan(5);
        }
    }

    @Component
    public static class TestComponent {
        @Autowired
        private PostService postService;

        @Autowired
        private EntityManager entityManager;

        @Autowired
        private PostgreSQLContainer<?> postgreSQLContainer;

        private void runTest() {
            ResetableToStringConsumer toStringConsumer = new ResetableToStringConsumer();
            if (SHOW_INTERNAL_POSTGRESQL_LOGS) {
                postgreSQLContainer.followOutput(toStringConsumer);
                postgreSQLContainer.getLogs();
                toStringConsumer.clear();
            }

            Instant start = Instant.now();
            postService.insertPosts(INSERTS_QUANTITY);
            Instant end = Instant.now();
            LOGGER.info("\n\n\nTime took to insert all registries: " + (end.toEpochMilli() - start.toEpochMilli()) + "ms");

            if (SHOW_INTERNAL_POSTGRESQL_LOGS) {
                LOGGER.info("\n\n\nPOSTGRESQL LOG:\n\n" + toStringConsumer.toUtf8String());
            }

            LOGGER.info("\n\n\nPOST_SEQ IS AT " + entityManager.createNativeQuery("select nextval('post_seq')").getSingleResult());
        }
    }
    static class ResetableToStringConsumer extends BaseConsumer<ToStringConsumer> {

        private final ByteArrayOutputStream stringBuffer = new ByteArrayOutputStream();

        @Override
        public void accept(OutputFrame outputFrame) {
            try {
                final byte[] bytes = outputFrame.getBytes();
                if (bytes != null) {
                    stringBuffer.write(bytes);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public String toUtf8String() {
            return stringBuffer.toString(Charsets.UTF_8);
        }
        
        public void clear() {
            stringBuffer.reset();
        }
    }
}
