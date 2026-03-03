package hogent.sdp2.sdpii.gui.admin.creeerMedewerker;

import hogent.sdp2.sdpii.gui.app.AppController;
import hogent.sdp2.sdpii.gui.components.admin.RegisterEmployeeForm;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class CreeerMedewerkerController extends BorderPane {

    private RegisterEmployeeForm form;

    public CreeerMedewerkerController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/admin/create_employee/CreateEmployee.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        form = new RegisterEmployeeForm();
        setCenter(form);
    }
}
