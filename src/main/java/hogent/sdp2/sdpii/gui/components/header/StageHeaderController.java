package hogent.sdp2.sdpii.gui.components.header;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class StageHeaderController extends HBox {
    @FXML Button closeBtn;
    @FXML Button minimizeBtn;
    @FXML Button maximizeBtn;


        public StageHeaderController(Stage stage) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/header/StageHeader.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }


            closeBtn.setOnAction(e -> stage.close());
            minimizeBtn.setOnAction(e -> stage.setIconified(true));
            maximizeBtn.setOnAction(e -> stage.setMaximized(!stage.isMaximized()));
        }

}
