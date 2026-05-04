package hogent.sdp2.backend.REST.dto.request;

public record TeamResponseDTO(
        int id,
        String naam,
        String beschrijving,
        Integer managerId,
        String managerNaam,
        Integer siteId,
        String siteNaam
) {}