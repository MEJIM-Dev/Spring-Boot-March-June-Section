package App.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String myMail;

    public String Sendmail(String msg, String from, String to, String heading){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText(msg);
        message.setSubject(heading);
        message.setTo(to);
        message.setFrom(myMail);

        mailSender.send(message);
        return "Mail sent";
    }

    public String SendMailWithLink(String msg, String to, String subject, String type) {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,"utf-8");

        try{

            helper.setText(msg,true);//("<div>Click on link to verify your account "+ "<a href=\"http://"+req.getServerName()+":"+req.getServerPort()+"/verify/user?token="+token+"\" > Verify </a> </div>", true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(myMail);

            mailSender.send(message);
        } catch (MessagingException e){
            System.out.println(e.getMessage());
            return "error while constructing mail";
        } catch (MailException e){
            System.out.println(e.getMessage());
            if(type=="resend"){
                return "Token has been generated, but couldn't send verification email ATM";
            }
            return "User created but couldn't send verification email ATM";
        }

        return "Check your email for new Token";

    }
}
