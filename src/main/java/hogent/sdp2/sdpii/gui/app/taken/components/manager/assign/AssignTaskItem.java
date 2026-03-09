package hogent.sdp2.sdpii.gui.app.taken.components.manager.assign;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class AssignTaskItem extends HBox {

    public AssignTaskItem(String taskName, String dueText) {
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add("assign-task-item");
        setSpacing(10);

        Label name = new Label(taskName);
        name.getStyleClass().add("task-name");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label due = new Label(dueText);
        due.getStyleClass().add("task-due-badge");

        Button delete = new Button("\uD83D\uDDD1");
        delete.getStyleClass().add("task-action-btn");

        getChildren().addAll(name, spacer, due, delete);
    }
}