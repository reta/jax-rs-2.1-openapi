package com.example.config;

import java.util.Arrays;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.openapi.OpenApiFeature;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.example.resource.JaxRsApiApplication;
import com.example.resource.PeopleRestService;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = PeopleRestService.class)
public class AppConfig {
	@Autowired private PeopleRestService peopleRestService;
	
	@Bean(destroyMethod = "destroy")
	public Server jaxRsServer(Bus bus) {
		final JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();

		factory.setApplication(new JaxRsApiApplication());
		factory.setServiceBean(peopleRestService);
		factory.setProvider(new JacksonJsonProvider());
		factory.setFeatures(Arrays.asList(new OpenApiFeature()));
		factory.setBus(bus);
		factory.setAddress("/");

		return factory.create();
	}

	@Bean
	public ServletRegistrationBean cxfServlet() {
		final ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new CXFServlet(), "/api/*");
		servletRegistrationBean.setLoadOnStartup(1);
		return servletRegistrationBean;
	}
}
