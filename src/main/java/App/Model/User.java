package App.Model;

import App.Annotations.Gender;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;

//@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
//@Data

@Entity
@Table()
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "first")
    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Email
    @Column(unique = true,nullable = false)
    private String email;

    @Gender(gender = {"male","female","neutral"})
    private String gender;

    @NotEmpty
    @Size(min = 8,max =70)
    @Column(nullable = false)
    private String password;

    private boolean deleted;

    private boolean verified;

    public boolean getVerified(){
        return this.verified;
    }

    public boolean getDeleted(){
        return !this.deleted;
    }
}
