package hogent.sdp2.backend.dto.request;

public record TeamLidRequestDTO(
        int werknemerId,
        boolean isSupervisor
) {}