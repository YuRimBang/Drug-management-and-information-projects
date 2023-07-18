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
import protocol.Header;
import storage.PkStorage;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
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
        try {
            viewInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void viewInfo() throws IOException {
        String ip = storage.getIp();
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buf);

            dos.writeInt(storage.getCurPk());
            byte[] body = buf.toByteArray();
            Header header = new Header(
                    Header.TYPE_REQUEST,
                    Header.ACTOR_USER,
                    Header.CODE_USER_VIEW_MY_INFO,
                    body.length
            );
            outputStream.write(header.getBytes());
            outputStream.write(body);

            int size = inputStream.readInt();
            UserDTO userInfo = UserDTO.readUser(inputStream);
            idText.setText(userInfo.getId());
            nameText.setText(userInfo.getName());
            phoneNumText.setText(userInfo.getPhone_num());
            schoolText.setText(userInfo.getSchool());
        } catch (Exception e) {
        }
    }

    public void secession(ActionEvent event) throws IOException { // 탈퇴
        String ip = Header.ip;
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        alert.setTitle("탈퇴");
        alert.setHeaderText("탈퇴 신청");
        alert.setContentText("탈퇴 하시겠습니까?");

        ButtonType confirmButton = new ButtonType("확인");
        ButtonType cancelButton = new ButtonType("취소");

        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        alert.showAndWait().ifPresent(buttonType -> {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buf);
            try {
                dos.writeInt(storage.getCurPk());
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] body = buf.toByteArray();
            if (buttonType == confirmButton) {
                Header header = new Header(
                        Header.TYPE_REQUEST,
                        Header.ACTOR_USER,
                        Header.CODE_USER_SECESSION,
                        body.length
                );
                try {
                    outputStream.write(header.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.write(body);
                } catch (IOException e) {
                    e.printStackTrace();
                }

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