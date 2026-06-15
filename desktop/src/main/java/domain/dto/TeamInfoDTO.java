package domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TeamInfoDTO(
        Integer id,
        String naam,
        String beschrijving
) {}