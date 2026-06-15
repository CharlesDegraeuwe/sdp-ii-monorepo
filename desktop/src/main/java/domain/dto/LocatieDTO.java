package domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LocatieDTO(
        Integer id,
        String naam,
        String locatie,
        Integer capaciteit,
        String status
) {}
