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
import protocol.Header;
import storage.PkStorage;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserSignUp implements Initializable {
    private PkStorage storage;
    @FXML private TextField idField;
    @FXML private TextField pwField;
    @FXML private TextField nameField;
    @FXML private TextField phoneNumField;
    @FXML private ChoiceBox<String> schoolBox;
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();

        idField.setPromptText("영문 대소문자와 숫자 4~10자");
        pwField.setPromptText("영문 대소문자와 숫자 8~20자");
        nameField.setPromptText("2-6자 한글");
        phoneNumField.setPromptText("숫자와 '-'로 이루어진 11~13자");
        try {
            setSchoolBox();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSchoolBox() throws IOException {
        String ip = Header.ip;
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_USER,
                Header.CODE_USER_SHOW_SCHOOL_LIST,
                0
        );
        outputStream.write(header.getBytes());

        int size = inputStream.readInt();
        for(int i = 0; i < size; i++)
        {
            String schoolName = inputStream.readUTF();
            schoolBox.getItems().add(schoolName);
        }
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
            String selectedSchool = schoolBox.getValue();
            dos.writeUTF(id);
            dos.writeUTF(pw);
            dos.writeUTF(name);
            dos.writeUTF(phone_num);
            dos.writeUTF(selectedSchool);
            byte[] body = buf.toByteArray();
            Header header = new Header(
                    Header.TYPE_REQUEST,
                    Header.ACTOR_USER,
                    Header.CODE_USER_REGIST,
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
                schoolBox.setValue(null);
                alert.setTitle("회원가입");
                alert.setHeaderText("회원가입 성공");
                alert.setContentText("회원가입이 되었습니다.");
                alert.showAndWait();
            }
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
