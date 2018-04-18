package baobab.notebookfx.config;

import java.util.Hashtable;
import java.util.Map;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "baobab.notebookfx.repositories",
        entityManagerFactoryRef = "entityManagerFactoryBean",
        transactionManagerRef = "jpaTransactionManager"
)
@ComponentScan(basePackages = "baobab")
public class AppConfiguration {

    @Bean
    public DataSource springJpaDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:./notebookDB;AUTO_SERVER=TRUE");
        dataSource.setUsername("");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        Map<String, Object> properties = new Hashtable<>();

        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");

        // enable auto creating database file (start with -Ddatabase.create=true)
        if (Boolean.getBoolean("database.create") || Boolean.getBoolean("database.debug")) {
            if (Boolean.getBoolean("database.create")) {
                properties.put("javax.persistence.schema-generation.database.action", "create");
            }
            properties.put("hibernate.format_sql", true);
            properties.put("hibernate.use_sql_comments", true);
            properties.put("hibernate.use_sql_comments", true);
            // import.sql import automaticaly or list of files sql (same location)
            //properties.put("hibernate.hbm2ddl.import_files", "import_1.sql,import_2.sql");
            adapter.setShowSql(true);
        } else {
            properties.put("javax.persistence.schema-generation.database.action", "none");
        }
        //----------------------------------------------------------------------
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(adapter);
        factory.setDataSource(this.springJpaDataSource());
        factory.setPackagesToScan("baobab.notebookfx.models");
        factory.setSharedCacheMode(SharedCacheMode.ENABLE_SELECTIVE);
        factory.setValidationMode(ValidationMode.NONE);
        factory.setJpaPropertyMap(properties);

        return factory;
    }

    @Bean
    public PlatformTransactionManager jpaTransactionManager() {
        return new JpaTransactionManager(this.entityManagerFactoryBean().getObject());
    }

//    @Bean
//    public PageState pageState() {
//        return new PageState();
//    }
}
