package com.tfg.inventariado.email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
    private JavaMailSender emailSender;
	
	public void sendEmail(String to, String subject, String text, byte[] attachmentData, String attachmentName) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            message.setFrom("inventariadohiberus@outlook.es");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            if (attachmentData != null && attachmentName != null) {
                helper.addAttachment(attachmentName, new ByteArrayResource(attachmentData));
            }

            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Manejar la excepción adecuadamente
        }
    }
}
