package domain.facades;

import domain.dto.UpdateWerknemerDTO;
import domain.dto.WerknemerDTO;
import domain.services.WerknemersApiService;

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
}