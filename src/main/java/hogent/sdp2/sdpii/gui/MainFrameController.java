package hogent.sdp2.sdpii.gui;

import hogent.sdp2.sdpii.gui.app.DashboardController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainFrameController extends BorderPane {
    public MainFrameController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/MainFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }

        //sidebar config
        SidebarController sidebar = new SidebarController(this);
        setLeft(sidebar);
        navigateTo(new DashboardController());
        sidebar.prefWidthProperty().bind(widthProperty().multiply(0.1));
        sidebar.prefHeightProperty().bind(prefHeightProperty().multiply(1));
    }

    public void navigateTo(Node view) {
        setCenter(view);
    }


}
