package domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TeamDTO(
        int id,
        String naam,
        String beschrijving,
        Integer managerId,
        String managerNaam,
        Integer siteId,
        String siteNaam
) {}