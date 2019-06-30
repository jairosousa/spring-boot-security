package com.mballem.curso.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine template;

    public void enviarPedidoDeConfirmacaoDeCadastro(String destino, String codigo) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        Context context = new Context();
        context.setVariable("titulo", "Bem vindo a clinica Spring Security");
        context.setVariable("texto", "Precisamos que confirme o seu cadastro no link abaixo");
        context.setVariable("linkConfirmacao",
                "http://localhost:8080/u/confirmacao/cadastro?codigo=" + codigo);

        String html = template.process("email/confirmacao", context);

        helper.setTo(destino);
        helper.setText(html, true);
        helper.setSubject("Confirmação de Cadastro");
        helper.setFrom("nao-responder@clinica.com.br");
        helper.addInline("logo", new ClassPathResource("/static/image/spring-security.png"));

        mailSender.send(message);
    }
}
