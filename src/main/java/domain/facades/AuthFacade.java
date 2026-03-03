package domain.facades;

import domain.auth.Sessie;
import domain.dto.WerknemerDTO;
import domain.services.AuthApiService;

public class AuthFacade {
    private final AuthApiService api = new AuthApiService();
    public WerknemerDTO login(String email, String wachtwoord) {
        WerknemerDTO werknemer = api.login(email, wachtwoord);
        Sessie.getInstance().setIngelogdeWerknemer(werknemer);
        return werknemer;
    }
}
