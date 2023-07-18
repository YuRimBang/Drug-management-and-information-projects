package controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.InfirmaryDAO;
import persistence.dao.InfirmaryNoticeDAO;
import persistence.dto.InfirmaryNoticeDTO;
import protocol.Header;
import storage.PkStorage;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminRegisterNotice implements Initializable {
    private PkStorage storage;

    @FXML private TextField titleField;
    @FXML private TextArea contentField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
    }

    public void registerNotice(ActionEvent event) throws IOException {
        String ip = storage.getIp();

        Socket socket = new Socket(ip, 4000);
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        try {
            String title = titleField.getText();
            String content = contentField.getText();

            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buf);

            dos.writeInt(storage.getCurPk());
            dos.writeUTF(title);
            dos.writeUTF(content);
            byte[] body = buf.toByteArray();
            Header header = new Header(
                    Header.TYPE_REQUEST,
                    Header.ACTOR_ADMIN,
                    Header.CODE_ADMIN_REGIST_INFIRMARY_NOTICE,
                    body.length
            );
            outputStream.write(header.getBytes());
            outputStream.write(body);

            titleField.clear();
            contentField.clear();
        } catch (Exception e) {

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