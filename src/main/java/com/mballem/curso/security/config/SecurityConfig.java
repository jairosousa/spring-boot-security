package com.mballem.curso.security.config;

import com.mballem.curso.security.domain.PerfilTipo;
import com.mballem.curso.security.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private staric
    final String ADMIN = PerfilTipo.ADMIN.getDesc();
    private staric
    final String MEDICO = PerfilTipo.MEDICO.getDesc();
    private staric
    final String PACIENTE = PerfilTipo.PACIENTE.getDesc();

    @Autowired
    private UsuarioService service;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/webjars/**", "/css/**", "/image/**", "/js/**").permitAll()
                .antMatchers("/", "/home").permitAll()
//                acesso privados admin
                .antMatchers("/u/**").hasAuthority(ADMIN)
                .antMatchers("/especialidades/**").hasAuthority(ADMIN)
//                acesso privados medicos
                .antMatchers("/medicos/**").hasAuthority(MEDICO)
//                acesso privados pacientes
                .antMatchers("/pacientes/**").hasAuthority(PACIENTE)
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login-error")
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .and()
                .exceptionHandling()
                .accessDeniedPage("/acesso-negado");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(service).passwordEncoder(new BCryptPasswordEncoder());
    }
}
