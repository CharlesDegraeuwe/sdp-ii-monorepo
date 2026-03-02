package hogent.sdp2.sdpii;

import domain.werknemer.Werknemer;
import hogent.sdp2.sdpii.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;

public class DatabaseTest {
    public static void main(String[] args) {
        // Gebruik jouw JPAUtil om de manager op te halen
        EntityManager em = JPAUtil.getENTITY_MANAGER_FACTORY().createEntityManager();

        try {
            em.getTransaction().begin();

            Werknemer wn = new Werknemer();
            wn.setNaam("Admin");
            wn.setVoornaam("Gebruiker");
            wn.setEmail("werknemer@email.com");
            wn.setWachtwoord("werknemer123");
            wn.setTelefoonnummer("0412345678");

            // Gebruik LocalDate.of of LocalDate.now()
            wn.setGeboortedatum(LocalDate.of(1995, 5, 15));

            wn.setRol("Werknemer");
            wn.setStatus("Actief");

            em.persist(wn);
            em.getTransaction().commit();

            System.out.println("--- SUCCES ---");
            System.out.println("Werknemer opgeslagen met ID: " + wn.getId());

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Fout tijdens database operatie:");
            e.printStackTrace();
        } finally {
            em.close();
            // Sluit de factory enkel af als je applicatie volledig stopt
            JPAUtil.getENTITY_MANAGER_FACTORY().close();
        }
    }
}
