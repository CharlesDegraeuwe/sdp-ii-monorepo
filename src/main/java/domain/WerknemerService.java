package domain;

import java.util.List;

public class WerknemerService {

    private final WerknemerRepository repo = new WerknemerRepository();

    public List<Werknemer> geefAlleWerknemers() {
        return repo.geefAlleWerknemers();
    }

    public void activeerWerknemer(int werknemerId) {
        repo.updateStatus(werknemerId, "Actief");
    }

    public void deactiveerWerknemer(int werknemerId) {
        repo.updateStatus(werknemerId, "Inactief");
    }

    public Werknemer zoekOpEmailEnWachtwoord(String email, String wachtwoord) {
        return repo.zoekOpEmailEnWachtwoord(email, wachtwoord);
    }

    public Werknemer zoekOpId(int id) {
        return repo.zoekOpId(id);
    }
}