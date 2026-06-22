package hogent.sdp2.sdpii.gui.app.teams.userspagina;

import domain.auth.Sessie;
import domain.dto.WerknemerDTO;
import domain.facades.TeamFacade;
import domain.facades.WerknemersFacade;
import domain.util.FilteredListUtil;
import hogent.sdp2.sdpii.gui.app.teams.userspagina.components.UserDetailsController;
import hogent.sdp2.sdpii.gui.app.teams.userspagina.components.UserItemController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class CheckUserpage extends VBox {
    private WerknemersFacade facade;
    private TeamFacade teamFacade;
    private List<WerknemerDTO> werknemers;
    private UserItemController selected;
    private Consumer<Integer> onNavigeerNaarTeam;
    private Runnable onNavigeerNaarCreateUser;

    @FXML VBox buttonContainer;
    @FXML VBox teamsList;
    @FXML VBox membersList;
    @FXML HBox mainCard;
    @FXML VBox leftColumn;
    @FXML VBox rightColumn;
    @FXML Button addMemberBtn;
    @FXML TextField searchField;
    @FXML ComboBox<String> statusFilter;


    private static final String ALLE_STATUSSEN = "Alle statussen";

    public CheckUserpage(WerknemersFacade facade, TeamFacade teamFacade, Consumer<Integer> onNavigeerNaarTeam, Runnable onNavigeerNaarCreateUser) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/components/userspagina/CheckUsers.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.facade = facade;
        this.teamFacade = teamFacade;
        this.onNavigeerNaarTeam = onNavigeerNaarTeam;
        this.onNavigeerNaarCreateUser = onNavigeerNaarCreateUser;
        init();
    }

    //auto selectie constructor
    public CheckUserpage(WerknemersFacade facade, TeamFacade teamFacade, Consumer<Integer> onNavigeerNaarTeam, Runnable onNavigeerNaarCreateUser, int autoSelectWerknemerId) {
        this(facade, teamFacade, onNavigeerNaarTeam, onNavigeerNaarCreateUser);
        for (Node node : teamsList.getChildren()) {
            if (node instanceof UserItemController item && item.getWerknemer().id() == autoSelectWerknemerId) {
                setSelected(item);
                break;
            }
        }
    }

    public void init() {
        leftColumn.prefWidthProperty().bind(mainCard.widthProperty().subtract(48).multiply(0.5));
        rightColumn.prefWidthProperty().bind(mainCard.widthProperty().subtract(48).multiply(0.5));
        leftColumn.setMaxWidth(Double.MAX_VALUE);
        rightColumn.setMaxWidth(Double.MAX_VALUE);

        boolean isSupervisor = Sessie.getInstance().isSuperVisor();

        if (isSupervisor) {
            int werknemerId = Sessie.getInstance().getIngelogdeWerknemer().id();
            var teams = teamFacade.getTeamsVanWerknemer(werknemerId);
            werknemers = teams.stream()
                    .flatMap(t -> teamFacade.getTeamLeden(t.id()).stream())
                    .map(lid -> new WerknemerDTO(lid.werknemerId(), lid.naam(), lid.voornaam(),
                            lid.email(), lid.telefoonnummer(), null, lid.rol(), "Actief"))
                    .distinct().toList();

            buttonContainer.setVisible(false);
            buttonContainer.setManaged(false);
        } else {
            werknemers = facade.geefAlleWerknemers();
        }

        // Status filter
        statusFilter.getItems().setAll(ALLE_STATUSSEN, "Actief", "Geblokkeerd", "Niet geactiveerd");
        statusFilter.setValue(ALLE_STATUSSEN);
        statusFilter.setOnAction(e -> filterEnToon());

        // Search
        searchField.textProperty().addListener((obs, oud, nieuw) -> filterEnToon());

        vulWerknemersList(werknemers);
        membersList.getChildren().add(noItems());

        addMemberBtn.setOnAction(e -> onNavigeerNaarCreateUser.run());

        teamsList.setOnMouseClicked(e -> {
            Node node = (Node) e.getTarget();
            while (node != null) {
                if (node instanceof UserItemController) return;
                node = node.getParent();
            }
            clearSelection();
        });
    }

    private void filterEnToon() {
        String query = searchField.getText().toLowerCase().trim();
        String statusVal = statusFilter.getValue();

        List<WerknemerDTO> gefilterd = FilteredListUtil.filter(werknemers, w -> {
            boolean naamMatch = query.isEmpty() ||
                    (w.voornaam() + " " + w.naam()).toLowerCase().contains(query) ||
                    w.naam().toLowerCase().contains(query) ||
                    w.voornaam().toLowerCase().contains(query);

            boolean statusMatch = statusVal == null || statusVal.equals(ALLE_STATUSSEN) ||
                    (w.status() != null && w.status().equals(statusVal));

            return naamMatch && statusMatch;
        });

        vulWerknemersList(gefilterd);
        clearSelection();
    }

    private void vulWerknemersList(List<WerknemerDTO> lijst) {
        teamsList.getChildren().clear();
        lijst.forEach(w -> {
            teamsList.getChildren().add(new UserItemController(w, this::setSelected));
        });
    }

    private void refreshList() {
        werknemers = facade.geefAlleWerknemers();
        filterEnToon();
    }

    private void setSelected(UserItemController selectedItem) {
        this.selected = selectedItem;
        teamsList.getChildren().forEach(node -> {
            if (node instanceof UserItemController item) item.setSelected(false);
        });
        selectedItem.setSelected(true);
        membersList.getChildren().clear();
        membersList.getChildren().add(new UserDetailsController(selectedItem.getWerknemer(), facade, this::refreshList, onNavigeerNaarTeam));
    }

    public Pane noItems() {
        VBox v = new VBox();
        v.setStyle("-fx-alignment: CENTER");
        VBox.setVgrow(v, Priority.ALWAYS);
        Label l = new Label("geen medewerker geselecteerd");
        v.getChildren().add(l);
        return v;
    }


    private void clearSelection() {
        selected = null;
        teamsList.getChildren().forEach(node -> {
            if (node instanceof UserItemController item) {
                item.setSelected(false);
            }
        });
        membersList.getChildren().clear();
        membersList.getChildren().add(noItems());
    }

}
