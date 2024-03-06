package dmx.springbootdb.router;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EntityScan
@EnableTransactionManagement
@RequiredArgsConstructor
public class DatabaseConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "master.datasource")
    public HikariConfig masterConfiguration() {
        return new HikariConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "slave.datasource")
    public HikariConfig slaveConfiguration() {
        return new HikariConfig();
    }

    @Bean
    public DataSource dataSourceRouter() {
        return new DatabaseRouter(new HikariDataSource(masterConfiguration()), new HikariDataSource(slaveConfiguration()));
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(dataSourceRouter()).packages("dmx.springbootdb").build();
    }

    @Bean(name = "jpaTxManager")
    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(@Qualifier("jpaTxManager") PlatformTransactionManager ptm) {
        return new ReplicaAwareTransactionManager(ptm);
    }
}
