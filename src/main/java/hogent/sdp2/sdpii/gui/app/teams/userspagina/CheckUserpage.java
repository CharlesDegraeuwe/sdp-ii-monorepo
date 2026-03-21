package hogent.sdp2.sdpii.gui.app.teams.userspagina;

import domain.dto.WerknemerDTO;
import domain.facades.WerknemersFacade;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.List;

public class CheckUserpage extends VBox {
    private WerknemersFacade facade;
    private List<WerknemerDTO> werknemers;
    private UserItemController selected;

    @FXML VBox teamsList;
    @FXML VBox membersList;
    @FXML HBox mainCard;
    @FXML VBox leftColumn;
    @FXML VBox rightColumn;


    public CheckUserpage(WerknemersFacade facade) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/userspagina/CheckUsers.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.facade = facade;
        init();
    }

    public void init() {
        leftColumn.prefWidthProperty().bind(mainCard.widthProperty().subtract(48).multiply(0.5));
        rightColumn.prefWidthProperty().bind(mainCard.widthProperty().subtract(48).multiply(0.5));
        leftColumn.setMaxWidth(Double.MAX_VALUE);
        rightColumn.setMaxWidth(Double.MAX_VALUE);
        werknemers = facade.geefAlleWerknemers();

        werknemers.forEach(w -> {
            UserItemController item = new UserItemController(w, this::setSelected);
            teamsList.getChildren().add(item);
        });

        membersList.getChildren().add(noItems());

        teamsList.setOnMouseClicked(e -> {
            Node node = (Node) e.getTarget();
            while (node != null) {
                if (node instanceof UserItemController) return;
                node = node.getParent();
            }
            clearSelection();
        });


    }

    public Pane noItems() {
        VBox v = new VBox();
        v.setStyle("-fx-alignment: CENTER");
        VBox.setVgrow(v, Priority.ALWAYS);
        Label l = new Label("geen medewerker geselecteerd");
        v.getChildren().add(l);
        return v;
    }

    private void setSelected(UserItemController selectedItem) {
        this.selected = selectedItem;

        teamsList.getChildren().forEach(node -> {
            if (node instanceof UserItemController item) {
                item.setSelected(false);
            }
        });

        selectedItem.setSelected(true);
        membersList.getChildren().clear();
        membersList.getChildren().add(new UserDetailsController(selectedItem.getWerknemer(), facade, this::refreshList));
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