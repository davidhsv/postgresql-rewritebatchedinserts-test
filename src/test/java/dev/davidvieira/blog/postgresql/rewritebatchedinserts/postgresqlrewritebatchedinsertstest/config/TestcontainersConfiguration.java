package dev.davidvieira.blog.postgresql.rewritebatchedinserts.postgresqlrewritebatchedinsertstest.config;

import dev.davidvieira.blog.postgresql.rewritebatchedinserts.postgresqlrewritebatchedinsertstest.ReWriteBatchedInsertsTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {
    
    // enable to show internal postgresql logs
    // that way you can see if it is using batch insert
    //, but it will slow down the tests
    public static final boolean SHOW_INTERNAL_POSTGRESQL_LOGS = ReWriteBatchedInsertsTest.SHOW_INTERNAL_POSTGRESQL_LOGS;

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        if (SHOW_INTERNAL_POSTGRESQL_LOGS) {
            return new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.2"))
                    .withCommand("postgres", "-c", "fsync=off", "-c", "log_statement=all");
        } else {
            return new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.2"));
        }
        
    }

}
