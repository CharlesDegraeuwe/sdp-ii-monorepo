package hogent.sdp2.sdpii.gui.app.teams.userspagina;

import domain.dto.WerknemerDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.function.Consumer;

public class UserItemController extends HBox {
    @FXML Label naam;

    private WerknemerDTO werknemer;
    private Consumer<UserItemController> onSelect;

    public UserItemController(WerknemerDTO werknemer, Consumer<UserItemController> onSelect) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/userspagina/UserItem.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.werknemer = werknemer;
        this.onSelect = onSelect;
        init();
    }

    private void init() {
        naam.setText(werknemer.voornaam() + " " + werknemer.naam());
        this.setOnMouseClicked(e -> onSelect.accept(this));
    }

    public void setSelected(boolean selected) {
        if (selected) {
            this.getStyleClass().add("selected");
        } else {
            this.getStyleClass().remove("selected");
        }
    }

    public WerknemerDTO getWerknemer() {
        return werknemer;
    }
}