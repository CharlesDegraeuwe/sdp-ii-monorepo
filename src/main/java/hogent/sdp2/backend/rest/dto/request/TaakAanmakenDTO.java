package hogent.sdp2.backend.rest.dto.request;

import java.time.LocalDate;

public record TaakAanmakenDTO(int werknemerId, String titel, String beschrijving, LocalDate deadline) {
}