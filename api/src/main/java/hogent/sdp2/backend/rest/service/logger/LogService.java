package hogent.sdp2.backend.rest.service.logger;

import hogent.sdp2.backend.domain.Log;
import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.rest.dto.request.LogDTO;
import hogent.sdp2.backend.rest.repository.LogRepository;
import hogent.sdp2.backend.rest.repository.WerknemerRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;
    private final WerknemerRepository werknemerRepository;

    public String maakLog(LogDTO dto) {
        if (dto.werknemerId() == null || dto.type() == null || dto.tabel() == null) {
            return "Ongeldig log: verplichte velden ontbreken.";
        }

        Werknemer werknemer = werknemerRepository.findById(dto.werknemerId()).orElse(null);
        if (werknemer == null) return "Werknemer niet gevonden.";

        Log nieuweLog = new Log();
        nieuweLog.setWerknemer(werknemer);
        nieuweLog.setType(dto.type());
        nieuweLog.setTabel(dto.tabel());
        nieuweLog.setTimestamp(dto.timestamp() != null ? dto.timestamp() : LocalDateTime.now());
        nieuweLog.setBeschrijving(dto.beschrijving());

        logRepository.save(nieuweLog);

        return "Log " + dto.type() + " " + dto.tabel() + " succesvol aangemaakt";
    }

    public List<LogDTO> getAlleLogs() {
        return logRepository.findAll().stream()
                .map(l -> new LogDTO(
                        l.getId(),
                        l.getWerknemer(),
                        l.getType(),
                        l.getTabel(),
                        l.getRecordId(),
                        l.getTimestamp(),
                        l.getBeschrijving()))
                .toList();
    }

    public LogDTO getByID(Integer id) {
        return logRepository
                .findById(id)
                .map(l -> new LogDTO(
                        l.getId(),
                        l.getWerknemer(),
                        l.getType(),
                        l.getTabel(),
                        l.getRecordId(),
                        l.getTimestamp(),
                        l.getBeschrijving()))
                .orElseThrow(() -> new RuntimeException("Log niet gevonden"));
    }

    public List<LogDTO> getRecenteLogs() {
        return logRepository.findTop10ByOrderByTimestampDesc().stream()
                .map(l -> new LogDTO(
                        l.getId(),
                        l.getWerknemer(),
                        l.getType(),
                        l.getTabel(),
                        l.getRecordId(),
                        l.getTimestamp(),
                        l.getBeschrijving()))
                .toList();
    }
}
