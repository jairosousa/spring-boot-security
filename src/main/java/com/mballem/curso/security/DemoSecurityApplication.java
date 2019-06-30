package com.mballem.curso.security;

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
	JavaMailSender sender;

	@Override
	public void run(String... args) throws Exception {
		SimpleMailMessage simple = new SimpleMailMessage();
		simple.setTo("jaironsousa@gmail.com"); //email destinatario
		simple.setText("Teste numero 1"); //texto da mensagem
		simple.setSubject("Teste 1"); // Assunto da mensagem

		sender.send(simple);
	}
}
