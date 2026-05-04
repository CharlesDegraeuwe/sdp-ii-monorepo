package hogent.sdp2.backend.REST.dto.request;

public record WachtwoordWijzigenDTO(
        String email,
        String oudWachtwoord,
        String nieuwWachtwoord
) {
}
