package hogent.sdp2.sdpii.gui.app.overzicht.components.notificaties;

import domain.auth.Sessie;
import domain.dto.NotificatieDTO;
import domain.dto.TaakDTO;
import domain.facades.NotificatieFacade;
import domain.facades.TakenFacade;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class NotificatieController extends VBox {
    @FXML Button see_more;
    @FXML VBox item_container;
    @FXML Button tabAll;
    @FXML Button tabTaken;
    @FXML Button tabAbsence;

    public NotificatieController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/notificaties/Notifications.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(this, javafx.scene.layout.Priority.ALWAYS);
        this.init();
    }

    private void init() {
        laadAlles();

        tabAll.setOnMouseClicked(e -> {
            setActieveTab(tabAll);
            see_more.setOnMouseClicked(ev -> Router.getInstance().navigeerNaar(Scherm.NOTIFICATIES));
            laadAlles();
        });
        tabTaken.setOnMouseClicked(e -> {
            setActieveTab(tabTaken);
            see_more.setOnMouseClicked(ev -> Router.getInstance().navigeerNaar(Scherm.TAKEN));
            laadTaken();
        });
        tabAbsence.setOnMouseClicked(e -> {
            setActieveTab(tabAbsence);
            see_more.setOnMouseClicked(ev -> Router.getInstance().navigeerNaar(Scherm.NOTIFICATIES));
            laadNotificaties();
        });

        see_more.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.NOTIFICATIES));
    }

    private void setActieveTab(Button actief) {
        tabAll.getStyleClass().setAll("tab");
        tabTaken.getStyleClass().setAll("tab");
        tabAbsence.getStyleClass().setAll("tab");
        actief.getStyleClass().setAll("tab_active");
    }

    private void laadAlles() {
        item_container.getChildren().clear();
        laadTakenInContainer();
        laadNotificatiesInContainer();
    }

    private void laadNotificaties() {
        item_container.getChildren().clear();
        laadNotificatiesInContainer();
    }

    private void laadNotificatiesInContainer() {
        try {
            int werknemerId = Sessie.getInstance().getIngelogdeWerknemer().id();
            NotificatieFacade facade = new NotificatieFacade();
            List<NotificatieDTO> notificaties = facade.geefNotificaties(werknemerId);
            for (NotificatieDTO dto : notificaties) {
                item_container.getChildren().add(new NotificatieItemController(dto));
            }
        } catch (Exception e) {
            // toon lege lijst bij fout
        }
    }

    private void laadTaken() {
        item_container.getChildren().clear();
        laadTakenInContainer();
    }

    private void laadTakenInContainer() {
        try {
            TakenFacade facade = new TakenFacade();
            List<TaakDTO> taken;
            if (Sessie.getInstance().isWerknemer() || Sessie.getInstance().isSuperVisor()) {
                taken = facade.geefEigenTaken();
            } else {
                taken = facade.geefAlleTaken();
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
            taken.stream()
                    .filter(t -> !"true".equals(t.afgewerkt()))
                    .forEach(t -> {
                        String label = t.titel() + (t.deadline() != null ? "  –  " + t.deadline().format(formatter) : "");
                        item_container.getChildren().add(
                                new NotificatieItemController(label, "#E31B35", Scherm.TAKEN)
                        );
                    });
        } catch (Exception e) {
            // toon lege lijst bij fout
        }
    }
}
