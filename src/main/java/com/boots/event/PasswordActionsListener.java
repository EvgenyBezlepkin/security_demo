package com.boots.event;

import com.boots.entity.User;
import com.boots.service.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.mail.javamail.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.UUID;

@Component
public class PasswordActionsListener implements  ApplicationListener<PasswordActionsEvent> {

    private final UserService service;
    private final JavaMailSender mailSender;

    public PasswordActionsListener(UserService service, JavaMailSender mailSender) {
        this.service = service;
        this.mailSender = mailSender;
    }

    @Override
    public void onApplicationEvent(PasswordActionsEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(PasswordActionsEvent event) {
        User user = event.getUser();
        EventType eventType = event.getEventType();
        String recipientAddress = user.getEmail();

        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user, token);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        switch (eventType) {
            case REGISTRATION: {
                String confirmationUrl = event.getAppUrl() + "/registrationConfirm?token=" + token;
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
                try {
                    helper.setTo(recipientAddress);
                    helper.setSubject("Registration Confirmation");
                    helper.setText("message.regSucc" + "\r\n" + "http://localhost:8080" + confirmationUrl);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
            case FORGOT_PASSWORD:{
                String confirmationUrl2 = event.getAppUrl() + "/forgotPassword?token=" + token;
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
                try {
                    helper.setTo(recipientAddress);
                    helper.setSubject("Forgot password confirmation");
                    helper.setText("message.regSucc" + "\r\n" + "http://localhost:8080" + confirmationUrl2);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
        mailSender.send(mimeMessage);
    }
}
