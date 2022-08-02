package App.Controller;

import App.Model.LoginCredentials;
import App.Model.Otp;
import App.Model.User;
import App.Repository.OtpRepository;
import App.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RestController
public class RestfulPostController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/user/update/{id}")
    public String UpdateUser(@PathVariable("id") String arg,@RequestBody() User updateUser){
        try{
            Long userId = Long.parseLong(arg);
            Optional<User> oldUser = userRepository.findById(userId);
            System.out.println(oldUser.isPresent());
            if(oldUser.isPresent()) {
                updateUser.setId(userId);
                userRepository.save(updateUser);
                return "Updated successfully";
            }
            return "User doesn't exist";
//            return "Successfully updated";
        } catch (RuntimeException e){
            return "Input a valid User id";
        }

    }

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String applicationEmail;

    @Autowired
    private OtpRepository otp;

    @PostMapping("/register/user")
    public String RegUser(@RequestBody User user, HttpServletRequest req, HttpServletResponse res){
        try {

            Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
            if(existingUser.isPresent()) {
                res.setStatus(400);
                return "Email is already in use";
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()) );
            user.setGender(user.getGender().toLowerCase());
            user.setDeleted(false);
            user.setVerified(false);
            userRepository.save(user);

            String token = UUID.randomUUID().toString();

            Otp newOtp = new Otp(token, user.getEmail(), false);
            newOtp.setCreatedAt(new Date());
//            newOtp.setUsed(false);
            otp.save(newOtp);

            //send
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(applicationEmail);
//            message.setTo(user.getEmail());
//            message.setSubject("Account Verification");
//            message.setText("Click on link to verify your account "+"http://"+req.getServerName()+":"+req.getServerPort()+"/verify/user?token="+token);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            try{

                helper.setText("<div>Click on link to verify your account "+ "<a href=\"http://"+req.getServerName()+":"+req.getServerPort()+"/verify/user?token="+token+"\" > Verify </a> </div>", true);
                helper.setTo(user.getEmail());
                helper.setSubject("Account Verification");
                helper.setFrom(applicationEmail);

                mailSender.send(message);
            } catch (MessagingException e) {
                System.out.println(e.getMessage());
                return "error constructing mail";
            }
            catch (MailException e) {
                System.out.println(e.getMessage());
                return "User created but couldn't send verification email ATM";
            }

            return "Created successfully. Check your Email for Verification";
        } catch (NumberFormatException e){
            System.out.println(e.getMessage());
            return "Couldn't save your entry";
        }

    }

    @PostMapping("/session")
    public String sessi(@RequestBody LoginCredentials loginCredentials, HttpServletRequest req,HttpServletResponse res){

        Optional<User> dbUser = userRepository.findByEmailAndPassword(loginCredentials.getEmail(),loginCredentials.getPassword());

        if(dbUser.isPresent()) {
            HttpSession session = req.getSession();
            if(session==null)
            {
                session.setAttribute("email", dbUser.get().getEmail());
                session.setAttribute("firstname", dbUser.get().getFirstname());
                session.setAttribute("lastname", dbUser.get().getLastname());
                return "welcome "+session.getAttribute("firstname");
            }

            return "no new session"; ///redirect to dashboard
        }

        return "No such user found";

    }

    @PostMapping("/login/user")
    public String LoginUser(@RequestBody LoginCredentials loginCredentials, HttpServletRequest req,HttpServletResponse res){

        Optional<User> dbUser = userRepository.findByEmailAndPassword(loginCredentials.getEmail(),loginCredentials.getPassword());

        if(dbUser.isPresent()) {
            Cookie[] cookie = req.getCookies();
            if(cookie==null) {
                Cookie cookie1 = new Cookie("email", loginCredentials.getEmail());
                cookie1.setMaxAge(60*60*24);
                cookie1.setPath("/");
                res.addCookie(cookie1);
            }
            return "user logged in/dashboard page";
        }

        return "No such user found";

    }

}
