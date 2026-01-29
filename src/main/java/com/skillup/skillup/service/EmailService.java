package com.skillup.skillup.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.nio.file.Files;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender; // CONFIGURADO POR application.properties
    }

    public String loadHtmlTemplate(String templateName) {
        try {
            ClassPathResource resource =
                    new ClassPathResource("templates/correos/" + templateName + ".html");

            return new String(Files.readAllBytes(resource.getFile().toPath()), "UTF-8");

        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar la plantilla: " + templateName, e);
        }
    }



    @Async
    public void sendHtmlEmail(String to, String subject, String htmlBody) {

        try {
            System.out.println("Enviando correo a " + to);

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    "UTF-8"
            );

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);


            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error enviando correo a " + to, e);
        }
    }

    public void sendWelcomeEmail(String to, String nombreUsuario) {

        String html = loadHtmlTemplate("bienvenida");

        // Reemplazar variable del HTML
        html = html.replace("{{nombre}}", nombreUsuario);

        sendHtmlEmail(to, "¡Bienvenido a SkillUp! 🎉", html);
    }
}