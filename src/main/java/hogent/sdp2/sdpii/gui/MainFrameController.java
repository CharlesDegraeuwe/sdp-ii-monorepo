package hogent.sdp2.sdpii.gui;

import hogent.sdp2.sdpii.gui.app.DashboardController;
import hogent.sdp2.sdpii.gui.components.BodyController;
import hogent.sdp2.sdpii.gui.components.HeaderController;
import hogent.sdp2.sdpii.gui.components.SidebarController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import lombok.Getter;

import java.io.IOException;

public class MainFrameController extends BorderPane {
    //variables
    @Getter private SidebarController sidebar;
    @Getter private HeaderController header;
    @Getter private BodyController body;
    private Boolean sidebarSmall;

    //constructor
    public MainFrameController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/MainFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }

        //sidebar config
        sidebar = new SidebarController(this);
        header = new HeaderController(this);
        body = new BodyController(this);


        // layout instellen
        setLeft(sidebar);
        sidebar.prefWidthProperty().bind(widthProperty().multiply(0.1));
        sidebar.prefHeightProperty().bind(prefHeightProperty().multiply(1));
        this.sidebarSmall = false;

        setCenter(body);
        body.setTop(header);

        navigateTo(new DashboardController(), body);
    }

    //routing
    public void navigateTo(Node view, BodyController body) {
        body.setCenter(view);

    }

    //sidebar resizing
    public void resize () {
        if(this.sidebarSmall) {
            sidebar.prefWidthProperty().unbind();
            sidebar.prefWidthProperty().bind(widthProperty().multiply(0.1));
            sidebar.lookupAll(".sidebar_label").forEach(node -> node.setVisible(true));
            sidebar.getBurger_button().getImage().cancel();
            sidebar.getBurger_button().setImage(new Image(getClass().getResourceAsStream("/icons/sidebar_collapse.png")));
            this.sidebarSmall = false;
        }
        else {
            sidebar.prefWidthProperty().unbind();
            sidebar.prefWidthProperty().bind(widthProperty().multiply(0.05));
            sidebar.lookupAll(".sidebar_label").forEach(node -> node.setVisible(false));
            sidebar.getBurger_button().getImage().cancel();
            sidebar.getBurger_button().setImage(new Image(getClass().getResourceAsStream("/icons/burger.png")));
            this.sidebarSmall = true;
        }
    }


}
