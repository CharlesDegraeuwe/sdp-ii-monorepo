package domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AfwezigheidAanmakenDTO(
        int werknemerId,
        LocalDate startDatum,
        LocalDate eindDatum,
        String reden,
        byte[] certificaat
) {}
