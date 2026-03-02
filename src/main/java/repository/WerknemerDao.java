package repository;

import domain.werknemer.Werknemer;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

public interface WerknemerDao extends GenericDao<Werknemer> {


    public List<Werknemer> geefAlleWerknemers() throws EntityNotFoundException;

    public void updateStatus(int werknemerId, String nieuweStatus) throws EntityNotFoundException;

    public Werknemer zoekOpEmailEnWachtwoord(String email, String wachtwoord) throws EntityNotFoundException;

    public Werknemer zoekOpId(int id) throws EntityNotFoundException;

    public Werknemer update(Werknemer werknemer) throws EntityNotFoundException;
}
