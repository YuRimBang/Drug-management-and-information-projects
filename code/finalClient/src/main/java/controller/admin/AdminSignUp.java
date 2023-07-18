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
import protocol.Header;

import java.io.*;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.ResourceBundle;
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
    byte[] imageData;
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
                imageData = convertFileToByteArray(selectedFile);
                fileTextField.setText(selectedFile.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private byte[] convertFileToByteArray(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] byteArray = new byte[(int) file.length()];
        fileInputStream.read(byteArray,0,byteArray.length);
        fileInputStream.close();
        return byteArray;
    }

    public void signUp(ActionEvent event) throws IOException {
        String ip = Header.ip;
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buf);

            String id = idField.getText();
            String pw = pwField.getText();
            String name = nameField.getText();
            String phone_num = phoneNumField.getText();

            dos.writeUTF(id);
            dos.writeUTF(pw);
            dos.writeUTF(name);
            dos.writeUTF(phone_num);

            File selectedFile = null;

            if (!fileTextField.getText().isEmpty()){
                selectedFile = new File(fileTextField.getText());
            }

            if (selectedFile != null) {
                try {
                    byte[] imageData = convertFileToByteArray(selectedFile);
                    dos.writeInt(imageData.length);
                    dos.write(imageData);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            byte[] body = buf.toByteArray();
            System.out.println(body.length);
            Header header = new Header(
                    Header.TYPE_REQUEST,
                    Header.ACTOR_ADMIN,
                    Header.CODE_ADMIN_REGIST,
                    body.length
            );
            outputStream.write(header.getBytes());
            outputStream.write(body);

            Header header1 = Header.readHeader(inputStream);
            if(header1.code == 3)
            {
                String errorMgs = inputStream.readUTF();
                String[] errArr = errorMgs.split("\\.");
                showAlert(errArr[0], errArr[1]);
                return;
            }
            else
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
            }
//            if(check == 1)
//            {
//                idField.clear();
//                pwField.clear();
//                nameField.clear();
//                phoneNumField.clear();
//                fileTextField.clear();
//
//                alert.setTitle("회원가입");
//                alert.setHeaderText("회원가입 성공");
//                alert.setContentText("회원가입이 되었습니다.");
//
//                alert.showAndWait();
//            } else
//            {
//                alert.setTitle("회원가입");
//                alert.setHeaderText("회원가입 실패");
//                alert.setContentText("회원가입에 실패했습니다.");
//
//                alert.showAndWait();
//            }
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