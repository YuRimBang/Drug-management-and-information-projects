package controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.AdminDAO;
import persistence.dao.ManagerDAO;
import persistence.dto.AdminDTO;
import storage.PkStorage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminViewInfo implements Initializable {
    private PkStorage storage;
    @FXML private Text idField;
    @FXML private Text nameField;
    @FXML private Text phoneNumField;
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        viewInfo();
    }

    public void viewInfo() {
        AdminDAO adminDAO = new AdminDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setPk(storage.getCurPk());
        List<AdminDTO> adminDTOS = adminDAO.inquiryAdmin(adminDTO);
        idField.setText(adminDTOS.get(0).getId());
        nameField.setText(adminDTOS.get(0).getName());
        phoneNumField.setText(adminDTOS.get(0).getPhone_num());
    }

    public void secession(ActionEvent event) { // 탈퇴
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        alert.setTitle("탈퇴");
        alert.setHeaderText("탈퇴 신청");
        alert.setContentText("탈퇴 하시겠습니까?");

        ButtonType confirmButton = new ButtonType("확인");
        ButtonType cancelButton = new ButtonType("취소");

        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == confirmButton) {
                managerDAO.deleteAdmin(storage.getCurPk());
                goToMainScreen(event);
            } else if (buttonType == cancelButton) {
            }
        });
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

    public void goToMainScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/mainUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }
}