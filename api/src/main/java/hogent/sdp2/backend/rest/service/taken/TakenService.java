package hogent.sdp2.backend.rest.service.taken;

import hogent.sdp2.backend.auth.SessieService;
import hogent.sdp2.backend.domain.Taken;
import hogent.sdp2.backend.domain.Teamwerknemer;
import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.rest.dto.request.TaakAanmakenDTO;
import hogent.sdp2.backend.rest.dto.request.TaakResponseDTO;
import hogent.sdp2.backend.rest.dto.response.WerknemerResponseDTO;
import hogent.sdp2.backend.rest.repository.SiteteamRepository;
import hogent.sdp2.backend.rest.repository.TakenRepository;
import hogent.sdp2.backend.rest.repository.TeamwerknemerRepository;
import hogent.sdp2.backend.rest.repository.WerknemerRepository;
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
        taak.setWerknemer(werknemer);
        taak.setTitel(dto.titel());
        taak.setBeschrijving(dto.beschrijving());
        taak.setAfgewerkt("nee");
        taak.setDeadline(dto.deadline());

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
        log.info("Werknemer {} heeft teams: {}", w.getId(), teamwerknemers);
        Integer teamId = teamwerknemers.isEmpty() ? null : teamwerknemers.get(0).getTeam().getId();

        List<Integer> siteIds =
                teamId != null ? siteteamRepository.findSiteIdsByTeamId(teamId) : List.of();
        log.info("Team {} heeft sites: {}", teamId, siteIds);
        Integer siteId = siteIds.isEmpty() ? null : siteIds.get(0);

        return new TaakResponseDTO(
                taak.getId(),
                new WerknemerResponseDTO(
                        w.getId(),
                        w.getNaam(),
                        w.getVoornaam(),
                        w.getEmail(),
                        w.getTelefoonnummer(),
                        w.getGeboortedatum(),
                        w.getRol(),
                        w.getStatus()),
                taak.getTitel(),
                taak.getBeschrijving(),
                taak.getAfgewerkt(),
                taak.getDeadline(),
                teamId,
                siteId);
    }
}
