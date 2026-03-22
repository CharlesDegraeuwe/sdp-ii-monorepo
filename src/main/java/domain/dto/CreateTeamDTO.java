package domain.dto;

import java.util.List;

public record CreateTeamDTO(
        String naam,
        String beschrijving,
        Integer managerId,
        Integer siteId,
        List<TeamLidDTO> leden
) {}