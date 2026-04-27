package hogent.sdp2.backend.dto.request;

import hogent.sdp2.backend.dto.response.WerknemerResponseDTO;

public record LoginResponseDTO(String token, WerknemerResponseDTO werknemer) {}