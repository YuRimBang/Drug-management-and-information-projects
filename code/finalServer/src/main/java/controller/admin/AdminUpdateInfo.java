package controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.AdminDAO;
import persistence.dto.AdminDTO;
import storage.PkStorage;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminUpdateInfo implements Initializable {
    private PkStorage storage;
    @FXML private TextField nameField;
    @FXML private TextField phoneNumField;
    Alert alert = new Alert(Alert.AlertType.INFORMATION);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
    }

    public void updateInfo() {
        AdminDAO adminDAO = new AdminDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        AdminDTO adminDTO = new AdminDTO();

        adminDTO.setPk(storage.getCurPk());
        adminDTO.setName(nameField.getText());
        adminDTO.setPhone_num(phoneNumField.getText());

        int result = adminDAO.updateAdminInfo(adminDTO);

        if(result == 1) {
            alert.setTitle("정보 수정");
            alert.setHeaderText("정보 수정 성공 메시지");
            alert.setContentText("정보가 수정되었습니다.");
            alert.showAndWait();
        }

        nameField.clear();
        phoneNumField.clear();
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