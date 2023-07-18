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
import persistence.dto.AdminDTO;
import protocol.Header;
import storage.PkStorage;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminUpdateInfo implements Initializable {
    private PkStorage storage;
    @FXML private TextField nameField;
    @FXML private TextField phoneNumField;
    Alert alert = new Alert(Alert.AlertType.INFORMATION);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
    }

    public void updateInfo() throws IOException {
        String ip = Header.ip;
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        int pk = storage.getCurPk();
        String name = nameField.getText();
        String phoneNum = phoneNumField.getText();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);
        dos.writeInt(pk);
        dos.writeUTF(name);
        dos.writeUTF(phoneNum);
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_ADMIN,
                Header.CODE_ADMIN_CHANGE_MY_INFO,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);

        updateResult(inputStream);
        nameField.clear();
        phoneNumField.clear();
    }
public void updateResult(DataInputStream inputStream) throws IOException {
    int result = inputStream.readInt();
    if(result == 1) {
        alert.setTitle("정보 수정");
        alert.setHeaderText("정보 수정 성공 메시지");
        alert.setContentText("정보가 수정되었습니다.");
        alert.showAndWait();
    }
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