package hogent.sdp2.sdpii.gui.components.app;

import domain.auth.Sessie;
import domain.dto.UpdateWerknemerDTO;
import domain.dto.WerknemerDTO;
import domain.facades.WerknemersFacade;
import hogent.sdp2.sdpii.gui.app.AppController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class AccountFormController extends VBox {

    @FXML private TextField naamField;
    @FXML private TextField voornaamField;
    @FXML private TextField emailField;
    @FXML private TextField telefoonnummerField;
    @FXML private TextField geboortedatumField;
    @FXML private Button editButton;

    private final WerknemersFacade facade;
    private WerknemerDTO huidigeWerknemer;
    private boolean editing = false;

    public AccountFormController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/app/AccountForm.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        this.facade = new WerknemersFacade();
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }
        initializeForm();
    }

    private void initializeForm() {
        loadUserData();
        setEditable(false);
    }

    private void loadUserData() {
        huidigeWerknemer = Sessie.getInstance().getIngelogdeWerknemer();
        if (huidigeWerknemer != null) {
            naamField.setText(huidigeWerknemer.naam());
            voornaamField.setText(huidigeWerknemer.voornaam());
            emailField.setText(huidigeWerknemer.email());
            telefoonnummerField.setText(huidigeWerknemer.telefoonnummer());
            geboortedatumField.setText(huidigeWerknemer.geboortedatum().toString());
        }
    }

    private void setEditable(boolean value) {
        naamField.setEditable(value);
        voornaamField.setEditable(value);
        emailField.setEditable(value);
        telefoonnummerField.setEditable(value);
        geboortedatumField.setEditable(value);
    }

    @FXML
    private void toggleEdit() {
        editing = !editing;
        if (editing) {
            setEditable(true);
            editButton.setText("Opslaan");
        } else {
            saveUser();
            setEditable(false);
            editButton.setText("Wijzig");
        }
    }

    private void saveUser() {
        if (huidigeWerknemer == null) return;
        UpdateWerknemerDTO update = new UpdateWerknemerDTO(
                huidigeWerknemer.id(),
                naamField.getText(),
                voornaamField.getText(),
                emailField.getText(),
                telefoonnummerField.getText(),
                java.time.LocalDate.parse(geboortedatumField.getText()),
                huidigeWerknemer.status()
        );
        facade.update(update);
        WerknemerDTO bijgewerkt = new WerknemerDTO(
                huidigeWerknemer.id(),
                update.naam(),
                update.voornaam(),
                update.email(),
                update.telefoonnummer(),
                update.geboortedatum(),
                huidigeWerknemer.rol(),
                huidigeWerknemer.status()
        );
        Sessie.getInstance().setIngelogdeWerknemer(bijgewerkt);
        huidigeWerknemer = bijgewerkt;
    }
}