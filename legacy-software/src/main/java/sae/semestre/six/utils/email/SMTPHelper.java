package sae.semestre.six.utils.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Properties;
import java.util.Date;

public class SMTPHelper {
    
    private static SMTPHelper instance;
    private final JavaMailSender mailSender;
    
    @Autowired
    private IFailedEmailDao failedEmailDao;
    
    private SMTPHelper() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("smtp.gmail.com");
        sender.setPort(587);
        sender.setUsername("hospital.system@gmail.com");
        sender.setPassword("hospital123!");
        
        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        
        this.mailSender = sender;
    }
    
    public static SMTPHelper getInstance() {
        if (instance == null) {
            instance = new SMTPHelper();
        }
        return instance;
    }
    
    private void sendEmailInternal(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hospital.system@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
        System.out.println("Email sent successfully");
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            sendEmailInternal(to, subject, body);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            saveFailedEmail(to, subject, body);
        }
    }

    private void saveFailedEmail(String to, String subject, String body) {
        try {
            FailedEmail failedEmail = new FailedEmail();
            failedEmail.setRecipient(to);
            failedEmail.setSubject(subject);
            failedEmail.setBody(body);
            failedEmailDao.save(failedEmail);
        } catch (Exception e) {
            System.err.println("Failed to save failed email: " + e.getMessage());
        }
    }

    @Scheduled(fixedRate = 300000) // Toutes les 5 minutes
    public void retryFailedEmails() {
        for (FailedEmail failedEmail : failedEmailDao.findEmailsToRetry()) {
            if (failedEmail.getRetryCount() >= 3) {
                System.out.println("Skipping email ID " + failedEmail.getId() + " after 3 retries");
                continue; // Ne pas réessayer après 3 échecs
            }

            try {
                sendEmailInternal(failedEmail.getRecipient(), failedEmail.getSubject(), failedEmail.getBody());
                failedEmailDao.delete(failedEmail); // Email envoyé avec succès, on le supprime 
                System.out.println("Retry successful for failed email ID: " + failedEmail.getId());
            } catch (Exception e) {
                System.err.println("Failed to retry email ID " + failedEmail.getId() + ": " + e.getMessage());
                failedEmail.setRetryCount(failedEmail.getRetryCount() + 1);
                failedEmail.setLastRetry(new Date());
                failedEmailDao.save(failedEmail);
            }
        }
    }
}