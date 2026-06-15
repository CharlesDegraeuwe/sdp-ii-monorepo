package domain.dto;

public record TeamLidDTO(
        int werknemerId,
        String naam,
        String voornaam,
        String email,
        String telefoonnummer,
        String rol,
        boolean isSupervisor,
        int teamId,
        String teamNaam,
        String teamBeschrijving,
        Integer managerId,
        String managerNaam,
        Integer siteId,
        String siteNaam
) {}