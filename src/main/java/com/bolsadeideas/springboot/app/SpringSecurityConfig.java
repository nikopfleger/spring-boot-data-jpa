package com.bolsadeideas.springboot.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.bolsadeideas.springboot.app.auth.handler.LoginSuccessHandler;
import com.bolsadeideas.springboot.app.models.service.JpaUserDetailsService;

//PARA USAR LAS ANOTACIONES DE SEGURIDAD
//OTRA FORMA
//SI QUIERO USAR @PreAuthorize("hasRole('')") tengo que poner prePostEnabled=truem ademas del securedEnabled
@EnableGlobalMethodSecurity(securedEnabled=true)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private LoginSuccessHandler successHandler;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private JpaUserDetailsService userDetailsService;
	


	// SOURCE OVERRIDE CONFIGURE
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// RUTAS PUBLICAS con permit all, hasAnyRole todo lo anterior de antmatchers
		// SPRING SECURITY VALIDA CON UN INTERCEPTOR SI TENGO PERMISOS
		// DESDE EL AND ES IMPLEMENTACION DEL FORMULARIO
		http.authorizeRequests().antMatchers("/", "/css/**", "/js/**", "/images/**", "/listar**", "/locale", "/api/clientes/**").permitAll()
		.antMatchers("/uploads/**").hasAnyRole("USER") //ESTE NO LO PUEDO PONER PORQUE NO USE EL OTRO METODO
				/*.antMatchers("/ver/**").hasAnyRole("USER")				
				.antMatchers("/form/**").hasAnyRole("ADMIN").antMatchers("/eliminar/**").hasAnyRole("ADMIN")
				.antMatchers("/factura/**").hasAnyRole("ADMIN") LO HACEMOS CON ANOTACIONES*/
				.anyRequest().authenticated()
				.and().formLogin().successHandler(successHandler)				
				.loginPage("/login").permitAll().and().logout().permitAll()
				.and().exceptionHandling().accessDeniedPage("/error_403");

	}

	@Autowired
	public void configurerGlobal(AuthenticationManagerBuilder builder) throws Exception {

		builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
		
	}

}
