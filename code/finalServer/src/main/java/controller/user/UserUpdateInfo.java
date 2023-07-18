package controller.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.UserDAO;
import persistence.dto.InfirmaryDTO;
import persistence.dto.UserDTO;
import storage.PkStorage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserUpdateInfo implements Initializable {
    private PkStorage storage;

    @FXML private TextField nameField;
    @FXML private TextField phoneNumField;
    @FXML private ChoiceBox<String> schoolBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        setSchoolBox();
    }

    public void setSchoolBox() {
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<InfirmaryDTO> list = userDAO.viewSchoolList();

        for(InfirmaryDTO item : list) {
            String schoolName = item.getSchool();
            schoolBox.getItems().add(schoolName);
        }
    }

    public void updateinfo(ActionEvent event) {
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        UserDTO userDTO = new UserDTO();

        try {
            userDTO.setPk(storage.getCurPk());
            userDTO.setName(nameField.getText());
            userDTO.setPhone_num(phoneNumField.getText());

            String selectedSchool = schoolBox.getValue();
            userDTO.setSchool(selectedSchool);

            userDTO.setSchool(selectedSchool);
            userDTO.setInfirmary_pk(userDAO.getInfirmaryPk(selectedSchool).getPk());

            userDAO.updateName(userDTO);
            userDAO.updatePhoneNum(userDTO);
            userDAO.updateSchool(userDTO);

            nameField.clear();
            phoneNumField.clear();
            schoolBox.setValue(null);
        } catch (Exception e) {
        }
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
