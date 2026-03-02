package hogent.sdp2.sdpii.gui.app.tasks;

import hogent.sdp2.sdpii.gui.components.app.PageTitleController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class TasksController extends BorderPane {
    public TasksController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/TasksPage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setTop(new PageTitleController("Taken"));
    }
}
