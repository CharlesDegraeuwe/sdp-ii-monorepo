package hogent.sdp2.backend.rest.service.locatie;

import hogent.sdp2.backend.domain.Machine;
import hogent.sdp2.backend.domain.Site;
import hogent.sdp2.backend.rest.dto.request.MachineAanmakenDTO;
import hogent.sdp2.backend.rest.dto.request.MachineWijzigenDTO;
import hogent.sdp2.backend.rest.repository.MachineRepository;
import hogent.sdp2.backend.rest.repository.SiteRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public String wijzigMachine(Integer id, MachineWijzigenDTO dto) {
        log.info("Audit: Poging tot wijzigen van machine met ID: {}", id);

        Optional<Machine> machineOpt = machineRepository.findById(id);

        if (machineOpt.isEmpty()) {
            log.warn("Audit: Wijzigen mislukt. Machine met ID {} bestaat niet.", id);
            return "Fout: De opgevraagde machine is niet gevonden.";
        }

        Machine machine = machineOpt.get();

        if (!machine.getNaam().equals(dto.naam()) && machineRepository.existsByNaam(dto.naam())) {
            log.warn("Audit: Wijzigen mislukt. Nieuwe naam '{}' is al in gebruik.", dto.naam());
            return "Fout: Er bestaat al een andere machine met deze naam.";
        }

        Optional<Site> siteOpt = siteRepository.findById(dto.siteId());

        if (siteOpt.isEmpty()) {
            log.warn("Audit: Wijzigen mislukt. Site ID {} bestaat niet.", dto.siteId());
            return "Fout: De opgegeven site bestaat niet.";
        }

        machine.setNaam(dto.naam());
        machine.setStatus(dto.status());
        machine.setSite(siteOpt.get());

        machineRepository.save(machine);

        log.info("Audit: Machine {} (ID: {}) succesvol gewijzigd.", dto.naam(), id);
        return "Machine '" + dto.naam() + "' is succesvol bijgewerkt!";
    }

    public String verwijderMachine(Integer id) {
        log.info("Audit: Poging tot verwijderen van machine met ID: {}", id);

        if (!machineRepository.existsById(id)) {
            log.warn("Audit: Verwijderen mislukt. Machine met ID {} bestaat niet.", id);
            return "Fout: De opgevraagde machine is niet gevonden.";
        }

        machineRepository.deleteById(id);

        log.info("Audit: Machine met ID {} is succesvol verwijderd.", id);
        return "Machine succesvol verwijderd!";
    }

    public String getMachineStatus(Integer id) {
        log.info("Audit: Ophalen status voor machine met ID: {}", id);

        Optional<Machine> machineOpt = machineRepository.findById(id);

        if (machineOpt.isEmpty()) {
            log.warn("Audit: Status ophalen mislukt. Machine met ID {} bestaat niet.", id);
            return "Fout: De opgevraagde machine is niet gevonden.";
        }

        Machine machine = machineOpt.get();

        log.info("Audit: Status van machine {} succesvol opgehaald.", machine.getNaam());

        return "De status van machine '" + machine.getNaam() + "' is: " + machine.getStatus();
    }

    public List<MachineWijzigenDTO> haalMachinesOpVoorSite(Integer siteId) {
        log.info("Audit: Machines opvragen voor site ID: {}", siteId);

        List<Machine> machines = machineRepository.findBySiteId(siteId);

        return machines.stream()
                .map(
                        machine ->
                                new MachineWijzigenDTO(
                                        machine.getNaam(), machine.getStatus(), machine.getId()))
                .collect(Collectors.toList());
    }
}
