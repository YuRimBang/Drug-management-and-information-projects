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
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.InfirmaryMedicineDAO;
import persistence.dao.MedicineBookmarkDAO;
import persistence.dao.MedicineDAO;
import persistence.dao.UserDAO;
import persistence.dto.InfirmaryMedicineDTO;
import persistence.dto.MedicineBookmarkDTO;
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

public class UserViewMedicineStored implements Initializable {
    private PkStorage storage;
    @FXML
    private ListView<String> medicineNameListField;
    private ObservableList<String> medicineNameList;
    @FXML private ListView<String> medicineInfoField;
    private ObservableList<String> medicineInfo;

    private UserSetTakingMedicine userSetTakingMedicine;

    public void setUserSetTakingMedicine(UserSetTakingMedicine userSetTakingMedicine) {
        this.userSetTakingMedicine = userSetTakingMedicine;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        medicineNameList = FXCollections.observableArrayList();
        medicineNameListField.setItems(medicineNameList);

        medicineInfo = FXCollections.observableArrayList();
        medicineInfoField.setItems(medicineInfo);
        try {
            viewMedicineNameList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ListView<String> getMedicineNameListField() {
        return medicineNameListField;
    }

    public void viewMedicineNameList() throws IOException {
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
                Header.CODE_USER_VIEW_SAVE_MEDICINE,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);

        medicineNameList.clear();
        int size = inputStream.readInt();
        for(int i = 0 ; i < size; i++)
        {
            String medicineName = inputStream.readUTF();
            medicineNameList.add(medicineName);
        }
    }

    public void viewMedicineInfo() throws IOException {
        String selectedMedicine = medicineNameListField.getSelectionModel().getSelectedItem();
        String ip = Header.ip;

        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeUTF(selectedMedicine);
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_USER,
                Header.CODE_USER_VIEW_USER_UNIVERCITY_BOOKMART_SELECT_MEDICINE,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);

        medicineInfo.clear();
        int size = inputStream.readInt();
        for(int i = 0; i<size; i++)
        {
            RefineData data = RefineData.readRefineData(inputStream);
            String list = "주성분: " + splitString(data.getMainIngr())
                    + "분류: " + data.getClassName() + "\n"
                    + "효능효과: " + splitString(data.getEfficacy())
                    + "사용법: " + splitString(data.getUseMethod())
                    + "주의사항: " + splitString(data.getCaution())
                    + "상호작용: " + splitString(data.getIntrc()) + "\n"
                    + "부작용: " + splitString(data.getSideEffect()) + "\n"
                    + "--------------------------------------------------------------------------------------------";
            list = list.replace("null", "");
            medicineInfo.add(list);
        }
    }

    public String splitString(String content) {
        StringBuilder result = new StringBuilder();
        int startIndex = 0;
        int endIndex = 20;

        while (startIndex < content.length()) {
            if (endIndex > content.length()) {
                endIndex = content.length();
            }

            String subString = content.substring(startIndex, endIndex);
            result.append(subString).append("\n");

            startIndex += 20;
            endIndex += 20;
        }

        return result.toString();
    }

    public void goToSearchMedicineScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/user/userSearchMedicineUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }

    public void goToSetTakingMedicineScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/user/userSetTakingMedicineUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
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
