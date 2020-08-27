package ninckblokje.poc.db.poller;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;

@SpringBootApplication
@Slf4j
public class PocDbPollerR2dbcApplication {

    public static void main(String[] args) {
        SpringApplication.run(PocDbPollerR2dbcApplication.class, args);
    }

    @Bean
    public ConnectionFactoryInitializer sqlServerConnectionFactoryInitializer(
            ConnectionFactory cf,
            @Value("${poc.schema}") String schema
    ) {
        log.info("Creating schema using {}", schema);
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(cf);
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource(schema));
        initializer.setDatabasePopulator(populator);
        return initializer;
    }
}
