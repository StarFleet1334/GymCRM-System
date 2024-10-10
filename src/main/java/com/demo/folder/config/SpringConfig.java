package com.demo.folder.config;

import com.demo.folder.trainsaction.RequestLoggingInterceptor;
import com.demo.folder.trainsaction.TransactionFilter;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.modelmapper.ModelMapper;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
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


  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new RequestLoggingInterceptor())
        .addPathPatterns("/**");
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

  @Bean
  public FilterRegistrationBean<TransactionFilter> loggingFilter() {
    FilterRegistrationBean<TransactionFilter> registrationBean = new FilterRegistrationBean<>();

    registrationBean.setFilter(new TransactionFilter());
    registrationBean.addUrlPatterns("/*");
    registrationBean.setOrder(1);

    return registrationBean;
  }

  public SpringConfig(Environment env) {
    this.env = env;
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
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

