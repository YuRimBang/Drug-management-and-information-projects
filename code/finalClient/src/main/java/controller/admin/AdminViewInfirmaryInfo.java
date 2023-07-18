package controller.admin;

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
import persistence.dto.AdminDTO;
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

public class AdminViewInfirmaryInfo implements Initializable {
    private PkStorage storage;

    @FXML
    private Text schoolText;
    @FXML
    private Text locationText;
    @FXML
    private Text infirmaryPhoneNum;
    @FXML
    private Text operatingTimeText;
    @FXML
    private Text nameText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        int pk = storage.getCurPk();
        try {
            setInfo(pk);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setInfo(int pk) throws IOException {
//    InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());
//    InfirmaryDTO infirmaryDTO = new InfirmaryDTO();
//
//    String schoolName = infirmaryDAO.getSchoolName(pk);
//
//    infirmaryDTO.setSchool(schoolName);
//    InfirmaryDTO resultInfirmaryDTO = infirmaryDAO.inquiryInfirmary(infirmaryDTO);
//
//    schoolText.setText(resultInfirmaryDTO.getSchool());
//    locationText.setText(resultInfirmaryDTO.getLocation());
//    infirmaryPhoneNum.setText(resultInfirmaryDTO.getInfirmary_phone_num());
//    operatingTimeText.setText(String.valueOf(resultInfirmaryDTO.getOpen_time()) + " ~ "
//            + String.valueOf(resultInfirmaryDTO.getClose_time()));
//    nameText.setText(resultInfirmaryDTO.getAdmin_name());
        String ip = Header.ip;
        System.out.println(ip);
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        viewInfirmary(pk, outputStream);
        showInfo(inputStream);
    }
    public void viewInfirmary(int pk, DataOutputStream outputStream) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);
        System.out.println(pk);
        dos.writeInt(pk);
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_ADMIN,
                Header.CODE_ADMIN_VIEW_INFIRMARY_INFO,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);
    }

    public void showInfo(DataInputStream inputStream) throws IOException {
        Header header = Header.readHeader(inputStream);
        InfirmaryDTO infirmaryDTO = InfirmaryDTO.readInfirmary(inputStream);
        schoolText.setText(infirmaryDTO.getSchool());
        locationText.setText(infirmaryDTO.getLocation());
        infirmaryPhoneNum.setText(infirmaryDTO.getInfirmary_phone_num());
        operatingTimeText.setText(String.valueOf(infirmaryDTO.getOpen_time()) + " ~ "
                + String.valueOf(infirmaryDTO.getClose_time()));
        nameText.setText(infirmaryDTO.getAdmin_name());
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