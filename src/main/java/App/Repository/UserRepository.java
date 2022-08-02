package App.Repository;

import App.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByIdAndDeleted(Long id, boolean del);

    List<User> findByLastnameAndDeleted(String name, boolean del);

    List<User> findByDeleted(boolean arg);

    Optional<User> findByEmailAndPassword(String e,String p);

    Optional<User> findByEmail(String email);
}
