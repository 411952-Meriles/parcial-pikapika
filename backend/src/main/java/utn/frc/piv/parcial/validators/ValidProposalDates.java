package utn.frc.piv.parcial.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProposalDatesValidator.class)
@Documented
public @interface ValidProposalDates {
    String message() default "Las fechas de la propuesta no son válidas";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}