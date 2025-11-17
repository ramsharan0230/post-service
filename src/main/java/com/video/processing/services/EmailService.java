package com.video.processing.services;

import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.context.Context;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public EmailService(
        JavaMailSender jMailSender,
        TemplateEngine templateEngine
    ){
        this.javaMailSender = jMailSender;
        this.templateEngine = templateEngine;
    }

    public void sendMail(String to, String subject, String templateName, Map<String, Object> variables) throws MessagingException{
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        Context context = new Context();
        context.setVariables(variables);

        String htmlContent = templateEngine.process("email-templates/" + templateName, context);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        helper.addInline("logo", new ClassPathResource("static/images/logo.png"));

        javaMailSender.send(message);
    }    
}
