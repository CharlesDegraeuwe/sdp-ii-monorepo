package domain.facades;

import domain.dto.UpdateWerknemerDTO;
import domain.dto.WerknemerDTO;
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
    }

    public void deactiveerWerknemer(int werknemerId) {

    }

    public WerknemerDTO zoekOpEmail(String email) {
        return api.zoekOpEmail(email);
    }

    public WerknemerDTO zoekOpId(int id) {
        return api.zoekOpId(id);
    }


    public void update(UpdateWerknemerDTO werknemer) {
        api.update(werknemer);

    }

    public boolean veranderStatus(int werknemerId, String actie) {
        return api.veranderStatus(werknemerId, actie);
    }

    public boolean registreerWerknemer(String naam, String voornaam, String email, String telefoon, String geboortedatum, String rol) {
        return api.registreerWerknemer(naam, voornaam, email, telefoon, geboortedatum, rol);
    }
}