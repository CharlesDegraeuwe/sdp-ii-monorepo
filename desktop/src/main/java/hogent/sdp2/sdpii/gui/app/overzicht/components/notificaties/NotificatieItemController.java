package hogent.sdp2.sdpii.gui.app.overzicht.components.notificaties;

import domain.dto.NotificatieDTO;
import domain.facades.NotificatieFacade;
import domain.facades.VerlofFacade;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NotificatieItemController extends HBox {

    private final Runnable onRefresh;

    public NotificatieItemController(NotificatieDTO dto, Runnable onRefresh) {
        this.onRefresh = onRefresh;
        setSpacing(8);
        setPadding(new Insets(8, 10, 8, 10));
        setAlignment(Pos.TOP_LEFT);
        setMaxWidth(Double.MAX_VALUE);
        setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 10; -fx-border-color: rgba(209,213,219,0.6); -fx-border-radius: 10; -fx-border-width: 1;");

        // Colored dot
        Circle dot = new Circle(4, Color.web(dotKleur(dto.titel())));
        VBox dotBox = new VBox(dot);
        dotBox.setAlignment(Pos.TOP_CENTER);
        dotBox.setPadding(new Insets(4, 0, 0, 0));

        // Content
        VBox content = new VBox(3);
        HBox.setHgrow(content, Priority.ALWAYS);

        // Title row
        Label lblTitel = new Label(dto.titel());
        lblTitel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #1F2937;");

        Label lblBadge = badge(dto.titel());

        Label lblTijd = new Label(formatDatum(dto.datum()));
        lblTijd.setStyle("-fx-font-size: 9px; -fx-text-fill: #9CA3AF;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox titleRow = new HBox(4, lblTitel, lblBadge, spacer, lblTijd);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        // Message
        Label lblBericht = new Label(dto.bericht());
        lblBericht.setStyle("-fx-font-size: 10px; -fx-text-fill: #6B7280;");
        lblBericht.setWrapText(false);
        lblBericht.setMaxWidth(220);

        content.getChildren().addAll(titleRow, lblBericht);

        // Action buttons
        if (heeftActie(dto.titel()) && dto.referentieId() != null) {
            HBox acties = maakActieKnoppen(dto);
            content.getChildren().add(acties);
        }

        getChildren().addAll(dotBox, content);
    }

    private HBox maakActieKnoppen(NotificatieDTO dto) {
        Button btnGoed = new Button("Goedkeuren");
        btnGoed.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-size: 9px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 4 10 4 10;");
        btnGoed.setCursor(Cursor.HAND);

        Button btnAf = new Button("Afwijzen");
        btnAf.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-size: 9px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 4 10 4 10;");
        btnAf.setCursor(Cursor.HAND);

        HBox acties = new HBox(6, btnGoed, btnAf);
        acties.setPadding(new Insets(4, 0, 0, 0));

        btnGoed.setOnAction(e -> voerActieUit(dto, true, btnGoed, btnAf, acties));
        btnAf.setOnAction(e -> voerActieUit(dto, false, btnGoed, btnAf, acties));

        return acties;
    }

    private void voerActieUit(NotificatieDTO dto, boolean goedkeuren, Button btnGoed, Button btnAf, HBox acties) {
        btnGoed.setDisable(true);
        btnAf.setDisable(true);

        new Thread(() -> {
            try {
                if (dto.titel().equals("Nieuwe verlofaanvraag")) {
                    VerlofFacade verlof = new VerlofFacade();
                    if (goedkeuren) verlof.keurVerlofGoed(dto.referentieId());
                    else verlof.wijsVerlofAf(dto.referentieId());
                }
                new NotificatieFacade().markeerAlsGelezen(dto.id());

                Platform.runLater(() -> {
                    String tekst = goedkeuren ? "✓ Goedgekeurd" : "✗ Afgewezen";
                    String kleur = goedkeuren ? "#ECFDF5" : "#FEF2F2";
                    String teksts = goedkeuren ? "#065F46" : "#991B1B";
                    Label lblResultaat = new Label(tekst);
                    lblResultaat.setStyle(String.format("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: %s; -fx-background-color: %s; -fx-background-radius: 8; -fx-padding: 3 8 3 8;", teksts, kleur));
                    int idx = ((VBox) acties.getParent()).getChildren().indexOf(acties);
                    ((VBox) acties.getParent()).getChildren().set(idx, new HBox(lblResultaat));
                    if (onRefresh != null) onRefresh.run();
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    btnGoed.setDisable(false);
                    btnAf.setDisable(false);
                });
            }
        }).start();
    }

    private boolean heeftActie(String titel) {
        return "Nieuwe verlofaanvraag".equals(titel) || "Teamlid afwezig".equals(titel);
    }

    private String dotKleur(String titel) {
        String t = titel.toLowerCase();
        if (t.contains("verlof")) return "#A78BFA";
        if (t.contains("afwezig") || t.contains("teamlid")) return "#FB923C";
        if (t.contains("taak")) return "#38BDF8";
        return "#9CA3AF";
    }

    private Label badge(String titel) {
        String t = titel.toLowerCase();
        String tekst, bg, fg;
        if (t.contains("verlof")) {
            tekst = "Verlof"; bg = "#EDE9FE"; fg = "#6D28D9";
        } else if (t.contains("afwezig") || t.contains("teamlid")) {
            tekst = "Afwezig"; bg = "#FFEDD5"; fg = "#C2410C";
        } else if (t.contains("taak")) {
            tekst = "Taak"; bg = "#E0F2FE"; fg = "#0369A1";
        } else {
            return new Label();
        }
        Label lbl = new Label(tekst);
        lbl.setStyle(String.format("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: %s; -fx-background-color: %s; -fx-background-radius: 8; -fx-padding: 1 6 1 6;", fg, bg));
        return lbl;
    }

    private String formatDatum(LocalDate datum) {
        if (datum == null) return "";
        LocalDate vandaag = LocalDate.now();
        if (datum.equals(vandaag)) return "vandaag";
        if (datum.equals(vandaag.minusDays(1))) return "gisteren";
        return datum.format(DateTimeFormatter.ofPattern("d MMM", new java.util.Locale("nl")));
    }
}
