package com.spring.getready.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.spring.getready.interceptor.AuthSuccessHandler;
import com.spring.getready.security.JwtAuthenticationEntryPoint;
import com.spring.getready.security.JwtAuthenticationFilter;
import com.spring.getready.services.CustomAuthProvider;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomAuthProvider authProvider;

	@Autowired
	private AuthSuccessHandler successHandler;

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider);
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/public/**");
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.cors()
				.and()
				.csrf().ignoringAntMatchers("/api/**") // Disable CSRF for API endpoints
				.and()
				.exceptionHandling()
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Support both session and stateless
				.and()
				.authorizeRequests()
				// API endpoints - JWT authentication
				.antMatchers("/api/auth/**").permitAll()
				.antMatchers("/api/admin/**").hasRole("ADMIN")
				.antMatchers("/api/jobs/**", "/api/applications/**", "/api/interviews/**").hasAnyRole("ADMIN", "RECRUITER", "CANDIDATE", "USER")
				.antMatchers("/api/**").authenticated()
				// Traditional endpoints - Session authentication
				.antMatchers("/", "/login", "/error", "/health").permitAll()
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/recruitment/**").hasAnyRole("ADMIN", "RECRUITER", "CANDIDATE", "USER")
				.antMatchers("/home/**").hasAnyRole("USER", "CANDIDATE", "RECRUITER", "ADMIN")
				.anyRequest().authenticated()
				.and()
				.formLogin().loginPage("/login").successHandler(successHandler).permitAll()
				.and()
				.logout().clearAuthentication(true).invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll();

		// Add JWT filter before UsernamePasswordAuthenticationFilter
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
