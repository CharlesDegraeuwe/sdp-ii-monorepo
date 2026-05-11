package hogent.sdp2.backend.rest.dto.request;

import hogent.sdp2.backend.rest.dto.response.WerknemerResponseDTO;

public record LoginResponseDTO(String token, WerknemerResponseDTO werknemer) {}