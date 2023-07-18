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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.ibatis.session.SqlSessionFactory;
import persistence.MyBatisConnectionFactory;
import persistence.dao.InfirmaryMedicineDAO;
import persistence.dao.MedicineDAO;
import persistence.dao.UserDAO;
import persistence.dto.InfirmaryMedicineDTO;
import persistence.dto.RefineData;
import persistence.dto.UserDTO;
import protocol.Header;
import storage.PkStorage;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UserSearchInfirmaryMedicine implements Initializable {
    private PkStorage storage;

    @FXML private Label count;
    @FXML
    private Button searchBtn;
    @FXML
    private TextField searchField;
    @FXML
    private ListView<Object> medicineListField;
    private ObservableList<Object> medicineList;


    UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
    private InfirmaryMedicineDAO infirmaryMedicineDAO;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();


        medicineList = FXCollections.observableArrayList();
        medicineListField.setItems(medicineList);

        searchBtn.setOnAction(event -> {
            try {
                viewMedicineList();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

public void viewMedicineList() throws IOException {
    int pk = storage.getCurPk();
    String medicineName = searchField.getText();
    String ip = Header.ip;
    System.out.println(ip);
    Socket socket = new Socket(ip, 4000);
    DataInputStream inputStream = new DataInputStream(socket.getInputStream());
    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(buf);

    dos.writeInt(pk);
    dos.writeUTF(medicineName);
    byte[] body = buf.toByteArray();
    Header header = new Header(
            Header.TYPE_REQUEST,
            Header.ACTOR_USER,
            Header.CODE_USER_SEARCH_USER_UNIVERCITY_INFIRMARY_MEDICINE,
            body.length
    );
    outputStream.write(header.getBytes());
    outputStream.write(body);
    medicineList.clear();
    count.setText("0개");

    int size = inputStream.readInt();
    count.setText(size + "개");
    int size2 = inputStream.readInt();
    if(size2 > 0){
        for(int i=0; i<size; i++){
            RefineData data = RefineData.readRefineData(inputStream);
            String list = "품목명: |" + data.getItemName() + "|\n"
                    + "업체명: " + data.getEntpName() + "\n"
                    + "주성분: " + splitString(data.getMainIngr())
                    + "분류: " + data.getClassName() + "\n"
                    + "전문/일반: " + data.getEtcOtcName() + "\n"
                    + "효능효과: " + splitString(data.getEfficacy())
                    + "사용법: " + splitString(data.getUseMethod())
                    + "주의사항: " + splitString(data.getCaution())
                    + "상호작용: " + splitString(data.getIntrc()) + "\n"
                    + "부작용: " + splitString(data.getSideEffect()) + "\n"
                    + "--------------------------------------------------------------------------------------------";
            list = list.replace("null", "");
            medicineList.add(list);
        }
        medicineListField.refresh();
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


    public void goToUserViewInfimaryInfoScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/user/userViewInfirmaryInfoUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }

    public void goToUserViewInfimaryNoticeScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/user/userViewInfimaryNoticeUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }

    public void goToUserViewInfimaryMedicineNumScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/user/userViewInfimaryMedicineNumUI.fxml"));

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
