package App.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails {
    private String firstname;
    private String lastname;
    private String gender;
    private String email;
    private boolean verified;

}
