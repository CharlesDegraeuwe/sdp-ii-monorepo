package domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NotificatieDTO(
        int id,
        int werknemerId,
        String titel,
        String bericht,
        String gelezen,
        LocalDate datum,
        Integer referentieId
) {}