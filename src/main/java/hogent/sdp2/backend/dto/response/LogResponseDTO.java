package hogent.sdp2.backend.dto.response;

import java.time.LocalDate;

public record LogResponseDTO(
        Integer id,
        Integer werknemerId,
        String werknemerNaam,
        String type,
        String tabel,
        Integer recordId,
        LocalDate timestamp,
        String beschrijving
) {}