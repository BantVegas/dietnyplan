package com.bantvegas.dietnyplan.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPdf(String toEmail, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Tvoj diétny plán");
            helper.setText("Ahoj, v prílohe nájdeš svoj 7-dňový diétny plán vo formáte PDF. 💪", false);

            InputStreamSource attachment = new ByteArrayResource(pdfBytes);
            helper.addAttachment("dietny-plan.pdf", attachment);

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("❌ Nepodarilo sa odoslať e-mail:");
            e.printStackTrace();
        }
    }
}
