package App.Controller;

import App.Exceptions.User404;
import App.Model.Otp;
import App.Model.User;
import App.Model.UserDetails;
import App.Repository.OtpRepository;
import App.Repository.UserRepository;
import App.Services.EmailService;
import App.Services.UserService;
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
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static App.FirstLetterToUpperCase.FirstToCap;

@RestController
public class RestfulGetController {

    private final int expirationTime = 15;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OtpRepository otpRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    OtpRepository otp;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String applicationEmail;

    @GetMapping("/login/get")
    public String LoginThroughGetReq(@RequestParam() String email, @RequestParam() String password, HttpServletResponse res){

        if(userService.LoginVerification(email, password, res)) {
            res.setStatus(404);
            return "invalid email or password";
        };

        return "User now logged in";
    }

    @GetMapping("/get/users")
    public List<User> getUsers(){
        return userRepository.findByDeleted(false);
    }

    @GetMapping("/register/user")
    public String RegUser(@RequestParam String password, @RequestParam String email, HttpServletRequest req, HttpServletResponse res){
        try {

            User user = new User();
            user.setEmail(email);
            user.setGender("female");
            user.setFirstname("first");
            user.setLastname("last");
            Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
            if(existingUser.isPresent()) {
                res.setStatus(400);
                return "Email is already in use";
            }

            System.out.println(passwordEncoder.encode(password));
            user.setPassword(passwordEncoder.encode(password) );
            user.setGender(user.getGender().toLowerCase());
            user.setDeleted(false);
            user.setVerified(false);
            userRepository.save(user);

            String token = UUID.randomUUID().toString();

            Otp newOtp = new Otp(token, user.getEmail(), false);
            newOtp.setCreatedAt(new Date());
            otp.save(newOtp);

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

    @GetMapping("/newtoken")
    public String GenerateNewToken(@RequestParam String email, HttpServletResponse res, HttpServletRequest req){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            res.setStatus(400);
            return "User doesn't exist";
        }

        String token = userService.GenerateNewToken(user.get());

       return emailService.SendMailWithLink(
               "<div>Click on link to verify your account "+ "<a href=\"http://"+req.getServerName()+":"+req.getServerPort()+"/verify/user?token="+token+"\" > Verify </a> </div>",
               user.get().getEmail(),
               "Account Verification second Attempt ", "resend"
       );
//        return "Check your email for new token";
    }


    @GetMapping("/verify/user")
    public String VerifyUser(@RequestParam("token") String token, HttpServletResponse res){
        Optional<Otp> dbOtp = otpRepository.findByTokenAndUsed(token, false);
        if(dbOtp.isPresent()){

            Calendar now = Calendar.getInstance();

            Otp otp = dbOtp.get();

            if((now.getTime().getTime()-otp.getCreatedAt().getTime())/1000/60>expirationTime) {
                res.setStatus(400);
                return "Token has Expired. Try generating a new token";
            }


            String email = otp.getUseremail();
            Optional<User> user = userRepository.findByEmail(email);
            user.get().setVerified(true);
            userRepository.save(user.get());

            dbOtp.get().setUsed(true);
            otpRepository.save(dbOtp.get());

            return "User is now verified";
        }

        res.setStatus(400);
        return "invalid token";
    }

    @GetMapping("/remove/user/{id}")
    public String DeleteUser(@PathVariable("id") String id, HttpServletResponse res){

        try{
            Long userId = Long.parseLong(id);
            Optional<User> retrievedUser = userRepository.findByIdAndDeleted(userId, false);
            if (retrievedUser.isPresent()) {
                //Soft delete
                User deletedUser = retrievedUser.get();
                deletedUser.setDeleted(true);
                userRepository.save(deletedUser);
                //hard delete
//                userRepository.delete(retrievedUser.get());
                return "User has been removed";
            }
            throw new User404("User doesn't exist");
        } catch (NumberFormatException e){
//            res.setStatus(400);
            return "Enter a valid user id";
        }

    }

    @GetMapping("/get/userbyid/{id}")
    public UserDetails getUser(@PathVariable String id){
        try{
            Long dbId = Long.parseLong(id);

            Optional<User> userFound = userRepository.findByIdAndDeleted(dbId, false);

            if(userFound.isPresent()){
                User user =userFound.get();
                String email = user.getEmail();
                char[] emailChar = email.toCharArray();

                for(int i=0; i<emailChar.length;i++){
                    if(i<=3||i>=emailChar.length-10){

                       continue;
                    }
                    emailChar[i]='*';
                }

                String encryptedEmail = "";

                for (char a:emailChar) {
                    encryptedEmail+=a;
                    System.out.println(a);
                }
                user.setEmail(encryptedEmail);

                UserDetails response = new UserDetails(user.getFirstname(), user.getLastname(), user.getGender(), user.getEmail(), user.getVerified());
                return response;
            }
            throw new User404("User doesn't exist");

        } catch (NumberFormatException E){
            throw new NumberFormatException("Enter a valid digit");
        }

    }

    @GetMapping("/login/user/session")
    public String LoginWithSession(@RequestParam String email, @RequestParam String pass ,HttpServletRequest req){
        Optional<User> user = userRepository.findByEmailAndPassword(email,pass);

        if(user.isPresent()){
            HttpSession session = req.getSession(true);

            if(session.isNew()){
                session.setAttribute("email", email);
                session.setAttribute("firstname", user.get().getFirstname());
                session.setAttribute("lastname", user.get().getLastname());
//                session.setMaxInactiveInterval(60*2);

                System.out.println("new session created");
                return "User logged in successfully";
            }

            System.out.println("no new session created");
            System.out.println(session.getAttribute("email"));
            return "User already logged in/dashboard reroute";
        }
        return "Invalid email or password";
    }

    @GetMapping("/dashboard/test")
    public String testDashboard(HttpSession req){
        if(req!=null) {
            System.out.println(req.getAttribute("firstname"));
            String name = (String) req.getAttribute("firstname");
            try{
                return "Welcome "+FirstToCap(name)+" email: "+req.getAttribute("email");
            } catch (NullPointerException e){
                return "No user logged in";
            }

        }
        return "no session";
    }

    @GetMapping("/logout/session")
    public String LogoutSession(HttpSession req){
        req.invalidate();
        return "User Logout"; // reroute user to the homepage or login page
    }

    @GetMapping("/logout/cookies")
    public String LogoutSession(HttpServletRequest req, HttpServletResponse res){
        Cookie[] cookies = req.getCookies();
        if(cookies!=null){
            for (Cookie cok: cookies) {
                cok.setValue("");
                cok.setPath("/");
                cok.setMaxAge(0);
                res.addCookie(cok);
            }
            return "User Logout"; // reroute user to the homepage or login page
        }
        return "No cookies were set initially";
    }

    @GetMapping("/login/user")
    public String LoginUser(@RequestParam String email, @RequestParam String pass ,HttpServletRequest req, HttpServletResponse res){

        Optional<User> dbUser = userRepository.findByEmailAndPassword(email,pass);

        if(dbUser.isPresent()) {
            Cookie[] cookie = req.getCookies();
            if(cookie==null) {
                Cookie cookie1 = new Cookie("email", email);
                cookie1.setMaxAge(60*60*2);
                cookie1.setPath("/");

                Cookie cookie2 = new Cookie("firstname", dbUser.get().getFirstname());
                cookie2.setMaxAge(60*60*2);
                cookie2.setPath("/");

                Cookie cookie3 = new Cookie("firstname", dbUser.get().getFirstname());
                cookie3.setMaxAge(60*60*2);
                cookie3.setPath("/");

                res.addCookie(cookie1);
                res.addCookie(cookie2);
                return "user logged in/dashboard page";
            }
            return "user already in/dashboard page";
        }

        return "No such user found";

    }

    @GetMapping("getusers/name/{name}")
    public List<User> UsersByFirstname(@PathVariable("name") String arg){
        List<User> user = userRepository.findByLastnameAndDeleted(arg,false);
        if(user.isEmpty()) {throw new User404("No user with lastname: "+arg+" was found");}
        return user;
    }

    @RequestMapping(value = {"/home", "/"}, method = RequestMethod.GET)
    public String Homepage(HttpServletRequest req){
        return "Homepage";

    }

    @GetMapping({"/about","/About"})
    public String AboutPage(HttpServletRequest req){
        return "About";
    }

    @GetMapping("/admin")
    public String AdminPage(){
        return "Admin";
    }

    @GetMapping("/dashboard")
    public String dashboard (HttpServletRequest req, HttpServletResponse res){
        Cookie userEmail = new Cookie("email", "dgo@gmail.com");
        if (req.getCookies()!=null) {
            try {
                res.sendRedirect("/");
            } catch (IOException e) {
                return "Invalid user details";
            }
        }
        return "welcome to your dashboard "+ userEmail.getValue();
    }

    @GetMapping("login/{data}")
    public String LoginPage(@PathVariable("data") String type){
        ArrayList<String> database = new ArrayList<>();
        database.add(0, "Michael");
        database.add(1, "Sarah");
        database.add(2, "James");
        database.add(3, "Sandra");

        int intType = Integer.parseInt(type);

        if(type.equals("1")){
            return "Welcome "+database.get(0);
        }
        if(intType==2){
            return "Welcome "+database.get(1);
        }
        if(intType==3){
            return "Welcome "+database.get(2);
        }
        if (intType==4){
            return "Welcome "+database.get(3);
        }

        return "User doesn't exist";
    }

    @GetMapping("/request")
    public String RequestPar (@RequestParam(name = "extra") String args, @RequestParam(name = "first") String arg){
        return "Request params 1: "+args+" Request params 2: "+arg;
    }

//    @GetMapping("login/data/json/")
//    public User JsonPage(){
//        List<User>users= new ArrayList<>();
//        users.add(new User(12, "Michael", "sam", "EAV@gmail.com"));
//        users.add(new User(12, "Dennis", "Patrick", "sagvgasyagb@gmail.com"));
//        users.add(new User(12, "paul", "Betty", "7gqwvh51@yahoo.com"));
//
//        User use =new User(12, "Michael", "sam", "EAV@gmail.com");
//        return use;
////        ObjectMapper mapper = new ObjectMapper();
////        try {
////            InputStream inputStream = new FileInputStream(new File("C:\\Users\\ejimm\\Desktop\\Javascript\\March-june\\Node\\data.json"));
////            TypeReference<List<Person>> typeReference = new TypeReference<List<Person>>() {} ;
////            List<Person> liPer = mapper.readValue(inputStream, typeReference);
////
////            List<String> data = new ArrayList<>();
////            for (Person p: liPer ) {
//////				System.out.println("Name: "+p.getName());
//////				System.out.println("No: "+p.getNo());
////                data.add(p.toString());
////            }
////
////            inputStream.close();
////
////            return "data";
////
////        } catch (FileNotFoundException e) {
////            e.printStackTrace();
////        } catch (StreamReadException e) {
////            e.printStackTrace();
////        } catch (DatabindException e) {
////            e.printStackTrace();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////
////        List<Person> personError = new ArrayList<>();
////        Person error = new Person();
////        error.setName("Buffer reader error");
////        error.setNo("404");
////        personError.add(error);
//////        return  personError;
////        List<String> err = new ArrayList<>(Arrays.asList("ERROR"));
////        return "err";
//    }

    @GetMapping("login/{no2}/{no3}")
    public String UserLogin(@PathVariable("no2") String args, @PathVariable("no3") String id){
        return "UserType: "+args+" UserId: "+id;
    }

    public ArrayList<String> fruits = new ArrayList<>(Arrays.asList("Apple","Banana","Orange","Mango"));
    public ArrayList<String> phones = new ArrayList<>(Arrays.asList("Iphone 6","IPhone 7","Iphone 8","Iphone 9","Samsung A72","Samsung A52","Samsung A32","Infinix Hot 10","Samsung S21 Ultra","Samsung S21","Tecno Camon 12"));
    public  List<String> ProductError = new ArrayList<>(Arrays.asList("No such section available"));

    @GetMapping("item/{section}")
    public List<String> GetItems(@PathVariable("section") String section){
        List<Integer> numbs = new ArrayList<>(Arrays.asList(1,2,3));

        if(section.equals("phones")) {
            return phones;
        };
        if(section.equals("fruits")) {
            return fruits;
        };

        return ProductError;
    }

    @GetMapping("item/{section}/{item}")
    public String GetItem(@PathVariable("section") String section, @PathVariable("item") String item){

        if(section.equals("phones")){
            try{
                String args = FirstToCap(item);
                if(phones.contains(args)) {
                    String resJson = phones.get(phones.indexOf(args));
                    return resJson;
                }
                return "no such Phone in the inventory";
            } catch(Exception e){
                return "No such Item available";
            }
        }
        if(section.equals("fruits")){
            try{
                String args = item.toLowerCase();
                if(fruits.contains(args)) {
                    return item.toLowerCase();
                }
                return "No such Fruit in the inventory";
            } catch(Exception e){
                return "No such Item available";
            }
        }
        return "No Such Section available";
    }

}
