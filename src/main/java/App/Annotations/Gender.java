package App.Annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GenderValidator.class)
public @interface Gender {

    String message() default "Enter a valid gender";

    String[] gender() default {"rather not say"};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
