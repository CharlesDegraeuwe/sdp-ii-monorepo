package hogent.sdp2.sdpii;

import hogent.sdp2.sdpii.gui.MainFrameController;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import java.awt.Taskbar;
import java.awt.Toolkit;
import javafx.scene.text.Font;
import java.io.IOException;

public class ManagementApplication extends javafx.application.Application {
    @Override

    public void start(Stage primaryStage) throws IOException {
        Scene scene = new Scene(new MainFrameController(primaryStage), 1380, 900);
        scene.getStylesheets().add("css/application.css");

        //primary stage initialiseren
        primaryStage.setOnShown((WindowEvent t) -> {
            primaryStage.setMinWidth(primaryStage.getWidth());
            primaryStage.setMinHeight(primaryStage.getHeight());
        });
        primaryStage.setTitle("Welcome");
        //onze eigen stijl toevoege
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);

        //app icon instellen
        primaryStage.getIcons().addAll(
                new Image(getClass().getResourceAsStream("/icons/logo.png")));
        if (Taskbar.isTaskbarSupported()) {
            var taskbar = Taskbar.getTaskbar();
            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                taskbar.setIconImage(Toolkit.getDefaultToolkit().getImage(
                        getClass().getResource("/icons/logo.png")
                ));
            }
        }

        //fonts laden
        Font f1 = Font.loadFont(getClass().getResourceAsStream("/fonts/akira/akira.otf"), 14);
        Font f2 = Font.loadFont(getClass().getResourceAsStream("/fonts/sf-pro-display/SFPRODISPLAYREGULAR.OTF"), 14);
        Font f3 = Font.loadFont(getClass().getResourceAsStream("/fonts/sf-pro-display/SFPRODISPLAYBOLD.OTF"), 14);

        /*
        System.out.println(f1.getName());
        System.out.println(f2.getName());
        System.out.println(f3.getName());*/

        primaryStage.show();
    }
}
