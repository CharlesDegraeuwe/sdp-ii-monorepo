package hogent.sdp2.backend.dto.request;

import hogent.sdp2.backend.domain.Werknemer;

import java.time.LocalDateTime;

public record LogDTO(int id,
                     Werknemer werknemer,
                     String type,
                     String tabel,
                     LocalDateTime timestamp,
                     String test) {
}
