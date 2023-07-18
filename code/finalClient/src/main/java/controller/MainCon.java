package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import protocol.Header;
import storage.PkStorage;

public class MainCon implements Initializable {
    @FXML
    private TextField idField;
    @FXML
    private TextField pwField;
    private PkStorage storage;

    Alert alert = new Alert(Alert.AlertType.INFORMATION);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
    }

    public void login(ActionEvent event) throws IOException {
        String ip = Header.ip;
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        try {
            String id = idField.getText();
            String pw = pwField.getText();
            FXMLLoader loader = new FXMLLoader();
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buf);
            dos.writeUTF(id);
            dos.writeUTF(pw);
            byte[] body = buf.toByteArray();
            Header header = new Header(
                    Header.TYPE_REQUEST,
                    Header.ACTOR_USER,
                    Header.CODE_USER_LOGIN,
                    body.length
            );
            outputStream.write(header.getBytes());
            outputStream.write(body);
            System.out.println("보낼게~ " + id + ", " + pw);

            goPage(event, loader,inputStream);
        } catch (Exception e) {
        }
    }
    public void goPage(ActionEvent event, FXMLLoader loader, DataInputStream inputStream) throws IOException {
        Header resultHeader = Header.readHeader(inputStream);
        System.out.println(resultHeader.code );
        if(resultHeader.code == 2)
        {
            alert.setTitle("로그인");
            alert.setHeaderText("로그인 실패");
            alert.setContentText("다시 입력하세요");
            alert.showAndWait();
        }
        else{
            int pk = inputStream.readInt();
            String name = inputStream.readUTF();
            String status = inputStream.readUTF();
            System.out.println(name);
            try {
                if (status.equals("user")) {
                    loader = new FXMLLoader(getClass().getResource("/JavaFX/user/userMainUI.fxml"));
                } else if (status.equals("admin")) {
                    loader = new FXMLLoader(getClass().getResource("/JavaFX/admin/adminMainUI.fxml"));
                } else if (status.equals("manager")) {
                    loader = new FXMLLoader(getClass().getResource("/JavaFX/manager/managerMainUI.fxml"));
                } else {
                    throw new Exception("Invalid user");
                }
                if(pk != 0){ // 로그인 성공
                    storage.setCurPk(pk); // 현재 사용자의 pk 설정 // 여기서 pk가 userDTO.getPk()가 됨.
                    storage.setName(name);
                }
                else{
                    System.out.println("사용자를 찾을 수 없습니다.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                alert.setTitle("로그인");
                alert.setHeaderText("로그인 실패");
                alert.setContentText("다시 입력하세요");
                alert.showAndWait();
                e.printStackTrace();
            }
        }
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    public void goToSignUpScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/choiceSignUpUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            System.out.println("test"+stage);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToFindIdScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/findIdUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }

    public void goToFindPwScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/findPwUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }

    public void goToPreviousScreen(ActionEvent event) {
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


