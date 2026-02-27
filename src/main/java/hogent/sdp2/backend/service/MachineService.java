package hogent.sdp2.backend.service;

import hogent.sdp2.backend.domain.Machine;
import hogent.sdp2.backend.domain.Site;
import hogent.sdp2.backend.dto.request.MachineAanmakenDTO;
import hogent.sdp2.backend.repository.MachineRepository;
import hogent.sdp2.backend.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MachineService {

    private final MachineRepository machineRepository;
    private final SiteRepository siteRepository;

    public String maakMachine(MachineAanmakenDTO dto) {
        log.info("Audit: Poging tot aanmaken nieuwe machine: {}", dto.naam());

        if (machineRepository.existsByNaam(dto.naam())) {
            log.warn("Audit: Aanmaken machine mislukt. Naam {} bestaat al.", dto.naam());
            return "Fout: Er bestaat al een machine met deze naam.";
        }

        Optional<Site> siteOpt = siteRepository.findById(dto.siteId());

        if (siteOpt.isEmpty()) {
            log.warn("Audit: Aanmaken machine mislukt. Site ID {} bestaat niet.", dto.siteId());
            return "Fout: De opgegeven site bestaat niet.";
        }

        Machine nieuweMachine = new Machine();
        nieuweMachine.setNaam(dto.naam());
        nieuweMachine.setStatus(dto.status());
        nieuweMachine.setSite(siteOpt.get());

        machineRepository.save(nieuweMachine);

        log.info("Audit: Machine {} succesvol gekoppeld aan site ID {}.", dto.naam(), dto.siteId());
        return "Machine '" + dto.naam() + "' is succesvol toegevoegd aan de site!";
    }
}