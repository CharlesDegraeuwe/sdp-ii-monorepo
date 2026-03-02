package domain.facades;

import domain.facades.entities.Werknemer;
import domain.oud.logger.LogService;
import repository.WerknemerDao;
import repository.WerknemerDaoJpa;

import java.util.List;

public class WerknemersFacade {

    private WerknemerDao repo = new WerknemerDaoJpa();
    private final LogService logService = new LogService();

    public List<Werknemer> geefAlleWerknemers() {
        return repo.geefAlleWerknemers();
    }

    public void activeerWerknemer(int werknemerId) {
        repo.updateStatus(werknemerId, "Actief");
        Werknemer werknemer = zoekOpId(werknemerId);
        logService.logActie(
                werknemer,
                "CREATE",
                "Werknemer",
                werknemerId,
                "Werknemer is geactiveerd"
        );
    }

    public void deactiveerWerknemer(int werknemerId) {
        repo.updateStatus(werknemerId, "Inactief");
        Werknemer werknemer = zoekOpId(werknemerId);
        logService.logActie(
                werknemer,
                "DELETE",
                "Werknemer",
                werknemerId,
                "Werknemer is gedeactiveerd"
        );

    }

    public Werknemer zoekOpEmailEnWachtwoord(String email, String wachtwoord) {
        return repo.zoekOpEmailEnWachtwoord(email, wachtwoord);
    }

    public Werknemer zoekOpId(int id) {
        return repo.zoekOpId(id);
    }

    public void update(Werknemer werknemer) {
        repo.update(werknemer);
        logService.logActie(
                werknemer,
                "UPDATE",
                "Werknemer",
                werknemer.getId(),
                "Werknemer is geupdate"
        );
    }
}