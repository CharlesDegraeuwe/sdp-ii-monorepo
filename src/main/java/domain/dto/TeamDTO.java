package domain.dto;

public record TeamDTO(
        int id,
        String naam,
        String beschrijving,
        Integer managerId,
        String managerNaam,
        Integer siteId,
        String siteNaam
) {}