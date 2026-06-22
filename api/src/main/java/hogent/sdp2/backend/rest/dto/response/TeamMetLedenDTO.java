package hogent.sdp2.backend.rest.dto.response;

import java.util.List;

public record TeamMetLedenDTO(int teamId, String teamNaam, List<WerknemerResponseDTO> leden) {}
