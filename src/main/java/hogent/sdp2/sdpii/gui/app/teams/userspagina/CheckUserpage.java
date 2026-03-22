package hogent.sdp2.sdpii.gui.app.teams.userspagina;

import domain.dto.WerknemerDTO;
import domain.facades.WerknemersFacade;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class CheckUserpage extends VBox {
    private WerknemersFacade facade;
    private List<WerknemerDTO> werknemers;
    private UserItemController selected;
    private Consumer<Integer> onNavigeerNaarTeam;
    private Runnable onNavigeerNaarCreateUser;

    @FXML VBox teamsList;
    @FXML VBox membersList;
    @FXML HBox mainCard;
    @FXML VBox leftColumn;
    @FXML VBox rightColumn;
    @FXML Button addMemberBtn;

    public CheckUserpage(WerknemersFacade facade, Consumer<Integer> onNavigeerNaarTeam, Runnable onNavigeerNaarCreateUser) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/components/userspagina/CheckUsers.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.facade = facade;
        this.onNavigeerNaarTeam = onNavigeerNaarTeam;
        this.onNavigeerNaarCreateUser = onNavigeerNaarCreateUser;
        init();
    }

    public void init() {
        leftColumn.prefWidthProperty().bind(mainCard.widthProperty().subtract(48).multiply(0.5));
        rightColumn.prefWidthProperty().bind(mainCard.widthProperty().subtract(48).multiply(0.5));
        leftColumn.setMaxWidth(Double.MAX_VALUE);
        rightColumn.setMaxWidth(Double.MAX_VALUE);

        werknemers = facade.geefAlleWerknemers();
        werknemers.forEach(w -> {
            teamsList.getChildren().add(new UserItemController(w, this::setSelected));
        });

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

    private void refreshList() {
        teamsList.getChildren().clear();
        membersList.getChildren().clear();
        membersList.getChildren().add(noItems());
        selected = null;
        werknemers = facade.geefAlleWerknemers();
        werknemers.forEach(w -> {
            teamsList.getChildren().add(new UserItemController(w, this::setSelected));
        });
    }
}