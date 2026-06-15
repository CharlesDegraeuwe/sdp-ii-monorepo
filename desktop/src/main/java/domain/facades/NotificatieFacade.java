package domain.facades;

import domain.dto.NotificatieDTO;
import domain.services.NotificatieApiService;

import java.util.List;

public class NotificatieFacade {
    private NotificatieApiService api = new NotificatieApiService();

    public List<NotificatieDTO> geefNotificaties(int werknemerId) {
        return api.geefNotificaties(werknemerId);
    }

    public long geefAantalOngelezen(int werknemerId) {
        return api.geefAantalOngelezen(werknemerId);
    }

    public String markeerAlsGelezen(int notificatieId) {
        return api.markeerAlsGelezen(notificatieId);
    }

    public String verwijderNotificatie(int notificatieId) {
        return api.verwijderNotificatie(notificatieId);
    }
}