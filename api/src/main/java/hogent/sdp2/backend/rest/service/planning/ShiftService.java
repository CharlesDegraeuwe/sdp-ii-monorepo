package hogent.sdp2.backend.rest.service.planning;

import hogent.sdp2.backend.domain.Shift;
import hogent.sdp2.backend.domain.Teamwerknemer;
import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.rest.dto.request.ShiftAanmakenDTO;
import hogent.sdp2.backend.rest.dto.request.ShiftAanpassenDTO;
import hogent.sdp2.backend.rest.dto.response.ShiftResponseDTO;
import hogent.sdp2.backend.rest.repository.ShiftRepository;
import hogent.sdp2.backend.rest.repository.TeamwerknemerRepository;
import hogent.sdp2.backend.rest.repository.WerknemerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final WerknemerRepository werknemerRepository;
    private final TeamwerknemerRepository teamwerknemerRepository;

    public List<ShiftResponseDTO> geefShiftenVanTeamOpDatum(Integer teamId, LocalDate datum) {
        List<Teamwerknemer> teamleden = teamwerknemerRepository.findByTeamId(teamId);
        return teamleden.stream()
                .flatMap(tw -> shiftRepository.findByWerknemer_Id(tw.getWerknemer().getId()).stream())
                .filter(s -> !datum.isBefore(s.getStartDatum()) && !datum.isAfter(s.getEindDatum()))
                .map(this::mapToDTO)
                .toList();
    }

    public List<ShiftResponseDTO> geefShiftenVanWerknemerOpDatum(Integer werknemerId, LocalDate datum) {
        return shiftRepository.findByWerknemer_Id(werknemerId).stream()
                .filter(s -> !datum.isBefore(s.getStartDatum()) && !datum.isAfter(s.getEindDatum()))
                .map(this::mapToDTO)
                .toList();
    }

    public List<ShiftResponseDTO> geefShiftenVanWerknemerInBereik(Integer werknemerId, LocalDate van, LocalDate tot) {
        return shiftRepository.findByWerknemer_Id(werknemerId).stream()
                .filter(s -> !s.getEindDatum().isBefore(van) && !s.getStartDatum().isAfter(tot))
                .map(this::mapToDTO)
                .toList();
    }

    public ShiftResponseDTO maakShift(ShiftAanmakenDTO dto) {
        Werknemer werknemer = werknemerRepository.findById(dto.werknemerId())
                .orElseThrow(() -> new IllegalArgumentException("Werknemer niet gevonden: " + dto.werknemerId()));

        boolean heeftOverlap = shiftRepository.findByWerknemer_Id(dto.werknemerId()).stream()
                .anyMatch(s -> !s.getEindDatum().isBefore(dto.startDatum()) && !s.getStartDatum().isAfter(dto.eindDatum()));
        if (heeftOverlap) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Deze werknemer heeft al een shift in deze periode.");
        }

        Shift shift = new Shift();
        shift.setWerknemer(werknemer);
        shift.setStartDatum(dto.startDatum());
        shift.setEindDatum(dto.eindDatum());
        shift.setStartTijd(dto.startTijd());
        shift.setEindTijd(dto.eindTijd());
        shift.setPauzeStart(dto.pauzeStart());
        shift.setPauzeEind(dto.pauzeEind());
        return mapToDTO(shiftRepository.save(shift));
    }

    public ShiftResponseDTO pasAan(Integer shiftId, ShiftAanpassenDTO dto) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new IllegalArgumentException("Shift niet gevonden: " + shiftId));
        shift.setStartDatum(dto.startDatum());
        shift.setEindDatum(dto.eindDatum());
        shift.setStartTijd(dto.startTijd());
        shift.setEindTijd(dto.eindTijd());
        shift.setPauzeStart(dto.pauzeStart());
        shift.setPauzeEind(dto.pauzeEind());
        return mapToDTO(shiftRepository.save(shift));
    }

    private ShiftResponseDTO mapToDTO(Shift shift) {
        Werknemer w = shift.getWerknemer();
        return new ShiftResponseDTO(
                shift.getId(),
                w.getId(),
                w.getVoornaam() + " " + w.getNaam(),
                shift.getStartDatum(),
                shift.getEindDatum(),
                shift.getStartTijd(),
                shift.getEindTijd(),
                shift.getPauzeStart(),
                shift.getPauzeEind()
        );
    }
}
