package domain.facades;

import domain.dto.GeschiedenisItemDTO;
import domain.dto.WerknemerDTO;
import domain.services.GeschiedenisApiService;

import java.util.List;

public class GeschiedenisFacade {
    private final GeschiedenisApiService api = new GeschiedenisApiService();

    public List<GeschiedenisItemDTO> geefGeschiedenisVanWerknemer(int werknemerId) {
        return api.geefGeschiedenisVanWerknemer(werknemerId);
    }

    public List<WerknemerDTO> geefTeamledenVanManager(int managerId) {
        return api.geefTeamledenVanManager(managerId);
    }
}