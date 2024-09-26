package com.demo.folder;

import com.demo.folder.config.OpenApiConfig;
import com.demo.folder.config.SecurityConfig;
import com.demo.folder.config.SpringConfig;
import com.demo.folder.config.WebConfig;
import com.demo.folder.trainsaction.TransactionFilter;
import java.util.EnumSet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import jakarta.servlet.DispatcherType;

public class App {

  public static void main(String[] args) throws Exception {
    Server server = new Server(8080);

    ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    handler.setContextPath("/");
    server.setHandler(handler);

    AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
    context.register(SpringConfig.class, WebConfig.class, SecurityConfig.class,
        OpenApiConfig.class);
    context.scan("com.demo.folder");

    DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
    ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
    handler.addServlet(servletHolder, "/");

    FilterHolder filterHolder = new FilterHolder();
    filterHolder.setFilter(new TransactionFilter());
    handler.addFilter(filterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));

    server.start();
    server.join();
  }
}
