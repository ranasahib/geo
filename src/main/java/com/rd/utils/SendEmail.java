package com.rd.utils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Component;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

@Component
public class SendEmail {

    private String SMTP_HOST_NAME = "smtp.gmail.com";
    private String FROM_EMAIL_ADDRESS = "panaceadev2@gmail.com";
    private String FROM_PASSWORD = "P@ssw0rd@123";
    private String PORT = "587";

    public boolean sendEmailJavaAPI2(String recipient, String subject, String body) {
        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.user", FROM_EMAIL_ADDRESS);
        props.put("mail.smtp.password", FROM_PASSWORD);
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.socketFactory.fallback", "true");

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL_ADDRESS, FROM_PASSWORD);
            }
        });
        try {
            MimeMessage message = new MimeMessage(session);
            Address fromAddress = new InternetAddress(FROM_EMAIL_ADDRESS);
            Address toAddress = new InternetAddress(recipient);
            message.setFrom(fromAddress);
            message.setRecipient(Message.RecipientType.TO, toAddress);
            message.setSubject(subject);
            message.setContent(body, "text/html");
            Transport transport = session.getTransport("smtp");
            transport.connect(SMTP_HOST_NAME, FROM_EMAIL_ADDRESS, FROM_PASSWORD);
            message.saveChanges();
            Transport.send(message);
            transport.close();
            return true;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean sendEmailJavaAPI(String email,String subject,String body){

    	Email from = new Email("info@thepanaceagroup.com");
        Email to = new Email(email);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);
        mail.addHeader("Priority", "Urgent");
        mail.addHeader("Priority", "High");

        SendGrid sg = new SendGrid("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");
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}
