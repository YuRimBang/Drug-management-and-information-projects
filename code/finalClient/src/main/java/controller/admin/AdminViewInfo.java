package controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.AdminDAO;
import persistence.dao.ManagerDAO;
import persistence.dto.AdminDTO;
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

public class AdminViewInfo implements Initializable {
    private PkStorage storage;
    @FXML private Text idField;
    @FXML private Text nameField;
    @FXML private Text phoneNumField;
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
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

        int pk = storage.getCurPk();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);
        dos.writeInt(pk);
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_ADMIN,
                Header.CODE_ADMIN_VIEW_MY_INFO,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);

        viewInfo(inputStream);
    }
public void viewInfo(DataInputStream inputStream) throws IOException {
    Header header = Header.readHeader(inputStream);
    idField.setText(inputStream.readUTF());
    nameField.setText(inputStream.readUTF());
    phoneNumField.setText(inputStream.readUTF());
}
    public void secession(ActionEvent event) throws IOException { // 탈퇴
        String ip = storage.getIp();
        Socket socket = new Socket(ip, 4000);
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        int pk = storage.getCurPk();
        alert.setTitle("탈퇴");
        alert.setHeaderText("탈퇴 신청");
        alert.setContentText("탈퇴 하시겠습니까?");

        ButtonType confirmButton = new ButtonType("확인");
        ButtonType cancelButton = new ButtonType("취소");

        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == confirmButton) {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(buf);
                try {
                    dos.writeInt(pk);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("삭제할 pk : " + pk);
                byte[] body = buf.toByteArray();
                Header header = new Header(
                        Header.TYPE_REQUEST,
                        Header.ACTOR_MANAGER,
                        Header.CODE_MANAGER_DELETE_ADMIN,
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/admin/adminMainUI.fxml"));

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