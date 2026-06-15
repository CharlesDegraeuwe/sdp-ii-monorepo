package domain;

import hogent.sdp2.sdpii.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class WerknemerRepository {

    public List<Werknemer> geefAlleWerknemers() {
        EntityManager em = JPAUtil.getENTITY_MANAGER_FACTORY().createEntityManager();
        try {
            return em.createQuery(
                    "SELECT w FROM Werknemer w", Werknemer.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Werknemer> geefWerknemersVanManager(int managerId) {
        EntityManager em = JPAUtil.getENTITY_MANAGER_FACTORY().createEntityManager();
        try {
            // Haal alle werknemers op die in hetzelfde team zitten als de manager
            return em.createQuery(
                            "SELECT DISTINCT tw.werknemer FROM Teamwerknemer tw " +
                                    "WHERE tw.id.teamId IN (" +
                                    "   SELECT tw2.id.teamId FROM Teamwerknemer tw2 " +
                                    "   WHERE tw2.id.werknemerId = :managerId" +
                                    ") AND tw.id.werknemerId <> :managerId",
                            Werknemer.class
                    ).setParameter("managerId", managerId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void updateStatus(int werknemerId, String nieuweStatus) {
        EntityManager em = JPAUtil.getENTITY_MANAGER_FACTORY().createEntityManager();
        try {
            em.getTransaction().begin();
            Werknemer w = em.find(Werknemer.class, werknemerId);
            if (w != null) {
                w.setStatus(nieuweStatus);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Werknemer zoekOpEmailEnWachtwoord(String email, String wachtwoord) {
        EntityManager em = JPAUtil.getENTITY_MANAGER_FACTORY().createEntityManager();
        try {
            return em.createQuery(
                            "SELECT w FROM Werknemer w WHERE w.email = :email AND w.wachtwoord = :wachtwoord",
                            Werknemer.class)
                    .setParameter("email", email)
                    .setParameter("wachtwoord", wachtwoord)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Werknemer zoekOpId(int id) {
        EntityManager em = JPAUtil.getENTITY_MANAGER_FACTORY().createEntityManager();
        try {
            return em.find(Werknemer.class, id);
        } finally {
            em.close();
        }
    }

    public void update(Werknemer werknemer) {
        EntityManager em = JPAUtil.getENTITY_MANAGER_FACTORY().createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(werknemer);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}