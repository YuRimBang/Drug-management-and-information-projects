package controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.InfirmaryDAO;
import persistence.dao.InfirmaryMedicineDAO;
import persistence.dto.InfirmaryMedicineDTO;
import storage.PkStorage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminDeleteMedicine implements Initializable {

    private PkStorage storage;
    @FXML
    private ListView<Object> medicineListField;
    private ObservableList<Object> medicineList;
    InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());
    InfirmaryMedicineDAO infirmaryMedicineDAO = new InfirmaryMedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        medicineList = FXCollections.observableArrayList();
        medicineListField.setItems(medicineList);

        viewMedicineList();

    }

    public void viewMedicineList() {
        int infirmary_pk = infirmaryDAO.getInfirmaryPk(storage.getCurPk());
        System.out.println("보건실키:" + storage.getCurPk());

        List<InfirmaryMedicineDTO> infirmaryMedicineDTOS = infirmaryMedicineDAO.selectAllByInfirmaryPk(infirmary_pk);

        for (InfirmaryMedicineDTO infirmaryMedicineDTO : infirmaryMedicineDTOS) {
            medicineList.add(infirmaryMedicineDTO);
        }
        medicineListField.refresh();
    }


    public void goToPreviousScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/admin/adminMainUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }

    public void deleteMedicine() {
        int infirmary_pk = infirmaryDAO.getInfirmaryPk(storage.getCurPk());

        InfirmaryMedicineDTO infirmaryMedicineDTO = (InfirmaryMedicineDTO) medicineListField.getSelectionModel().getSelectedItem();

        infirmaryMedicineDAO.deleteInfirmaryMedicine(infirmaryMedicineDTO.getPk());

        List<InfirmaryMedicineDTO> infirmaryMedicineDTOS = infirmaryMedicineDAO.selectAllByInfirmaryPk(infirmary_pk);
        medicineList.clear();
        for (InfirmaryMedicineDTO infirmaryMedicineDTO1 : infirmaryMedicineDTOS) {
            medicineList.add(infirmaryMedicineDTO1);
        }
        medicineListField.setItems(medicineList);

        medicineListField.getSelectionModel().clearSelection();
    }
}