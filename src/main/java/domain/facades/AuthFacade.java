package domain.facades;

import domain.auth.Sessie;
import domain.dto.WerknemerDTO;
import domain.services.AuthApiService;

public class AuthFacade {
    private final AuthApiService api = new AuthApiService();

    public WerknemerDTO login(String email, String wachtwoord) {
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email is verplicht.");
        if (wachtwoord == null || wachtwoord.isBlank())
            throw new IllegalArgumentException("Wachtwoord is verplicht.");

        WerknemerDTO werknemer = api.login(email, wachtwoord);
        if (werknemer != null) {
            Sessie.getInstance().setIngelogdeWerknemer(werknemer);
        }
        return werknemer;
    }

    public boolean activeerAccount(int werknemerId, String activatieCode) {
        if (activatieCode == null || activatieCode.isBlank())
            throw new IllegalArgumentException("Activatiecode is verplicht.");
        return api.activeerAccount(werknemerId, activatieCode);
    }
}