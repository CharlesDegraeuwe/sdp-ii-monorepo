package domain.facades;

import domain.auth.Sessie;
import domain.dto.LoginResponseDTO;
import domain.dto.WerknemerDTO;
import domain.services.AuthApiService;

public class AuthFacade {
    private AuthApiService api = new AuthApiService();

    public void verzendLoginEmail(String email) {
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email is verplicht.");
        api.verzendLoginEmail(email);
    }

    public WerknemerDTO loginMetWachtwoord(String email, String wachtwoord) {
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email is verplicht.");
        if (wachtwoord == null || wachtwoord.isBlank())
            throw new IllegalArgumentException("Wachtwoord is verplicht.");

        LoginResponseDTO result = api.loginMetWachtwoord(email, wachtwoord);
        Sessie.getInstance().setIngelogdeWerknemer(result.werknemer());
        return result.werknemer();
    }

    public WerknemerDTO loginMetCode(String email, String code) {
        if (code == null || code.isBlank())
            throw new IllegalArgumentException("Code is verplicht.");

        LoginResponseDTO result = api.loginMetCode(email, code);
        Sessie.getInstance().setIngelogdeWerknemer(result.werknemer());
        return result.werknemer();
    }
}
