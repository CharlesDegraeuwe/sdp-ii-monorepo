package domain.facades;

import domain.dto.*;
import domain.services.TeamApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamFacadeTest {

    @Mock
    private TeamApiService api;

    @InjectMocks
    private TeamFacade facade;

    private CreateTeamDTO geldigTeamDTO() {
        List<CreateTeamLidDTO> leden = List.of(
                new CreateTeamLidDTO(1, false),
                new CreateTeamLidDTO(2, false)
        );
        return new CreateTeamDTO("TeamA", "Beschrijving", 10, 1, leden);
    }

    @Test
    void getAlleTeams_delegeertNaarApi() {
        when(api.getAlleTeams()).thenReturn(List.of());

        List<TeamDTO> result = facade.getAlleTeams();

        assertNotNull(result);
        verify(api).getAlleTeams();
    }

    @Test
    void getTeamLeden_delegeertNaarApi() {
        when(api.getTeamMembers(1)).thenReturn(List.of());

        List<TeamLidDTO> result = facade.getTeamLeden(1);

        assertNotNull(result);
        verify(api).getTeamMembers(1);
    }

    @Test
    void maakTeam_metGeldigeData_roeptApiAan() {
        TeamDTO verwacht = new TeamDTO(1, "TeamA", "Beschrijving", 10, "Manager", 1, "Site");
        when(api.maakTeam(any())).thenReturn(verwacht);

        TeamDTO result = facade.maakTeam(geldigTeamDTO());

        assertEquals(verwacht, result);
        verify(api).maakTeam(any());
    }

    @Test
    void maakTeam_metLegeNaam_gooit_IllegalArgumentException() {
        CreateTeamDTO dto = new CreateTeamDTO("", "Beschrijving", 10, 1,
                List.of(new CreateTeamLidDTO(1, false)));
        assertThrows(IllegalArgumentException.class, () -> facade.maakTeam(dto));
        verifyNoInteractions(api);
    }

    @Test
    void maakTeam_metNullManagerId_gooit_IllegalArgumentException() {
        CreateTeamDTO dto = new CreateTeamDTO("TeamA", "Beschrijving", null, 1,
                List.of(new CreateTeamLidDTO(1, false)));
        assertThrows(IllegalArgumentException.class, () -> facade.maakTeam(dto));
        verifyNoInteractions(api);
    }

    @Test
    void maakTeam_metNullSiteId_gooit_IllegalArgumentException() {
        CreateTeamDTO dto = new CreateTeamDTO("TeamA", "Beschrijving", 10, null,
                List.of(new CreateTeamLidDTO(1, false)));
        assertThrows(IllegalArgumentException.class, () -> facade.maakTeam(dto));
        verifyNoInteractions(api);
    }

    @Test
    void maakTeam_metLedenLijstNull_gooit_IllegalArgumentException() {
        CreateTeamDTO dto = new CreateTeamDTO("TeamA", "Beschrijving", 10, 1, null);
        assertThrows(IllegalArgumentException.class, () -> facade.maakTeam(dto));
        verifyNoInteractions(api);
    }

    @Test
    void maakTeam_metLegeLedenLijst_gooit_IllegalArgumentException() {
        CreateTeamDTO dto = new CreateTeamDTO("TeamA", "Beschrijving", 10, 1, List.of());
        assertThrows(IllegalArgumentException.class, () -> facade.maakTeam(dto));
        verifyNoInteractions(api);
    }

    @Test
    void maakTeam_metMeerDanVierLeden_gooit_IllegalArgumentException() {
        List<CreateTeamLidDTO> vijfLeden = List.of(
                new CreateTeamLidDTO(1, false),
                new CreateTeamLidDTO(2, false),
                new CreateTeamLidDTO(3, false),
                new CreateTeamLidDTO(4, false),
                new CreateTeamLidDTO(5, false)
        );
        CreateTeamDTO dto = new CreateTeamDTO("TeamA", "Beschrijving", 10, 1, vijfLeden);
        assertThrows(IllegalArgumentException.class, () -> facade.maakTeam(dto));
        verifyNoInteractions(api);
    }

    @Test
    void maakTeam_metMeerDanEenSupervisor_gooit_IllegalArgumentException() {
        List<CreateTeamLidDTO> leden = List.of(
                new CreateTeamLidDTO(1, true),
                new CreateTeamLidDTO(2, true)
        );
        CreateTeamDTO dto = new CreateTeamDTO("TeamA", "Beschrijving", 10, 1, leden);
        assertThrows(IllegalArgumentException.class, () -> facade.maakTeam(dto));
        verifyNoInteractions(api);
    }

    @Test
    void maakTeam_metExactEenSupervisor_isGeldig() {
        List<CreateTeamLidDTO> leden = List.of(
                new CreateTeamLidDTO(1, true),
                new CreateTeamLidDTO(2, false)
        );
        CreateTeamDTO dto = new CreateTeamDTO("TeamA", "Beschrijving", 10, 1, leden);
        when(api.maakTeam(dto)).thenReturn(new TeamDTO(1, "TeamA", "Beschrijving", 10, "Manager", 1, "Site"));

        assertDoesNotThrow(() -> facade.maakTeam(dto));
    }

    @Test
    void verwijderTeam_delegeertNaarApi() {
        facade.verwijderTeam(1);
        verify(api).verwijderTeam(1);
    }

    @Test
    void verwijderLid_delegeertNaarApi() {
        facade.verwijderLid(1, 2);
        verify(api).verwijderLid(1, 2);
    }

    @Test
    void maakSupervisor_delegeertNaarApi() {
        facade.maakSupervisor(1, 2);
        verify(api).maakSupervisor(1, 2);
    }

    @Test
    void getBeschikbareWerknemers_delegeertNaarApi() {
        when(api.getBeschikbareWerknemers(1)).thenReturn(List.of());

        List<WerknemerDTO> result = facade.getBeschikbareWerknemers(1);

        assertNotNull(result);
        verify(api).getBeschikbareWerknemers(1);
    }
}
