//package App.Services;
//
//import App.Model.User;
//import App.Repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Optional;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    @Autowired
//    UserRepository repo;
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        Optional<User> user = repo.findByEmail(email);
//
//        if (user.isEmpty()) throw new UsernameNotFoundException("No user Found");
//
//        User userFound = user.get();
//
//        return new org.springframework.security.core.userdetails.User(
//                "as",//userFound.getEmail(),
//                "sa",//userFound.getPassword(),
//                true,//userFound.getVerified(),
//                true,//userFound.getDeleted(),
//                true,
//                true,
//                new ArrayList<GrantedAuthority>(Arrays.asList(new SimpleGrantedAuthority("ADMIN"), new SimpleGrantedAuthority("USER")))//new ArrayList<String>(Arrays.asList("ADMIN","USER"))//list.of()//getAuthorities()
//        );
//    }
//}
