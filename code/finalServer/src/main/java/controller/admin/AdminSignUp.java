package controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.AdminDAO;
import persistence.dao.UserDAO;
import persistence.dto.AdminDTO;
import persistence.dto.UserDTO;

import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.ResourceBundle;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.net.MalformedURLException;
import java.net.URL;

public class AdminSignUp implements Initializable {

    @FXML private TextField idField;
    @FXML private TextField pwField;
    @FXML private TextField nameField;
    @FXML private TextField phoneNumField;
    @FXML private TextField fileTextField;

    Alert alert = new Alert(Alert.AlertType.INFORMATION);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idField.setPromptText("영문 대소문자와 숫자 4~10자");
        pwField.setPromptText("영문 대소문자와 숫자 8~20자");
        nameField.setPromptText("2-6자 한글");
        phoneNumField.setPromptText("숫자와 '-'로 이루어진 11~13자");
    }

    public void chooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("재직 증명서 선택");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                byte[] imageData = convertFileToByteArray(selectedFile);
                fileTextField.setText(selectedFile.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private byte[] convertFileToByteArray(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] byteArray = new byte[(int) file.length()];
        fileInputStream.read(byteArray);
        fileInputStream.close();
        return byteArray;
    }

    public void signUp(ActionEvent event) {
        AdminDAO adminDAO = new AdminDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        AdminDTO adminDTO = new AdminDTO();

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
            if (adminDAO.isIdDuplicateCheck(id)) {
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
            if (adminDAO.isPhoneNumDuplicateCheck(phone_num)) {
                showAlert("전화번호가 이미 등록되어 있습니다.", "다른 전화번호를 입력해주세요.");
                return;
            }

            adminDTO.setId(id);
            adminDTO.setPw(pw);
            adminDTO.setName(name);
            adminDTO.setPhone_num(phone_num);

            File selectedFile = null;

            if (!fileTextField.getText().isEmpty()){
                selectedFile = new File(fileTextField.getText());
            }

            if (selectedFile != null) {
                try {
                    byte[] imageData = convertFileToByteArray(selectedFile);
                    adminDTO.setFile(imageData);

                    System.out.println("adminDTO의 파일: " + adminDTO.getFile());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            int check = adminDAO.signUpAdmin(adminDTO);
            System.out.println("check: " + check);

            if(check == 1)
            {
                idField.clear();
                pwField.clear();
                nameField.clear();
                phoneNumField.clear();
                fileTextField.clear();

                alert.setTitle("회원가입");
                alert.setHeaderText("회원가입 성공");
                alert.setContentText("회원가입이 되었습니다.");

                alert.showAndWait();
            } else
            {
                alert.setTitle("회원가입");
                alert.setHeaderText("회원가입 실패");
                alert.setContentText("회원가입에 실패했습니다.");

                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            alert.setTitle("오류");
            alert.setHeaderText("오류 발생");
            alert.setContentText("회원가입 중 오류가 발생했습니다.");

            alert.showAndWait();
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