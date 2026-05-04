package hogent.sdp2.backend.REST.dto.request;

public record LogRequestDTO(
        Integer id,
        Integer werknemerId,
        String type,
        String tabel,
        Integer recordId,
        String beschrijving
) {}