package com.wiltech.orderservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

//@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/data/**").authenticated()
                .antMatchers("/protected/**").hasRole("USER")
                .antMatchers("**/protected/**").hasRole("USER")
                .antMatchers("orders/protected/**").hasRole("USER")
                .anyRequest().denyAll()
//                .anyRequest().permitAll()
                .and()
                .formLogin().loginPage("/web/login").permitAll()
                .and()
                .csrf().ignoringAntMatchers("/contact-email")
                .and()
                .logout().logoutUrl("/web/logout").logoutSuccessUrl("/web/").permitAll();
    }
}
