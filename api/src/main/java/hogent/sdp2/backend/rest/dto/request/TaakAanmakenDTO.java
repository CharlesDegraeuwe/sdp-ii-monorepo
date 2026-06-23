package hogent.sdp2.backend.rest.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record TaakAanmakenDTO(Integer werknemerId, String titel, String beschrijving, LocalDate deadline, LocalTime startuur, LocalTime einduur) {
}