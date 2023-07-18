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
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.*;
import persistence.dto.InfirmaryDTO;
import persistence.dto.InfirmaryMedicineDTO;
import persistence.dto.InfirmaryNoticeDTO;
import persistence.dto.UserDTO;
import storage.PkStorage;

import javax.xml.soap.Text;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminUpdateMedicineNum implements Initializable {
    private PkStorage storage;
    @FXML private ListView<Object> medicineNumListField;
    private ObservableList<Object> medicineNumList;
    @FXML private TextField stockField;
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();

        medicineNumList = FXCollections.observableArrayList();
        medicineNumListField.setItems(medicineNumList);

        viewMedicineNumList();
    }

    public void viewMedicineNumList() {
        InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        int infirmary_pk = infirmaryDAO.getInfirmaryPk(storage.getCurPk());

        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryMedicineDAO infirmaryMedicineDAO = new InfirmaryMedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<InfirmaryMedicineDTO> medicineListResult = infirmaryMedicineDAO.inquiryMedicineStock(infirmary_pk);
        medicineNumList.clear();
        try {
            for (InfirmaryMedicineDTO medicine : medicineListResult) {
                String medicineName = medicineDAO.inquiryMedicineNameBySeqNum(String.valueOf(medicine.getMedicine_pk()));
                medicine.setMedicine_name(medicineName);
                medicineNumList.add(medicine);
            }
            medicineNumListField.refresh();
        } catch (Exception e) {
        }
    }

    public void updateMedicineNum() {
        InfirmaryMedicineDAO infirmaryMedicineDAO = new InfirmaryMedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryMedicineDTO selectedItem = (InfirmaryMedicineDTO) medicineNumListField.getSelectionModel().getSelectedItem();
        selectedItem.setMedicine_stock(Integer.parseInt(stockField.getText()));

        int row = infirmaryMedicineDAO.updateInfirmaryMedicine(selectedItem);

        if(row == 1) {
            stockField.clear();
            viewMedicineNumList();

            alert.setTitle("약 수량 수정");
            alert.setHeaderText("약 수량 수정 성공");
            alert.setContentText("약 수량 수정되었습니다.");

            alert.showAndWait();
        }

    }

    public void plusMedicineNum() {
        InfirmaryMedicineDAO infirmaryMedicineDAO = new InfirmaryMedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryMedicineDTO selectedItem = (InfirmaryMedicineDTO) medicineNumListField.getSelectionModel().getSelectedItem();

        int row = infirmaryMedicineDAO.plusInfirmaryMedicineNum(selectedItem.getPk());

        if(row == 1) {
            viewMedicineNumList();
        }
    }

    public void minusMedicineNum() {
        InfirmaryMedicineDAO infirmaryMedicineDAO = new InfirmaryMedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryMedicineDTO selectedItem = (InfirmaryMedicineDTO) medicineNumListField.getSelectionModel().getSelectedItem();

        int row = infirmaryMedicineDAO.minusInfirmaryMedicineNum(selectedItem.getPk());

        if(row == 1) {
            viewMedicineNumList();
        }
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
}