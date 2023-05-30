package com.springecomm.springecomm.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/register", "/loginUser.html").permitAll()
                .antMatchers("/seller_product_catalog.html").hasRole("SELLER")
                .antMatchers("/buyer_product_catalog.html").hasRole("BUYER")
                .and()
                .formLogin()
                .loginPage("/loginUser.html")
                .loginProcessingUrl("/loginUser")
                .successHandler((request, response, authentication) -> {
                    String targetUrl = determineTargetUrl(authentication);
                    response.sendRedirect(targetUrl);
                })
                .failureUrl("/loginUser.html?error=Invalid credentials")
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/loginUser.html?logout")
                .deleteCookies("JSESSIONID") // Remove any cookies, if needed
                .and()
                .csrf().disable();
    }


    private String determineTargetUrl(Authentication authentication) {
        // Determine the target URL based on the user's role
        if (authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("SELLER"))) {
            return "/seller/catalog";
        } else {
            return "/buyer_product_catalog";
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
