package App.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(User404.class)
    public ResponseEntity<GeneralErrorMessage> runTime(User404 user404){
        GeneralErrorMessage msg = new GeneralErrorMessage("404", user404.getMessage());
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<GeneralErrorMessage> runTime(ConstraintViolationException e){
        System.out.println(e.getMessage().split("'")[1]);
        GeneralErrorMessage msg = new GeneralErrorMessage("400", e.getMessage().split("'")[1]);
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<GeneralErrorMessage> runTime(NumberFormatException e){
        System.out.println(e.getMessage());
        GeneralErrorMessage msg = new GeneralErrorMessage("400", e.getMessage());
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<GeneralErrorMessage> runTime(SQLIntegrityConstraintViolationException e){
        System.out.println(e.getMessage());
        GeneralErrorMessage msg = new GeneralErrorMessage("400", e.getMessage());
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }
}
