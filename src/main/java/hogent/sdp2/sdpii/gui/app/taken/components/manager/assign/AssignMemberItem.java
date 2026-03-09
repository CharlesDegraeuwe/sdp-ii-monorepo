package hogent.sdp2.sdpii.gui.app.taken.components.manager.assign;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

public class AssignMemberItem extends HBox {

    private boolean selected = false;
    private final Circle circle;

    public AssignMemberItem(String memberName, String color) {
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add("member-item");
        setSpacing(10);

        circle = new Circle(8);
        circle.setStyle("-fx-fill: transparent; -fx-stroke: " + color + "; -fx-stroke-width: 2;");

        Label name = new Label(memberName);
        name.getStyleClass().add("task-name");

        getChildren().addAll(circle, name);

        setOnMouseClicked(e -> {
            selected = !selected;
            circle.setStyle(selected
                    ? "-fx-fill: " + color + "; -fx-stroke: " + color + "; -fx-stroke-width: 2;"
                    : "-fx-fill: transparent; -fx-stroke: " + color + "; -fx-stroke-width: 2;");
        });
    }

    public boolean isSelected() {
        return selected;
    }
}