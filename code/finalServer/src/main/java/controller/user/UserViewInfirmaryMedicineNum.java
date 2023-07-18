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
import persistence.dto.InfirmaryMedicineDTO;
import persistence.dto.InfirmaryNoticeDTO;
import persistence.dto.RefineData;
import storage.PkStorage;

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
        viewMedicineNumList();
    }

    public void viewMedicineNumList() {
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        int infirmary_pk = userDAO.getInfirmary_pk(storage.getCurPk());

        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryMedicineDAO infirmaryMedicineDAO = new InfirmaryMedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<InfirmaryMedicineDTO> medicineListResult = infirmaryMedicineDAO.inquiryMedicineStock(infirmary_pk);
        medicineNumList.clear();
        try {
            for (InfirmaryMedicineDTO medicine : medicineListResult) {
                String medicineName = medicineDAO.inquiryMedicineNameBySeqNum(String.valueOf(medicine.getMedicine_pk()));
                System.out.println(medicineName);
                medicine.setMedicine_name(medicineName);
                medicineNumList.add(medicine);
            }
            medicineNumListField.refresh();
        } catch (Exception e) {
        }
    }

    public void viewMedicineList(ActionEvent event) {
        try {
            InfirmaryMedicineDTO infirmaryMedicineDTO = (InfirmaryMedicineDTO) medicineNumListField.getSelectionModel().getSelectedItem();
            MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
            List<RefineData> resultList = medicineDAO.inquiryMedicineByName(infirmaryMedicineDTO.getMedicine_name());
            medicineList.clear();

            for (RefineData data : resultList) {
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
