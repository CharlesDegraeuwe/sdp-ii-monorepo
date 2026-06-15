package hogent.sdp2.sdpii.gui.app.notificaties;

import domain.Beheerder;
import domain.auth.Sessie;
import domain.dto.NotificatieDTO;
import domain.dto.WerknemerDTO;
import domain.facades.NotificatieFacade;
import domain.services.SseListenerService;
import hogent.sdp2.sdpii.gui.components.app.PageTitleController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.List;

public class NotificatiesController extends BorderPane {
    //TODO Wrapper klasse gebruiken
    //
    @FXML private VBox ongelezenLijst;
    @FXML private VBox alleLijst;
    @FXML private VBox ongelezenSectie;
    @FXML private Label leegLabel;

    @FXML private Button allesKnop;
    @FXML private Button werkKnop;
    @FXML private Button afwezigheidKnop;
    @FXML private Button verlofKnop;

    private List<NotificatieDTO> alleNotificaties;
    private String actieveFilter = "Alles";
    private NotificatieFacade nf;
    private final Runnable sseListener = this::laadNotificaties;

    public NotificatiesController(NotificatieFacade nf) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/notificaties/NotificationsPage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.nf = nf;
        setTop(new PageTitleController("Notificaties"));

        SseListenerService.getInstance().voegListenerToe(sseListener);
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                SseListenerService.getInstance().verwijderListener(sseListener);
            }
        });
    }

    @FXML
    public void initialize() {
        laadNotificaties();
    }

    private void laadNotificaties() {
        WerknemerDTO werknemer = Sessie.getInstance().getIngelogdeWerknemer();
        if (werknemer == null) return;

        new Thread(() -> {
            List<NotificatieDTO> notificaties = nf.geefNotificaties(werknemer.id());

            Platform.runLater(() -> {
                this.alleNotificaties = notificaties;
                toonNotificaties(notificaties);
            });
        }).start();
    }

    private void toonNotificaties(List<NotificatieDTO> notificaties) {
        ongelezenLijst.getChildren().clear();
        alleLijst.getChildren().clear();

        if (notificaties.isEmpty()) {
            leegLabel.setVisible(true);
            leegLabel.setManaged(true);
            ongelezenSectie.setVisible(false);
            ongelezenSectie.setManaged(false);
            return;
        }

        leegLabel.setVisible(false);
        leegLabel.setManaged(false);

        List<NotificatieDTO> ongelezen = notificaties.stream()
                .filter(n -> n.gelezen().equals("Nee"))
                .toList();

        ongelezenSectie.setVisible(!ongelezen.isEmpty());
        ongelezenSectie.setManaged(!ongelezen.isEmpty());

        for (NotificatieDTO n : ongelezen) {
            ongelezenLijst.getChildren().add(maakNotificatieRij(n));
        }

        for (NotificatieDTO n : notificaties) {
            alleLijst.getChildren().add(maakNotificatieRij(n));
        }
    }

    private HBox maakNotificatieRij(NotificatieDTO notificatie) {
        HBox rij = new HBox(12);
        rij.getStyleClass().add("notificatie-rij");
        rij.setAlignment(Pos.CENTER_LEFT);

        Pane dot = new Pane();
        dot.getStyleClass().add(notificatie.gelezen().equals("Nee") ? "dot-ongelezen" : "dot-gelezen");

        VBox tekst = new VBox(3);
        HBox.setHgrow(tekst, Priority.ALWAYS);
        Label titel = new Label(notificatie.titel());
        titel.getStyleClass().add("notificatie-titel");
        Label bericht = new Label(notificatie.bericht());
        bericht.getStyleClass().add("notificatie-bericht");
        bericht.setWrapText(true);
        Label datum = new Label(notificatie.datum().toString());
        datum.getStyleClass().add("notificatie-datum");
        tekst.getChildren().addAll(titel, bericht, datum);

        HBox acties = new HBox(8);
        acties.setAlignment(Pos.CENTER_RIGHT);

        // Goedkeuren/afwijzen knoppen voor verlofaanvragen (manager)
        if (notificatie.titel().equals("Nieuwe verlofaanvraag") && notificatie.referentieId() != null) {
            String status = geefVerlofStatusVeilig(notificatie.referentieId());
            if ("In afwachting".equals(status)) {
                Button goedkeuren = new Button("Goedkeuren");
                goedkeuren.getStyleClass().add("goedkeuren-knop");
                Button afwijzen = new Button("Afwijzen");
                afwijzen.getStyleClass().add("afwijzen-knop");
                goedkeuren.setOnAction(e -> verwerkVerlofActie(notificatie, true, goedkeuren, afwijzen, acties));
                afwijzen.setOnAction(e -> verwerkVerlofActie(notificatie, false, goedkeuren, afwijzen, acties));
                acties.getChildren().addAll(goedkeuren, afwijzen);
            } else if ("Goedgekeurd".equals(status)) {
                Label statusLabel = new Label("✓ Goedgekeurd");
                statusLabel.getStyleClass().add("status-goedgekeurd");
                acties.getChildren().add(statusLabel);
            } else if ("Afgewezen".equals(status)) {
                Label statusLabel = new Label("✗ Afgewezen");
                statusLabel.getStyleClass().add("status-afgewezen");
                acties.getChildren().add(statusLabel);
            } else if ("Geannuleerd".equals(status)) {
                Label statusLabel = new Label("✗ Geannuleerd");
                statusLabel.getStyleClass().add("status-afgewezen");
                acties.getChildren().add(statusLabel);
            }
        }

        // Annuleren knop voor werknemer bij goedgekeurd verlof
        if (notificatie.titel().equals("Verlof goedgekeurd") && notificatie.referentieId() != null) {
            String status = geefVerlofStatusVeilig(notificatie.referentieId());
            if ("Goedgekeurd".equals(status)) {
                Button annuleren = new Button("Annuleren");
                annuleren.getStyleClass().add("afwijzen-knop");
                annuleren.setOnAction(e -> annuleerVerlof(notificatie.referentieId(), annuleren, acties));
                acties.getChildren().add(annuleren);
            } else if ("Geannuleerd".equals(status)) {
                Label statusLabel = new Label("✗ Geannuleerd");
                statusLabel.getStyleClass().add("status-afgewezen");
                acties.getChildren().add(statusLabel);
            }
        }

        // Gelezen knop
        if (notificatie.gelezen().equals("Nee")) {
            Button gelezen = new Button("✓");
            gelezen.getStyleClass().add("actie-knop");
            gelezen.setOnAction(e -> markeerGelezen(notificatie.id()));
            acties.getChildren().add(gelezen);
        }

        // Verwijder knop
        Button verwijder = new Button("🗑");
        verwijder.getStyleClass().add("actie-knop");
        verwijder.setOnAction(e -> verwijderNotificatie(notificatie.id()));
        acties.getChildren().add(verwijder);

        rij.getChildren().addAll(dot, tekst, acties);
        return rij;
    }

    private String geefVerlofStatusVeilig(int verlofId) {
        try {
            return Beheerder.getInstance().getVerlofFacade().geefVerlofStatus(verlofId);
        } catch (Exception e) {
            return "";
        }
    }

    private void verwerkVerlofActie(NotificatieDTO notificatie, boolean goedkeuren, Button goedkeurenKnop, Button afwijzenKnop, HBox acties) {
        goedkeurenKnop.setDisable(true);
        afwijzenKnop.setDisable(true);
        new Thread(() -> {
            try {
                if (goedkeuren) {
                    Beheerder.getInstance().getVerlofFacade().keurVerlofGoed(notificatie.referentieId());
                } else {
                    Beheerder.getInstance().getVerlofFacade().wijsVerlofAf(notificatie.referentieId());
                }
                Beheerder.getInstance().getNotificatieFacade().markeerAlsGelezen(notificatie.id());
                Platform.runLater(this::laadNotificaties);
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    goedkeurenKnop.setDisable(false);
                    afwijzenKnop.setDisable(false);
                });
            }
        }).start();
    }

    private void annuleerVerlof(int verlofId, Button annulerenKnop, HBox acties) {
        annulerenKnop.setDisable(true);
        new Thread(() -> {
            try {
                Beheerder.getInstance().getVerlofFacade().annuleerVerlof(verlofId);
                Platform.runLater(this::laadNotificaties);
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> annulerenKnop.setDisable(false));
            }
        }).start();
    }

    private void markeerGelezen(int notificatieId) {
        new Thread(() -> {
            Beheerder.getInstance().getNotificatieFacade().markeerAlsGelezen(notificatieId);
            Platform.runLater(this::laadNotificaties);
        }).start();
    }

    private void verwijderNotificatie(int notificatieId) {
        new Thread(() -> {
            Beheerder.getInstance().getNotificatieFacade().verwijderNotificatie(notificatieId);
            Platform.runLater(this::laadNotificaties);
        }).start();
    }

    @FXML private void filterAlles() { setFilter("Alles"); }
    @FXML private void filterWerk() { setFilter("Werk"); }
    @FXML private void filterAfwezigheid() { setFilter("Afwezigheid"); }
    @FXML private void filterVerlof() { setFilter("Verlof"); }

    private void setFilter(String filter) {
        actieveFilter = filter;

        allesKnop.getStyleClass().setAll(filter.equals("Alles") ? "filter-knop-actief" : "filter-knop");
        werkKnop.getStyleClass().setAll(filter.equals("Werk") ? "filter-knop-actief" : "filter-knop", "filter-knop-werk");
        afwezigheidKnop.getStyleClass().setAll(filter.equals("Afwezigheid") ? "filter-knop-actief" : "filter-knop", "filter-knop-afwezigheid");
        verlofKnop.getStyleClass().setAll(filter.equals("Verlof") ? "filter-knop-actief" : "filter-knop", "filter-knop-verlof");

        if (alleNotificaties == null) return;

        List<NotificatieDTO> gefilterd = switch (filter) {
            case "Werk" -> alleNotificaties.stream()
                    .filter(n -> !n.titel().contains("verlof") && !n.titel().contains("afwezig") && !n.titel().contains("Verlof") && !n.titel().contains("Afwezig"))
                    .toList();
            case "Afwezigheid" -> alleNotificaties.stream()
                    .filter(n -> n.titel().contains("afwezig") || n.titel().contains("Afwezig") || n.titel().contains("Teamlid"))
                    .toList();
            case "Verlof" -> alleNotificaties.stream()
                    .filter(n -> n.titel().contains("verlof") || n.titel().contains("Verlof"))
                    .toList();
            default -> alleNotificaties;
        };

        toonNotificaties(gefilterd);
    }
}