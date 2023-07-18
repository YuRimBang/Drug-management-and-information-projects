package controller.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.UserDAO;
import persistence.dto.InfirmaryDTO;
import persistence.dto.UserDTO;

import javax.swing.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserSignUp implements Initializable {

    @FXML private TextField idField;
    @FXML private TextField pwField;
    @FXML private TextField nameField;
    @FXML private TextField phoneNumField;
    @FXML private ChoiceBox<String> schoolBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idField.setPromptText("영문 대소문자와 숫자 4~10자");
        pwField.setPromptText("영문 대소문자와 숫자 8~20자");
        nameField.setPromptText("2-6자 한글");
        phoneNumField.setPromptText("숫자와 '-'로 이루어진 11~13자");
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

    public void signUp(ActionEvent event) {
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        UserDTO userDTO = new UserDTO();

        try {
            String id = idField.getText();
            String pw = pwField.getText();
            String name = nameField.getText();
            String phone_num = phoneNumField.getText();

            // 아이디 형식 검사 (영문 대소문자와 숫자로 이루어진 4~10자)
            if (!id.matches("^[a-zA-Z0-9]{4,10}$")) {
                showAlert("아이디 형식이 올바르지 않습니다.", "다시 입력해주세요.");
                return;
            }

            // 아이디 중복 체크
            if (userDAO.isIdDuplicateCheck(id)) {
                showAlert("중복된 아이디입니다.", "다른 아이디를 입력해주세요.");
                return;
            }

            // 비밀번호 형식 검사 (영문 대소문자와 숫자로 이루어진 8~20자)
                if (!pw.matches("^[a-zA-Z0-9]{8,20}$")) {
                showAlert("비밀번호 형식이 올바르지 않습니다.", "다시 입력해주세요.");
                return;
            }

            // 이름 형식 검사 (2-10자 한글)
            if (!name.matches("^[가-힣]{2,10}$")) {
                showAlert("이름 형식이 올바르지 않습니다.", "다시 입력해주세요.");
                return;
            }

            // 전화번호 형식 검사 (숫자와 '-'로 이루어진 11~13자)
            if (!phone_num.matches("^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}$")) {
                showAlert("전화번호 형식이 올바르지 않습니다.", "다시 입력해주세요.");
                return;
            }

            // 전화번호 중복 체크
            if (userDAO.isPhoneNumDuplicateCheck(phone_num)) {
                showAlert("전화번호가 이미 등록되어 있습니다.", "다른 전화번호를 입력해주세요.");
                return;
            }

            userDTO.setId(id);
            userDTO.setPw(pw);
            userDTO.setName(name);
            userDTO.setPhone_num(phone_num);

            String selectedSchool = schoolBox.getValue();
            userDTO.setSchool(selectedSchool);

            userDTO.setInfirmary_pk(userDAO.getInfirmaryPk(selectedSchool).getPk());

            userDAO.signUpUser(userDTO);

            idField.clear();
            pwField.clear();
            nameField.clear();
            phoneNumField.clear();

            schoolBox.setValue(null);

        } catch (Exception e) {
        }
    }

    private void showAlert(String message, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("입력 오류");
        alert.setHeaderText(message);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void goToPreviousScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/choiceSignUpUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }
}
