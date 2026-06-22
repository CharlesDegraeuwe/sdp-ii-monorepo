package hogent.sdp2.backend.rest.dto.request;

import hogent.sdp2.backend.domain.Werknemer;
import java.time.LocalDateTime;

public record LogDTO(
        Integer id,
        Integer werknemerId,
        WerknemerInfo werknemer,
        String type,
        String tabel,
        Integer recordId,
        LocalDateTime timestamp,
        String beschrijving) {

    public record WerknemerInfo(String voornaam, String naam) {}

    public LogDTO(
            Integer id,
            Werknemer werknemer,
            String type,
            String tabel,
            Integer recordId,
            LocalDateTime timestamp,
            String beschrijving) {
        this(
                id,
                werknemer != null ? werknemer.getId() : null,
                werknemer != null
                        ? new WerknemerInfo(werknemer.getVoornaam(), werknemer.getNaam())
                        : null,
                type,
                tabel,
                recordId,
                timestamp,
                beschrijving);
    }
}
