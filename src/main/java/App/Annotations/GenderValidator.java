package App.Annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;

public class GenderValidator implements ConstraintValidator<Gender, String> {

    private String[] listofgen;

    @Override
    public void initialize(Gender gender) {
        this.listofgen = gender.gender();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s.isEmpty()) {
            return false;
        }

        for (String role:listofgen) {
            if(s.equalsIgnoreCase(role)) {
                return true;
            }
        }

        return false;
    }
}
