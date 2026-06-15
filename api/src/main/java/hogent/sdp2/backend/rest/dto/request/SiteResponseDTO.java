package hogent.sdp2.backend.rest.dto.request;

public record SiteResponseDTO(Integer id, String naam, String locatie, int capaciteit, String status) {
}