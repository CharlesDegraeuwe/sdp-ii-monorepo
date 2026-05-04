package hogent.sdp2.backend.REST.controller;

import hogent.sdp2.backend.REST.dto.request.MachineAanmakenDTO;
import hogent.sdp2.backend.REST.dto.request.MachineWijzigenDTO;
import hogent.sdp2.backend.REST.service.locatie.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/machines")
@RequiredArgsConstructor
public class MachineController {

    private final MachineService machineService;

    @PostMapping
    public String createMachine(@RequestBody MachineAanmakenDTO dto) {
        return machineService.maakMachine(dto);
    }

    @PutMapping("/{id}")
    public String updateMachine(@PathVariable Integer id, @RequestBody MachineWijzigenDTO dto) {
        return machineService.wijzigMachine(id, dto);
    }

    @DeleteMapping("/{id}")
    public String deleteMachine(@PathVariable Integer id) {
        return machineService.verwijderMachine(id);
    }

    @GetMapping("/{id}/status")
    public String getMachineStatus(@PathVariable Integer id) {
        return machineService.getMachineStatus(id);
    }

}