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
import persistence.dao.UserDAO;
import persistence.dto.AdminDTO;
import persistence.dto.UserDTO;
import storage.PkStorage;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminUpdatePw implements Initializable {
    private PkStorage storage;
    @FXML
    private TextField pwField;
    @FXML private TextField newPwField;
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
    }

    public void updatePw() {
        AdminDAO adminDAO = new AdminDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        AdminDTO adminDTO = new AdminDTO();

        if(pwField.getText().equals(adminDAO.checkPw(storage.getCurPk()))) {
            adminDTO.setPk(storage.getCurPk());
            adminDTO.setPw(newPwField.getText());
            adminDAO.updatePw(adminDTO);

            alert.setTitle("비밀번호 수정");
            alert.setHeaderText("비밀번호 수정 성공 메시지");
            alert.setContentText("비밀번호가 수정되었습니다.");

            alert.showAndWait();
        } else {
            alert.setTitle("비밀번호 불일치");
            alert.setHeaderText("비밀번호 수정 실패 메시지");
            alert.setContentText("비밀번호가 일치하지 않습니다.");

            alert.showAndWait();
        }

        pwField.clear();
        newPwField.clear();
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