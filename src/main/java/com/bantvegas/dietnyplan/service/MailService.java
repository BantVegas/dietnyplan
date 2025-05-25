package com.bantvegas.dietnyplan.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Existujúca metóda na odosielanie PDF prílohy (nechaj ju)
    public void sendPdf(String toEmail, byte[] pdfBytes) {
        // ... tvoj aktuálny kód ...
    }

    // NOVÁ metóda - jednoduchý textový e-mail bez prílohy
    public void sendSimpleEmail(String toEmail, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}
