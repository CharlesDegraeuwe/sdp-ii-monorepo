package domain;

import hogent.sdp2.sdpii.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;

public class LogService {

    public void logActie(Werknemer werknemer,
                         String type,
                         String tabel,
                         Integer recordId,
                         String details) {

        EntityManager em = JPAUtil.getENTITY_MANAGER_FACTORY().createEntityManager();

        try {
            em.getTransaction().begin();

            Log log = new Log();
            log.setWerknemer(werknemer);
            log.setType(type);
            log.setTabel(tabel);
            log.setRecordId(recordId);
            log.setTimestamp(LocalDateTime.now());
            log.setTest(details);

            em.persist(log);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}