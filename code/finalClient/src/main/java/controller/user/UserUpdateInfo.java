package controller.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.UserDAO;
import persistence.dto.InfirmaryDTO;
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

public class UserUpdateInfo implements Initializable {
    private PkStorage storage;

    @FXML private TextField nameField;
    @FXML private TextField phoneNumField;
    @FXML private ChoiceBox<String> schoolBox;

    Alert alert = new Alert(Alert.AlertType.INFORMATION);


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        setSchoolBox();
    }

    public void setSchoolBox() {
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<InfirmaryDTO> list = userDAO.viewSchoolList();

        for(InfirmaryDTO item : list) {
            String schoolName = item.getSchool();
            schoolBox.getItems().add(schoolName);
        }
    }

    public void updateinfo() throws IOException {
        String ip = Header.ip;
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        int pk = storage.getCurPk();
        String name = nameField.getText();
        String phoneNum = phoneNumField.getText();
        String selectedSchool = schoolBox.getValue();

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeInt(pk);
        dos.writeUTF(name != null ? name : "");
        dos.writeUTF(phoneNum != null ? phoneNum : "");
        dos.writeUTF(selectedSchool != null ? selectedSchool : "");

        byte[] body = buf.toByteArray();

        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_USER,
                Header.CODE_USER_CHANGE_MY_INFO,
                body.length
        );

        outputStream.write(header.getBytes());
        outputStream.write(body);

        updateResult(inputStream);

        nameField.clear();
        phoneNumField.clear();
        schoolBox.getSelectionModel().clearSelection();

    }
    public void updateResult(DataInputStream inputStream) throws IOException {
        Header header = Header.readHeader(inputStream);
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
