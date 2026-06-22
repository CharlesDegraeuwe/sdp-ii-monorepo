package hogent.sdp2.backend.rest.dto.request;

import hogent.sdp2.backend.domain.Werknemer;
import java.time.LocalDateTime;

public record LogDTO(
        int id,
        Werknemer werknemer,
        String type,
        String tabel,
        Integer recordId,
        LocalDateTime timestamp,
        String beschrijving) {}
