package hogent.sdp2.backend.dto.request;

import java.util.List;

public record CreateTeamRequestDTO(
        String naam,
        String beschrijving,
        Integer managerId,
        Integer siteId,
        List<TeamLidRequestDTO> leden
) {}