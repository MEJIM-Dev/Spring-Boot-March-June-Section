package App.Model;

public class LoginCredentials {
    private String email;

    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "Person{" +
                "no='" + email + '\'' +
                ", name='" + password + '\'' +
                '}';
    }

    public void setPassword(String pass) {
        this.password = pass;
    }
}
