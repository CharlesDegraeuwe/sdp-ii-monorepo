package domain.dto;

import java.time.LocalDate;

public record TaakDTO(int id, WerknemerDTO werknemer, String titel, String beschrijving, String afgewerkt, LocalDate deadline, Integer siteId, Integer teamId) {
}
