package hogent.sdp2.backend.REST.dto.request;

import hogent.sdp2.backend.REST.dto.response.WerknemerResponseDTO;

public record LoginResponseDTO(String token, WerknemerResponseDTO werknemer) {}