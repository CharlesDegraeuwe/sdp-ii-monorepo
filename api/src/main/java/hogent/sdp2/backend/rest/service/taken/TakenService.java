package hogent.sdp2.backend.rest.service.taken;

import hogent.sdp2.backend.auth.SessieService;
import hogent.sdp2.backend.domain.Site; // <-- Vergeet deze import niet!
import hogent.sdp2.backend.domain.TaakWerknemer;
import hogent.sdp2.backend.domain.Taken;
import hogent.sdp2.backend.domain.Teamwerknemer;
import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.rest.dto.request.TaakAanmakenDTO;
import hogent.sdp2.backend.rest.dto.request.TaakResponseDTO;
import hogent.sdp2.backend.rest.dto.response.WerknemerResponseDTO;
import hogent.sdp2.backend.rest.repository.*;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TakenService {

    private final TakenRepository takenRepository;
    private final WerknemerRepository werknemerRepository;
    private final TeamwerknemerRepository teamwerknemerRepository;
    private final SiteteamRepository siteteamRepository;
    private final TaakWerknemerRepository taakWerknemerRepository;
    private final SiteRepository siteRepository; // <-- Toegevoegd!

    public List<TaakResponseDTO> geefTakenVanWerknemer(int werknemerId) {
        return takenRepository.findByWerknemer_Id(werknemerId).stream().map(this::toDTO).toList();
    }

    @Transactional
    public String maakTaakAan(TaakAanmakenDTO dto) {
        Werknemer werknemer =
            werknemerRepository
                .findById(dto.werknemerId())
                .orElseThrow(() -> new RuntimeException("Werknemer niet gevonden"));

        Taken taak = new Taken();
        taak.setWerknemer(werknemer); // De maker / eigenaar van de taak
        taak.setTitel(dto.titel());
        taak.setBeschrijving(dto.beschrijving());
        taak.setAfgewerkt("nee");
        taak.setDeadline(dto.deadline());

        if (dto.startuur() != null && !dto.startuur().isBlank()) {
            taak.setStartuur(java.time.LocalTime.parse(dto.startuur()));
        }
        if (dto.einduur() != null && !dto.einduur().isBlank()) {
            taak.setEinduur(java.time.LocalTime.parse(dto.einduur()));
        }

        // <-- HIER WORDT DE SITE GEKOPPELD AAN DE TAAK
        if (dto.siteId() > 0) {
            Site site = siteRepository.findById(dto.siteId())
                .orElseThrow(() -> new RuntimeException("Site niet gevonden"));
            taak.setSite(site);
        }

        takenRepository.save(taak);
        return "Taak '" + dto.titel() + "' succesvol aangemaakt";
    }

    @Transactional
    public String markeerAfgewerkt(int taakId) {
        Taken taak =
            takenRepository
                .findById(taakId)
                .orElseThrow(() -> new RuntimeException("Taak niet gevonden"));
        taak.setAfgewerkt("ja");
        takenRepository.save(taak);
        return "Taak gemarkeerd als afgewerkt";
    }

    public List<TaakResponseDTO> geefAlleTaken() {
        return takenRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional
    public String wijsTaakToe(int taakId, int werknemerId) {
        Taken taak =
            takenRepository
                .findById(taakId)
                .orElseThrow(() -> new RuntimeException("Taak niet gevonden"));
        Werknemer werknemer =
            werknemerRepository
                .findById(werknemerId)
                .orElseThrow(() -> new RuntimeException("Werknemer niet gevonden"));

        taak.setWerknemer(werknemer);
        takenRepository.save(taak);
        return "Taak '"
            + taak.getTitel()
            + "' toegewezen aan "
            + werknemer.getVoornaam()
            + " "
            + werknemer.getNaam();
    }

    @Transactional
    public String verwijderTaak(int taakId) {
        Taken taak =
            takenRepository
                .findById(taakId)
                .orElseThrow(() -> new RuntimeException("Taak niet gevonden"));
        takenRepository.delete(taak);
        return "Taak '" + taak.getTitel() + "' succesvol verwijderd";
    }

    public void assertEigenaarVanTaak(int taakId, SessieService sessieService) {
        if (sessieService.isAdminOfManager()) return;
        Taken taak =
            takenRepository
                .findById(taakId)
                .orElseThrow(() -> new RuntimeException("Taak niet gevonden"));
        if (!taak.getWerknemer().getId().equals(sessieService.getIngelogdeWerknemerId())) {
            throw new AccessDeniedException("Je kunt alleen je eigen taken als afgewerkt markeren");
        }
    }

    private TaakResponseDTO toDTO(Taken taak) {
        Werknemer w = taak.getWerknemer();

        List<Teamwerknemer> teamwerknemers = teamwerknemerRepository.findByWerknemerId(w.getId());
        Integer teamId = teamwerknemers.isEmpty() ? null : teamwerknemers.get(0).getTeam().getId();

        Integer siteId = taak.getSite() != null ? taak.getSite().getId() : null;

        String locatieNaam = taak.getSite() != null ? taak.getSite().getNaam() : "Onbekende locatie";

        List<Integer> werknemerIds = taakWerknemerRepository.findByIdTaakId(taak.getId())
            .stream()
            .map(tw -> tw.getWerknemer().getId())
            .toList();

        return new TaakResponseDTO(
            taak.getId(),
            new WerknemerResponseDTO(
                w.getId(), w.getNaam(), w.getVoornaam(), w.getEmail(),
                w.getTelefoonnummer(), w.getGeboortedatum(), w.getRol(), w.getStatus()),
            taak.getTitel(),
            taak.getBeschrijving(),
            taak.getAfgewerkt(),
            taak.getDeadline(),
            teamId,
            siteId,
            locatieNaam,
            werknemerIds,
            taak.getStartuur() != null ? taak.getStartuur().toString() : null,
            taak.getEinduur() != null ? taak.getEinduur().toString() : null
        );
    }

    @Transactional
    public void updateToewijzingen(Integer taakId, List<Integer> werknemerIds) {
        Taken taken = takenRepository.findById(taakId).orElseThrow();

        taakWerknemerRepository.deleteByIdTaakId(taakId);

        for (Integer wId : werknemerIds) {
            Werknemer werknemer = werknemerRepository.findById(wId).orElseThrow();

            TaakWerknemer toewijzing = new TaakWerknemer();
            toewijzing.setTaken(taken);
            toewijzing.setWerknemer(werknemer);

            taakWerknemerRepository.save(toewijzing);
        }
    }

    @Transactional
    public void planTaakIn(int taakId, String datum, String startuur, String einduur) {
        Taken taak = takenRepository.findById(taakId)
            .orElseThrow(() -> new IllegalArgumentException("Taak niet gevonden: " + taakId));

        System.out.println("-> DEBUG: Taak " + taakId + " verplaatsen van " + taak.getDeadline() + " naar " + datum);

        taak.setDeadline(java.time.LocalDate.parse(datum));

        // Vul de uren in voor de kalender
        taak.setStartuur(java.time.LocalTime.parse(startuur));
        taak.setEinduur(java.time.LocalTime.parse(einduur));

        takenRepository.save(taak);

        System.out.println("-> DEBUG: Taak " + taakId + " is succesvol vastgepind op " + datum + "!");
    }
}
