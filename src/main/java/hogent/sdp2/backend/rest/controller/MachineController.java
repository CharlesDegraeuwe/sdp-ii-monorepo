package hogent.sdp2.backend.rest.controller;

import hogent.sdp2.backend.rest.dto.request.MachineAanmakenDTO;
import hogent.sdp2.backend.rest.dto.request.MachineWijzigenDTO;
import hogent.sdp2.backend.rest.service.locatie.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/machines")
@RequiredArgsConstructor
public class MachineController {

    private final MachineService machineService;

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @PostMapping
    public String createMachine(@RequestBody MachineAanmakenDTO dto) {
        return machineService.maakMachine(dto);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @PutMapping("/{id}")
    public String updateMachine(@PathVariable Integer id, @RequestBody MachineWijzigenDTO dto) {
        return machineService.wijzigMachine(id, dto);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @DeleteMapping("/{id}")
    public String deleteMachine(@PathVariable Integer id) {
        return machineService.verwijderMachine(id);
    }

    @GetMapping("/{id}/status")
    public String getMachineStatus(@PathVariable Integer id) {
        return machineService.getMachineStatus(id);
    }

}
