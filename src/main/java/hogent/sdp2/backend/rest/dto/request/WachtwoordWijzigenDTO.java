package hogent.sdp2.backend.rest.dto.request;

public record WachtwoordWijzigenDTO(
        String email,
        String oudWachtwoord,
        String nieuwWachtwoord
) {
}
