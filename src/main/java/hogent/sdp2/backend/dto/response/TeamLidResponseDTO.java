package hogent.sdp2.backend.dto.response;

public record TeamLidResponseDTO(
        int id,
        String naam,
        String voornaam,
        String email,
        boolean isSupervisor
) {}