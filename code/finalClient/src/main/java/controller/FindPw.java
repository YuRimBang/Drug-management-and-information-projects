package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import protocol.Header;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class FindPw implements Initializable {
    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneNumField;
    @FXML
    private TextField idField;
    @FXML
    private ListView<String> pwField;
    private ObservableList<String> pwList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pwList = FXCollections.observableArrayList();
        pwField.setItems(pwList);
    }

    public void findPw(ActionEvent event) throws IOException {
        String ip = Header.ip;
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        String name = nameField.getText();
        String phoneNum = phoneNumField.getText();
        String id = idField.getText();

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);
        dos.writeUTF(name);
        dos.writeUTF(phoneNum);
        dos.writeUTF(id);
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_USER,
                Header.CODE_USER_FIND_PW,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);
        findPwResult(inputStream);
    }
    public void findPwResult(DataInputStream inputStream) throws IOException {
        Header header = Header.readHeader(inputStream);
        String txt = inputStream.readUTF();
        pwList.add(txt);
        pwField.refresh();
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
