package controller.user;

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
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.InfirmaryDAO;
import persistence.dao.InfirmaryMedicineDAO;
import persistence.dao.MedicineDAO;
import persistence.dao.UserDAO;
import persistence.dto.InfirmaryDTO;
import persistence.dto.InfirmaryMedicineDTO;
import persistence.dto.InfirmaryNoticeDTO;
import persistence.dto.RefineData;
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

public class UserViewInfirmaryMedicineNum implements Initializable {
    private PkStorage storage;
    @FXML private ListView<Object> medicineNumListField;
    private ObservableList<Object> medicineNumList;
    @FXML private ListView<String> medicineListField;
    private ObservableList<String> medicineList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        medicineNumList = FXCollections.observableArrayList();
        medicineNumListField.setItems(medicineNumList);

        medicineList = FXCollections.observableArrayList();
        medicineListField.setItems(medicineList);
        try {
            viewMedicineNumList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void viewMedicineNumList() throws IOException {
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
                Header.CODE_USER_VIEW_USER_UNIVERCITY_INFIRMARY_MEDICINE_QUANTITY,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);

        medicineNumList.clear();

        int size = inputStream.readInt();
        System.out.println(size);
        for(int i = 0; i < size; i++)
        {
            InfirmaryMedicineDTO medicine = InfirmaryMedicineDTO.readInfirmaryMedicine(inputStream);
            String medicineName = inputStream.readUTF();
            medicine.setMedicine_name(medicineName);
            medicineNumList.add(medicine);
        }
        medicineNumListField.refresh();

    }

    public void viewMedicineList(ActionEvent event) {
        try {
            InfirmaryMedicineDTO infirmaryMedicineDTO = (InfirmaryMedicineDTO) medicineNumListField.getSelectionModel().getSelectedItem();
            String ip = Header.ip;
            Socket socket = new Socket(ip, 4000);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buf);

            dos.write(infirmaryMedicineDTO.getBytes());
            byte[] body = buf.toByteArray();
            Header header = new Header(
                    Header.TYPE_REQUEST,
                    Header.ACTOR_USER,
                    Header.CODE_USER_VIEW_USER_UNIVERCITY_INFIRMARY_SELECT_MEDICINE,
                    body.length
            );
            outputStream.write(header.getBytes());
            outputStream.write(body);
            medicineList.clear();

            int size = inputStream.readInt();
            for(int i = 0; i<size; i++)
            {
                RefineData data = RefineData.readRefineData(inputStream);
                String list = "효능효과: " + splitString(data.getEfficacy())
                        + "주의사항: " + splitString(data.getCaution())
                        + "상호작용: " + splitString(data.getIntrc()) + "\n"
                        + "부작용: " + splitString(data.getSideEffect()) + "\n";
                list = list.replace("null", "");
                medicineList.add(list);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String splitString(String content) {
        StringBuilder result = new StringBuilder();
        int startIndex = 0;
        int endIndex = 40;

        while (startIndex < content.length()) {
            if (endIndex > content.length()) {
                endIndex = content.length();
            }

            String subString = content.substring(startIndex, endIndex);
            result.append(subString).append("\n");

            startIndex += 40;
            endIndex += 40;
        }

        return result.toString();
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
