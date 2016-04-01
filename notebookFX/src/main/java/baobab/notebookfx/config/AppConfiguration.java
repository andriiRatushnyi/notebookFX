package baobab.notebookfx.config;

import baobab.notebookfx.models.states.PageState;
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
        //dataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        //dataSource.setUrl("jdbc:derby:notebookDB");
        //dataSource.setUrl("jdbc:derby:notebookDB;create=true");
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:./notebookDB");
        dataSource.setUsername("");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        Map<String, Object> properties = new Hashtable<>();
        properties.put("javax.persistence.schema-generation.database.action", "none");
        //properties.put("javax.persistence.schema-generation.database.action", "create");
        //properties.put("hibernate.format_sql", true);
        //properties.put("hibernate.use_sql_comments", true);

        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        //adapter.setDatabasePlatform("org.hibernate.dialect.DerbyTenSevenDialect");
        adapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
        //adapter.setShowSql(true);

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
//    public Shutdown shutdown() {
//        return new Shutdown();
//    }

    @Bean
    public PageState pageState() {
        return new PageState();
    }

//    public class Shutdown {
//
//        public void close() {
//            try {
//                DriverManager.getConnection("jdbc:derby:notebookDB;shutdown=true");
//            } catch (SQLException e) { }
//        }
//    }
}
