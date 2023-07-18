package controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.*;
import persistence.dto.InfirmaryDTO;
import persistence.dto.InfirmaryMedicineDTO;
import persistence.dto.InfirmaryNoticeDTO;
import persistence.dto.UserDTO;
import protocol.Header;
import storage.PkStorage;

import javax.xml.soap.Text;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminUpdateMedicineNum implements Initializable {
    private PkStorage storage;
    @FXML private ListView<Object> medicineNumListField;
    private ObservableList<Object> medicineNumList;
    @FXML private TextField stockField;
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();

        medicineNumList = FXCollections.observableArrayList();
        medicineNumListField.setItems(medicineNumList);

        try {
            viewMedicineNumList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void viewMedicineNumList() throws IOException {
        int pk = storage.getCurPk();
        String ip = Header.ip;
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeInt(pk);
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_ADMIN,
                Header.CODE_ADMIN_VIEW_INFIRMARY_MEDICINE,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);

        medicineNumList.clear();
        int size = inputStream.readInt();
        System.out.println(size);
        if(size > 0){
            for(int i=0; i<size; i++){
                InfirmaryMedicineDTO infirmaryMedicineDTO = InfirmaryMedicineDTO.readInfirmaryMedicine(inputStream);
                infirmaryMedicineDTO.setMedicine_name(inputStream.readUTF());
                medicineNumList.add(infirmaryMedicineDTO);
            }
            medicineNumListField.refresh();
        }
    }

    public void updateMedicineNum() throws IOException {
        String ip = storage.getIp();

        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        InfirmaryMedicineDTO selectedItem = (InfirmaryMedicineDTO) medicineNumListField.getSelectionModel().getSelectedItem();
        int stock = Integer.parseInt(stockField.getText());
        int option = 1;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.write(selectedItem.getBytes());
        dos.writeInt(stock);
        dos.writeInt(option);
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_ADMIN,
                Header.CODE_ADMIN_CHANGE_MEDICINE_QUANTITY,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);

        selectedItem.setMedicine_stock(stock);

        int row = inputStream.readInt();

        if(row == 1) {
            stockField.clear();
            viewMedicineNumList();

            alert.setTitle("약 수량 수정");
            alert.setHeaderText("약 수량 수정 성공");
            alert.setContentText("약 수량 수정되었습니다.");

            alert.showAndWait();
        }

    }
    public void plusMedicineNum() throws IOException {
        String ip = storage.getIp();

        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());;

        InfirmaryMedicineDTO selectedItem = (InfirmaryMedicineDTO) medicineNumListField.getSelectionModel().getSelectedItem();

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);
        int stock = 0;
        int option = 2;
        dos.write(selectedItem.getBytes());
        dos.writeInt(stock);
        dos.writeInt(option);
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_ADMIN,
                Header.CODE_ADMIN_CHANGE_MEDICINE_QUANTITY,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);
        int row = inputStream.readInt();

        if(row == 1) {
            viewMedicineNumList();
        }
    }

    public void minusMedicineNum() throws IOException {
        String ip = storage.getIp();

        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());;

        InfirmaryMedicineDTO selectedItem = (InfirmaryMedicineDTO) medicineNumListField.getSelectionModel().getSelectedItem();

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);
        int stock = 0;
        int option = 3;
        dos.write(selectedItem.getBytes());
        dos.writeInt(stock);
        dos.writeInt(option);
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_ADMIN,
                Header.CODE_ADMIN_CHANGE_MEDICINE_QUANTITY,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);
        int row = inputStream.readInt();

        if(row == 1) {
            viewMedicineNumList();
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