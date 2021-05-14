package com.bolsadeideas.springboot.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bolsadeideas.springboot.app.auth.handler.LoginSuccessHandler;

//PARA USAR LAS ANOTACIONES DE SEGURIDAD
//OTRA FORMA
//SI QUIERO USAR @PreAuthorize("hasRole('')") tengo que poner prePostEnabled=truem ademas del securedEnabled
@EnableGlobalMethodSecurity(securedEnabled=true)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private LoginSuccessHandler successHandler;
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// SOURCE OVERRIDE CONFIGURE
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// RUTAS PUBLICAS con permit all, hasAnyRole todo lo anterior de antmatchers
		// SPRING SECURITY VALIDA CON UN INTERCEPTOR SI TENGO PERMISOS
		// DESDE EL AND ES IMPLEMENTACION DEL FORMULARIO
		http.authorizeRequests().antMatchers("/", "/css/**", "/js/**", "/images/**", "/listar").permitAll()
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

		PasswordEncoder encoder = passwordEncoder();
		/*
		 * UserBuilder users = User.builder().passwordEncoder(password -> { return
		 * encoder.encode(password); });
		 * 
		 * UserBuilder users = User.builder().passwordEncoder(password ->
		 * encoder.encode(password));
		 * 
		 */
		// EL :: obtiene el parametro de la funcion lambda y se lo pasa al metodo
		// LOS DOS METODOS DE ARRIBA SON EQUIVALENTES, SE PUEDE USAR CUALQUIERA DE LOS 3
		// EL :: ES DE JAVA 8
		UserBuilder users = User.builder().passwordEncoder(encoder::encode);

		builder.inMemoryAuthentication().withUser(users.username("admin").password("12345").roles("ADMIN", "USER"))
				.withUser(users.username("nico").password("12345").roles("USER"));
	}

}
