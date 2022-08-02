package App.Repository;

import App.Model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp,Long> {
    Optional<Otp> findByToken(String token);

    Optional<Otp> findByTokenAndUsed(String token, boolean used);
}
