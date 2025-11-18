package utn.frc.piv.parcial.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import utn.frc.piv.parcial.dtos.ProposalDTO;

import java.time.LocalDateTime;

public class ProposalDatesValidator implements ConstraintValidator<ValidProposalDates, ProposalDTO> {

    @Override
    public void initialize(ValidProposalDates constraintAnnotation) {
    }

    @Override
    public boolean isValid(ProposalDTO proposalDTO, ConstraintValidatorContext context) {
        if (proposalDTO == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = proposalDTO.getStartDate();
        LocalDateTime endDate = proposalDTO.getEndDate();

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        // Validar que start_date sea futura
        if (startDate == null || !startDate.isAfter(now)) {
            context.buildConstraintViolationWithTemplate("La fecha de inicio debe ser futura")
                    .addPropertyNode("startDate")
                    .addConstraintViolation();
            valid = false;
        }

        // Validar que end_date sea futura al start_date
        if (endDate == null) {
            context.buildConstraintViolationWithTemplate("La fecha de fin es obligatoria")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            valid = false;
        } else if (startDate != null && !endDate.isAfter(startDate)) {
            context.buildConstraintViolationWithTemplate("La fecha de fin debe ser posterior a la fecha de inicio")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}