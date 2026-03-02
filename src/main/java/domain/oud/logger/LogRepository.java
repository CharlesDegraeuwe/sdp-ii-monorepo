package domain.oud.logger;

import repository.entities.Log;
import hogent.sdp2.sdpii.util.JPAUtil;
import jakarta.persistence.EntityManager;

public class LogRepository {

    //TODO herschrijf JPA repo
    public void slaLogOp(Log log) {
        EntityManager em = JPAUtil.getENTITY_MANAGER_FACTORY().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(log);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}