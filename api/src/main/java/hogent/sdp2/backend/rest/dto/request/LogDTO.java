package hogent.sdp2.backend.rest.dto.request;

import hogent.sdp2.backend.domain.Werknemer;
import java.time.LocalDateTime;

public record LogDTO(
        Integer id,
        Integer werknemerId,
        String type,
        String tabel,
        LocalDateTime timestamp,
        String beschrijving) {

    public LogDTO(
            Integer id,
            Werknemer werknemer,
            String type,
            String tabel,
            Integer recordId,
            LocalDateTime timestamp,
            String beschrijving) {
        this(id, werknemer != null ? werknemer.getId() : null, type, tabel, timestamp, beschrijving);
    }
}
