package com.demo.folder.config;


import com.demo.folder.trainsaction.AuthenticationInterceptor;
import com.demo.folder.trainsaction.RequestLoggingInterceptor;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.modelmapper.ModelMapper;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackages = "com.demo.folder")
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
public class SpringConfig implements WebMvcConfigurer {

  private final Environment env;
  @Autowired
  @Lazy
  private AuthenticationInterceptor authenticationInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new RequestLoggingInterceptor())
        .addPathPatterns("/**");
    registry.addInterceptor(authenticationInterceptor)
        .addPathPatterns("/**")  // Authenticate for all paths
        .excludePathPatterns("/api/trainee/register", "/api/trainer/register",
            "/api/training-type/create", "/api/login", "/api/trainee/all", "/api/trainer/all",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**");
  }

  @Bean
  public SpringDocConfigProperties springDocConfigProperties() {
    var springDocConfigProperties = new SpringDocConfigProperties();
    springDocConfigProperties.setPackagesToScan(List.of("com.demo.folder.controller"));
    springDocConfigProperties.setPathsToMatch(List.of("/**"));
    return springDocConfigProperties;
  }


  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }


  public SpringConfig(Environment env) {
    this.env = env;
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(
        Objects.requireNonNull(env.getProperty("spring.datasource.driver-class-name")));
    dataSource.setUrl(env.getProperty("spring.datasource.url"));
    dataSource.setUsername(env.getProperty("spring.datasource.username"));
    dataSource.setPassword(env.getProperty("spring.datasource.password"));
    return dataSource;
  }

  @Bean(name = "sessionFactory")
  public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    sessionFactory.setDataSource(dataSource);
    sessionFactory.setConfigLocation(new ClassPathResource("hibernate.cfg.xml"));
    sessionFactory.setPackagesToScan("com.demo.folder.entity.base", "com.demo.folder.entity.dto");

    Properties hibernateProperties = new Properties();
    hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    hibernateProperties.put("hibernate.show_sql", "true");
    hibernateProperties.put("hibernate.hbm2ddl.auto", "update");
    sessionFactory.setHibernateProperties(hibernateProperties);

    return sessionFactory;
  }

  @Bean
  public PlatformTransactionManager hibernateTransactionManager(SessionFactory sessionFactory) {
    HibernateTransactionManager transactionManager = new HibernateTransactionManager();
    transactionManager.setSessionFactory(sessionFactory);
    return transactionManager;
  }
}
