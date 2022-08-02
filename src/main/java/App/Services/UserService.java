package App.Services;

import App.Exceptions.User404;
import App.Model.Otp;
import App.Model.User;
import App.Repository.OtpRepository;
import App.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    OtpRepository otpRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    public String GenerateNewToken(User user){
        String token = UUID.randomUUID().toString();
        Otp otp = new Otp(token, user.getEmail(), false );
        otp.setCreatedAt(new Date());
        otpRepository.save(otp);

        return token;
    }

    public boolean LoginVerification(String email, String password, HttpServletResponse res){
        Optional<User> user = userRepository.findByEmail(email);

        if(user.isEmpty()){
            System.out.println("wrong email");
            return true;
//            throw new User404("Invalid email or password");
        }

        if(!passwordEncoder.matches(password, user.get().getPassword())) {
            System.out.println("wrong password");
            return true;
        }

        return false;
    }
}
