package repository;

import domain.Werknemer;
import hogent.sdp2.sdpii.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class WerknemerDaoJpa extends GenericDaoJpa<Werknemer> implements WerknemerDao{

    public WerknemerDaoJpa(){
        super(Werknemer.class);
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public Werknemer zoekOpId(int id) {
        EntityManager em = JPAUtil.getENTITY_MANAGER_FACTORY().createEntityManager();
        try {
            return em.find(Werknemer.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public Werknemer update(Werknemer werknemer) {
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
        return werknemer;
    }
}
