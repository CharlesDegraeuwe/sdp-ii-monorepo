package hogent.sdp2.sdpii.gui.components.auth;

import domain.auth.Sessie;
import domain.dto.WerknemerDTO;
import domain.facades.AuthFacade;
import repository.entities.Werknemer;

import domain.facades.WerknemersFacade;
import hogent.sdp2.sdpii.gui.MainFrameController;
import hogent.sdp2.sdpii.gui.app.AppController;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginFormController extends VBox {
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtWachtwoord;
    @FXML private Label lblFout;
    @FXML private Button loginBtn;

    private MainFrameController mf;
    private Stage stage;
    private final AuthFacade service = new AuthFacade();

    public LoginFormController(MainFrameController mf, Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/auth/LoginForm.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.mf = mf;
        this.stage = stage;
        this.init();


    }

    private void init() {
        loginBtn.setText("Log in");
        this.txtWachtwoord.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                login();
            }
        });

    }

    @FXML
    private void login(javafx.event.ActionEvent event) {
       this.login();
    }

    private void login() {
        this.loginBtn.setText("laden...");
        this.loginBtn.setDisable(true);

        Task<WerknemerDTO> task = new Task<>() {
            @Override
            protected WerknemerDTO call() {
                return service.login(
                        txtEmail.getText(),
                        txtWachtwoord.getText()
                );
            }
        };

        task.setOnSucceeded(e -> {
            WerknemerDTO werknemer = task.getValue();
            System.out.println("Werknemer: " + werknemer);
            if (werknemer == null) {
                lblFout.setText("Ongeldig email of wachtwoord!");
                loginBtn.setText("Log in");
                loginBtn.setDisable(false);
                return;
            }
            Sessie.getInstance().setIngelogdeWerknemer(werknemer);
            navigeerNaarActivatie();
        });

        task.setOnFailed(e -> {
            System.out.println("Task failed: " + task.getException());
            lblFout.setText("Er ging iets mis!");
            loginBtn.setText("Log in");
            loginBtn.setDisable(false);
        });

        new Thread(task).start();
    }



    public void reset() {
        this.loginBtn.setText("Log in");
        this.loginBtn.setDisable(false);
        txtEmail.setText("");
        txtWachtwoord.setText("");
    }

    private void navigeerNaarActivatie() {
        this.mf.setCenter(new AppController(stage, mf));
    }
}

//