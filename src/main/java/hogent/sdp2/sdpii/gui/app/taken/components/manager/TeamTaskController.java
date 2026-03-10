package hogent.sdp2.sdpii.gui.app.taken.components.manager;

import domain.auth.Sessie;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.IOException;

public class TeamTaskController extends VBox {
    @FXML @Getter BorderPane page_container;

    public TeamTaskController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/manager/TeamTaskLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void Init(){

    }
}
