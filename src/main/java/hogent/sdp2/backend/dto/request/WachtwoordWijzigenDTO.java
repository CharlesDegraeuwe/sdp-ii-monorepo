package hogent.sdp2.backend.dto.request;

public record WachtwoordWijzigenDTO(
        String email,
        String oudWachtwoord,
        String nieuwWachtwoord
) {
}
