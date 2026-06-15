package hogent.sdp2.sdpii.gui.app.teams.userspagina;

import domain.facades.WerknemersFacade;
import hogent.sdp2.sdpii.gui.app.teams.userspagina.components.CreateUserFormController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class CreateUserPage extends BorderPane {
    public CreateUserPage(WerknemersFacade facade) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/components/userspagina/CreateUsers.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.setCenter(new CreateUserFormController(facade));
    }


}
