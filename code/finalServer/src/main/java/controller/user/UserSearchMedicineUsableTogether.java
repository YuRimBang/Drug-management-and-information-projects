package controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.MedicineDAO;
import persistence.dto.RefineData;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserSearchMedicineUsableTogether implements Initializable {
    @FXML private TextField medicine1Field;
    @FXML private TextField medicine2Field;
    @FXML private ListView<String> medicineListField;
    private ObservableList<String> medicineList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        medicineList = FXCollections.observableArrayList();
        medicineListField.setItems(medicineList);
    }

    public void inquiryElderlyCautionMedicine(ActionEvent event) {
        try {
            MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
            String content = medicineDAO.combination(medicine1Field.getText(), medicine2Field.getText());

            medicineList.clear();
            medicineList.add(content);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToPreviousScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/user/userSearchMedicineUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }
}