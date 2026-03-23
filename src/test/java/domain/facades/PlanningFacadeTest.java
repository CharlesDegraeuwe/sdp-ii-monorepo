package domain.facades;

import domain.dto.AfwezigheidsOverzichtDTO;
import domain.services.PlanningApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanningFacadeTest {

    @Mock
    private PlanningApiService api;

    @InjectMocks
    private PlanningFacade facade;

    private final LocalDate van = LocalDate.now();
    private final LocalDate tot = LocalDate.now().plusMonths(1);

    @Test
    void geefAfwezighedenVanTeam_delegeertNaarApi() {
        List<AfwezigheidsOverzichtDTO> verwacht = List.of();
        when(api.geefAfwezighedenVanTeam(5, van, tot)).thenReturn(verwacht);

        List<AfwezigheidsOverzichtDTO> result = facade.geefAfwezighedenVanTeam(5, van, tot);

        assertEquals(verwacht, result);
        verify(api).geefAfwezighedenVanTeam(5, van, tot);
    }

    @Test
    void geefAlleAfwezigheden_delegeertNaarApi() {
        List<AfwezigheidsOverzichtDTO> verwacht = List.of();
        when(api.geefAlleAfwezigheden(van, tot)).thenReturn(verwacht);

        List<AfwezigheidsOverzichtDTO> result = facade.geefAlleAfwezigheden(van, tot);

        assertEquals(verwacht, result);
        verify(api).geefAlleAfwezigheden(van, tot);
    }

    @Test
    void geefAfwezighedenVanSpecifiekTeam_delegeertNaarApi() {
        List<AfwezigheidsOverzichtDTO> verwacht = List.of();
        when(api.geefAfwezighedenVanSpecifiekTeam(3, van, tot)).thenReturn(verwacht);

        List<AfwezigheidsOverzichtDTO> result = facade.geefAfwezighedenVanSpecifiekTeam(3, van, tot);

        assertEquals(verwacht, result);
        verify(api).geefAfwezighedenVanSpecifiekTeam(3, van, tot);
    }
}
