package com.mballem.curso.security;

import com.mballem.curso.security.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootApplication
public class DemoSecurityApplication implements CommandLineRunner {

	public static void main(String[] args) {
//		System.out.println(new BCryptPasswordEncoder().encode("root"));
		SpringApplication.run(DemoSecurityApplication.class, args);
	}

	@Autowired
	EmailService service;

	@Override
	public void run(String... args) throws Exception {
		service.enviarPedidoDeConfirmacaoDeCadastro("jaironsousa@gmail.com", "9852pool"); //email destinatario
	}
}
