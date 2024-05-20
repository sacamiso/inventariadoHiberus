package com.tfg.inventariado.email;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfig {

	@Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.office365.com");
        mailSender.setPort(25);
        mailSender.setUsername("inventariadohiberus@outlook.es");
        mailSender.setPassword("contrasenaparaelcorreo2024");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        //props.put("mail.debug", "true"); // si deseo ver como se envía paso a paso el correo, empleado para las pruebas
        props.put("mail.smtp.ssl.trust", "*");

        return mailSender;
    }
}
