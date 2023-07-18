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
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.InfirmaryDAO;
import persistence.dao.InfirmaryMedicineDAO;
import persistence.dto.AdminDTO;
import persistence.dto.InfirmaryMedicineDTO;
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

public class AdminDeleteMedicine implements Initializable {

    private PkStorage storage;
    @FXML
    private ListView<Object> medicineListField;
    private ObservableList<Object> medicineList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        medicineList = FXCollections.observableArrayList();
        medicineListField.setItems(medicineList);

        try {
            viewMedicineList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void viewMedicineList() throws IOException {
//        int pk = storage.getCurPk();
//
//        String ip = storage.getIp();
//        System.out.println(ip);
//        Socket socket = new Socket(ip, 4000);
//        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
//        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
//        ByteArrayOutputStream buf = new ByteArrayOutputStream();
//        DataOutputStream dos = new DataOutputStream(buf);
//
//        dos.writeInt(pk);
//        byte[] body = buf.toByteArray();
//        Header header = new Header(
//                Header.TYPE_REQUEST,
//                Header.ACTOR_ADMIN,
//                Header.CODE_ADMIN_VIEW_INFIRMARY_MEDICINE,
//                body.length
//        );
//        outputStream.write(header.getBytes());
//        outputStream.write(body);
//
//        int size = inputStream.readInt();
//        if(size > 0){
//            for(int i=0; i<size; i++){
//                InfirmaryMedicineDTO infirmaryMedicineDTO = InfirmaryMedicineDTO.readInfirmaryMedicine(inputStream);
//                medicineList.add(infirmaryMedicineDTO);
//            }
//            medicineListField.refresh();
//        }
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

        medicineList.clear();
        int size = inputStream.readInt();
        System.out.println(size);
        if(size > 0){
            for(int i=0; i<size; i++){
                InfirmaryMedicineDTO infirmaryMedicineDTO = InfirmaryMedicineDTO.readInfirmaryMedicine(inputStream);
                infirmaryMedicineDTO.setMedicine_name(inputStream.readUTF());
                medicineList.add(infirmaryMedicineDTO);
            }
            medicineListField.refresh();
        }
    }
    public void deleteMedicine() throws IOException {
        String ip = storage.getIp();

        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        int pk = storage.getCurPk();

        InfirmaryMedicineDTO infirmaryMedicineDTO = (InfirmaryMedicineDTO) medicineListField.getSelectionModel().getSelectedItem();
        int selectMedicinePk = infirmaryMedicineDTO.getPk();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeInt(pk);
        dos.writeInt(selectMedicinePk);
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_ADMIN,
                Header.CODE_ADMIN_DELETE_MEDICINE,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);
//        infirmaryMedicineDAO.deleteInfirmaryMedicine(infirmaryMedicineDTO.getPk());
//
//        List<InfirmaryMedicineDTO> infirmaryMedicineDTOS = infirmaryMedicineDAO.selectAllByInfirmaryPk(infirmary_pk);
        medicineList.clear();
        int size = inputStream.readInt();
        if(size > 0)
        {
            for (int i = 0; i < size; i++) {
                InfirmaryMedicineDTO infirmaryMedicineDTO1 = InfirmaryMedicineDTO.readInfirmaryMedicine(inputStream);
                String medicineName = inputStream.readUTF();
                infirmaryMedicineDTO1.setMedicine_name(medicineName);
                medicineList.add(infirmaryMedicineDTO1);
            }
        }
        medicineListField.setItems(medicineList);
        medicineListField.getSelectionModel().clearSelection();
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