package controller.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.InfirmaryDAO;
import persistence.dao.UserDAO;
import persistence.dto.InfirmaryDTO;
import protocol.Header;
import storage.PkStorage;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class UserViewInfirmaryInfo implements Initializable {
    private PkStorage storage;
    @FXML private Text schoolText;
    @FXML private Text locationText;
    @FXML private Text infirmaryPhoneNum;
    @FXML private Text operatingTimeText;
    @FXML private Text nameText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();

        try {
            setInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setInfo() throws IOException {
        String ip = Header.ip;
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeInt(storage.getCurPk());
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_USER,
                Header.CODE_USER_VIEW_USER_UNIVERCITY_INFIRMARY_INFO,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);

        InfirmaryDTO resultInfirmaryDTO = InfirmaryDTO.readInfirmaryInfo(inputStream);

        schoolText.setText(resultInfirmaryDTO.getLocation());
        locationText.setText(resultInfirmaryDTO.getAdmin_name());
        infirmaryPhoneNum.setText(resultInfirmaryDTO.getInfirmary_phone_num());
        operatingTimeText.setText(String.valueOf(resultInfirmaryDTO.getOpen_time()) + " ~ "
                + String.valueOf(resultInfirmaryDTO.getClose_time()));
        nameText.setText(resultInfirmaryDTO.getSchool());

    }

    public void goToPreviousScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/user/userSearchInfimaryMedicineUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }
}