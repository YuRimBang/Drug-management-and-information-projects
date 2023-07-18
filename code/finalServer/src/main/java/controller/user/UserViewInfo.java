package controller.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.ManagerDAO;
import persistence.dao.UserDAO;
import persistence.dto.UserDTO;
import storage.PkStorage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserViewInfo implements Initializable {
    private PkStorage storage;
    @FXML private Text idText;
    @FXML private Text nameText;
    @FXML private Text phoneNumText;
    @FXML private Text schoolText;
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

    public void setUserInfo(String id, String name, String phoneNum, String gender, String school) {
        idText.setText(id);
        nameText.setText(name);
        phoneNumText.setText(phoneNum);
        schoolText.setText(school);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        viewInfo();
    }

    public void viewInfo() {
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        UserDTO userDTO = new UserDTO();

        try {
            userDTO.setPk(storage.getCurPk());

            List<UserDTO> userInfoList = userDAO.inquiryUser(userDTO);
            idText.setText(userInfoList.get(0).getId());
            nameText.setText(userInfoList.get(0).getName());
            phoneNumText.setText(userInfoList.get(0).getPhone_num());
            schoolText.setText(userInfoList.get(0).getSchool());

        } catch (Exception e) {
        }
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
                managerDAO.deleteUser(storage.getCurPk());
                goToMainScreen(event);
            } else if (buttonType == cancelButton) {
            }
        });
    }

    public void goToPreviousScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/user/userMainUI.fxml"));

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