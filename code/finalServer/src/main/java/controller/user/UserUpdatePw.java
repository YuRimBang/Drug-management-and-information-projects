package controller.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.w3c.dom.Text;
import persistence.MyBatisConnectionFactory;
import persistence.dao.UserDAO;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import persistence.dto.UserDTO;

import java.net.URL;
import java.util.ResourceBundle;

public class UserUpdatePw implements Initializable {
    int pk = 1;
    @FXML private TextField pwField;
    @FXML private TextField newPwField;
    Alert alert = new Alert(AlertType.INFORMATION);
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void updatePw() {
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        UserDTO userDTO = new UserDTO();

        if(pwField.getText().equals(userDAO.checkPw(pk))) {
            userDTO.setPk(pk);
            userDTO.setPw(newPwField.getText());
            userDAO.updatePw(userDTO);

            alert.setTitle("비밀번호 변경");
            alert.setHeaderText("비밀번호 변경 메시지");
            alert.setContentText("비밀번호가 변경되었습니다.");

            alert.showAndWait();
        } else {
            alert.setTitle("비밀번호 불일치");
            alert.setHeaderText("비밀번호 불일치 메시지");
            alert.setContentText("비밀번호가 일치하지 않습니다.");

            alert.showAndWait();
        }

        pwField.clear();
        newPwField.clear();
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
}
