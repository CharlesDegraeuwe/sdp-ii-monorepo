package hogent.sdp2.backend.controller;

import hogent.sdp2.backend.dto.request.MachineAanmakenDTO;
import hogent.sdp2.backend.service.MachineService;
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
}