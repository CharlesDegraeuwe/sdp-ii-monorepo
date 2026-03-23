package hogent.sdp2.backend.service;

import hogent.sdp2.backend.domain.Log;
import hogent.sdp2.backend.dto.request.LogRequestDTO;
import hogent.sdp2.backend.dto.response.LogResponseDTO;
import hogent.sdp2.backend.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    public LogResponseDTO slaLogOp(LogRequestDTO dto) {


        Log log = new Log();
        log.setWerknemerId(dto.werknemerId());
        log.setType(dto.type());
        log.setTabel(dto.tabel());
        log.setRecordId(dto.recordId() == null ? 1 : dto.recordId());
        log.setTimestamp(LocalDate.now());
        log.setBeschrijving(dto.beschrijving() == null ? "beschrijving" : dto.beschrijving());

        Log opgeslagen = logRepository.save(log);
        return mapNaarDTO(opgeslagen);
    }

    public List<LogResponseDTO> geefAlleLogs() {
        return logRepository.findAllByOrderByTimestampDesc()
                .stream()
                .map(this::mapNaarDTO)
                .toList();
    }

    public List<LogResponseDTO> geefLogsVanWerknemer(Integer werknemerId) {
        return logRepository.findByWerknemerIdOrderByTimestampDesc(werknemerId)
                .stream()
                .map(this::mapNaarDTO)
                .toList();
    }

    private LogResponseDTO mapNaarDTO(Log log) {
        Integer w = log.getWerknemerId();
        return new LogResponseDTO(
                log.getId(),
                w,
                String.valueOf(w),
                log.getType(),
                log.getTabel(),
                log.getRecordId(),
                log.getTimestamp(),
                log.getBeschrijving()
        );
    }
}