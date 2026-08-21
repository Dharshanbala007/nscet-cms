package com.nscet.cms.db.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.nscet.cms.db.repository")
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
public class DatabaseConfig {

    @Value("${db.url:jdbc:mysql://localhost:3306/nscet_cms?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata}")
    private String dbUrl;

    @Value("${db.username:root}")
    private String dbUsername;

    @Value("${db.password:1234}")
    private String dbPassword;

    @Value("${db.driver:com.mysql.cj.jdbc.Driver}")
    private String dbDriver;

    @Value("${db.pool.size:20}")
    private int poolSize;

    @Value("${db.pool.min:5}")
    private int poolMin;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setDriverClassName(dbDriver);
        config.setMaximumPoolSize(poolSize);
        config.setMinimumIdle(poolMin);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setPoolName("NSCET-CMS-Pool");
        return new HikariDataSource(config);
    }

    @Bean
    @DependsOn("flyway")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.nscet.cms.db.entity");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.connection.CharSet", "utf8mb4");
        props.put("hibernate.connection.characterEncoding", "utf8");
        props.put("hibernate.connection.useUnicode", "true");
        props.put("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        em.setJpaPropertyMap(props);

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return txManager;
    }

    @Bean
    public Flyway flyway(DataSource dataSource) {
        try (java.sql.Connection conn = dataSource.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM flyway_schema_history WHERE version = '15' OR success = 0");
        } catch (Exception ignored) {}

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .outOfOrder(true)
                .load();
        try {
            flyway.repair();
        } catch (Exception e) {
            System.err.println("[FlywayConfig] Repair error: " + e.getMessage());
        }
        flyway.migrate();
        return flyway;
    }
}
