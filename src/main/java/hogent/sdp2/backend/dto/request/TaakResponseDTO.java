package hogent.sdp2.backend.dto.request;

import java.time.LocalDate;

public record TaakResponseDTO(int id, WerknemerResponseDTO werknemer, String titel, String beschrijving, String afgewerkt, LocalDate deadline, Integer teamId, Integer siteId) {
}