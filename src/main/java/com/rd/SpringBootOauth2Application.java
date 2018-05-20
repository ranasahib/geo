package com.rd;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.rd.utils.Constant;


@EnableZuulProxy
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, /*securedEnabled = true, */ proxyTargetClass = true)
public class SpringBootOauth2Application {
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(SpringBootOauth2Application.class, args);
	}
	
	@Bean
	public String getBaseUrl() throws UnknownHostException {
	    Constant.baseUrl =  "http://103.62.95.146:9191";
	    return Constant.baseUrl;
	}
	
	@Bean
	public DataStore getPostGisDataStore() throws IOException{
		Map<String, Object> params = new HashMap<>();
		params.put("dbtype", "postgis");
		params.put("host", "localhost");
		params.put("port", 5432);
		params.put("schema", "public");
		params.put("database", "postgres");
		params.put("user", "postgres");
		params.put("passwd", "postgres");

		return DataStoreFinder.getDataStore(params);
	}
	
	@Bean
	public CorsFilter corsFilter() {
	    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    final CorsConfiguration config = new CorsConfiguration();
	    config.setAllowCredentials(true);
	    config.addAllowedOrigin("*");
	    config.addAllowedHeader("*");
	    config.addAllowedMethod("OPTIONS");
	    config.addAllowedMethod("HEAD");
	    config.addAllowedMethod("GET");
	    config.addAllowedMethod("PUT");
	    config.addAllowedMethod("POST");
	    config.addAllowedMethod("DELETE");
	    config.addAllowedMethod("PATCH");
	    config.addAllowedMethod("X-Frame-Options");
	    source.registerCorsConfiguration("/**", config);
	    return new CorsFilter(source);
	}

}