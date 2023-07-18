package controller.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.w3c.dom.Text;
import persistence.MyBatisConnectionFactory;
import persistence.dao.UserDAO;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import persistence.dto.UserDTO;
import protocol.Header;
import storage.PkStorage;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class UserUpdatePw implements Initializable {
    private PkStorage storage;

    @FXML private TextField pwField;
    @FXML private TextField newPwField;
    Alert alert = new Alert(AlertType.INFORMATION);
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
    }

    public void updatePw() throws IOException {
        String ip = storage.getIp();
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        String pw = pwField.getText();
        int curPk = storage.getCurPk();

        checkPw(pw,curPk, outputStream);
        Header header = Header.readHeader(inputStream);
        boolean result = inputStream.readBoolean();
        System.out.println(result);

        if(result) {
            String newPw = newPwField.getText();
            updatePw(curPk,newPw,outputStream);

            alert.setTitle("비밀번호 수정");
            alert.setHeaderText("비밀번호 수정 성공 메시지");
            alert.setContentText("비밀번호가 수정되었습니다.");

            alert.showAndWait();
        } else {
            alert.setTitle("비밀번호 불일치");
            alert.setHeaderText("비밀번호 수정 실패 메시지");
            alert.setContentText("비밀번호가 일치하지 않습니다.");

            alert.showAndWait();
        }

        pwField.clear();
        newPwField.clear();
    }

    public void checkPw(String pw, int curPw, DataOutputStream outputStream) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);
        dos.writeUTF(pw);
        dos.writeInt(curPw);
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_USER,
                Header.CODE_USER_CHECK_PW,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);
    }
    public void updatePw(int curPk, String newPw,DataOutputStream outputStream) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);
        dos.writeInt(curPk);
        dos.writeUTF(newPw);
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_USER,
                Header.CODE_USER_CHANGE_PW,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);
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
