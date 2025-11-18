package utn.frc.piv.parcial.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utn.frc.piv.parcial.validators.ValidProposalDates;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidProposalDates
@Schema(description = "DTO para crear una nueva propuesta")
public class ProposalDTO {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 50, message = "El título no puede exceder 50 caracteres")
    @Schema(description = "Título de la propuesta", example = "Hackathon", maxLength = 50, requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    @Schema(description = "Descripción detallada de la propuesta", example = "hacemos algo?", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Fecha y hora de inicio de la propuesta (debe ser futura)", example = "2025-10-01 10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime startDate;

    @NotNull(message = "La fecha de fin es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Fecha y hora de fin de la propuesta (debe ser posterior al inicio)", example = "2025-10-15 18:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime endDate;
}