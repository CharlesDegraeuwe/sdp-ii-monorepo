package domain.dto;

import java.util.List;

public record TeamMetLedenDTO(int teamId, String teamNaam, List<WerknemerDTO> leden) {}
