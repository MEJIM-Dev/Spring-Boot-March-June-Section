package App.Exceptions;

public class User404 extends RuntimeException {
    public User404(String message) {
        super(message);
    }
}
