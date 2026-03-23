package hogent.sdp2.sdpii.gui.app.overzicht.components.locatie_info;

import domain.dto.LocatieInfoDTO;
import domain.facades.LocatieFacade;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class LocatieInfoController extends VBox {
    @FXML
    TableView<LocatieInfoDTO> locatieTable;
    @FXML TableColumn<LocatieInfoDTO, String> colNaam;
    @FXML TableColumn<LocatieInfoDTO, String> colLocatie;
    @FXML
    TableColumn<LocatieInfoDTO, String> colStatus;
    @FXML TableColumn<LocatieInfoDTO, String> colStats;
    @FXML
    Button see_more;

    public LocatieInfoController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/locatie_info/LocatieInfo.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        init();
    }

    private void init() {
        colNaam.setCellValueFactory(new PropertyValueFactory<>("naam"));
        colLocatie.setCellValueFactory(new PropertyValueFactory<>("locatie"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStats.setCellValueFactory(new PropertyValueFactory<>("quickStats"));

        try {
            new LocatieFacade().geefAlleLocaties().forEach(l ->
                locatieTable.getItems().add(new LocatieInfoDTO(
                        l.naam(),
                        l.locatie(),
                        l.status() != null ? l.status() : "—",
                        l.capaciteit() != null ? "capaciteit: " + l.capaciteit() : "—"
                ))
            );
        } catch (Exception e) {
            // toon lege tabel bij fout
        }

        locatieTable.setPrefHeight(35 + locatieTable.getItems().size() * 40);
        locatieTable.setMinHeight(Region.USE_PREF_SIZE);
        locatieTable.setMaxHeight(Region.USE_PREF_SIZE);

        see_more.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.LOCATIES));
    }
}