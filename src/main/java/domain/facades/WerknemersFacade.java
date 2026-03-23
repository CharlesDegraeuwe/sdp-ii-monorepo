package domain.facades;

import domain.dto.UpdateWerknemerDTO;
import domain.dto.WerknemerDTO;
import domain.services.LogService;
import domain.services.WerknemersApiService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class WerknemersFacade {
    private final WerknemersApiService api = new WerknemersApiService();

    public List<WerknemerDTO> geefAlleWerknemers() {
        return api.getAlleWerknemers();
    }

    public void activeerWerknemer(String code) {
        api.activeerWerknemer(code);
        LogService.log("UPDATE", "werknemer", "Werknemer geactiveerd – code: " + code);
    }

    public void deactiveerWerknemer(int werknemerId) {
        LogService.log("UPDATE", "werknemer", "Werknemer gedeactiveerd – werknemerId: " + werknemerId);
    }

    public WerknemerDTO zoekOpEmail(String email) {
        return api.zoekOpEmail(email);
    }

    public WerknemerDTO zoekOpId(int id) {
        return api.zoekOpId(id);
    }


    public void update(UpdateWerknemerDTO werknemer) {
        api.update(werknemer);
        LogService.log("UPDATE", "werknemer", "Werknemerprofiel gewijzigd – email: " + werknemer.email());
    }

    public boolean veranderStatus(int werknemerId, String actie) {
        boolean result = api.veranderStatus(werknemerId, actie);
        LogService.log("UPDATE", "werknemer", "Status gewijzigd – werknemerId: " + werknemerId + ", actie: " + actie);
        return result;
    }

    public boolean registreerWerknemer(String naam, String voornaam, String email, String telefoon, String geboortedatum, String rol) {
        boolean result = api.registreerWerknemer(naam, voornaam, email, telefoon, geboortedatum, rol);
        LogService.log("CREATE", "werknemer", "Werknemer geregistreerd – " + voornaam + " " + naam + " (" + rol + "), email: " + email);
        return result;
    }
}