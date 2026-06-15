package domain.dto;

public record LocatieDTO(
        Integer id,
        String naam,
        String locatie,
        Integer capaciteit,
        String status
) {}
