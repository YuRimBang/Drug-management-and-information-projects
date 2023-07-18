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
import storage.PkStorage;

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
        viewMedicineNameList();
    }

    public ListView<String> getMedicineNameListField() {
        return medicineNameListField;
    }

    public void viewMedicineNameList() {
        MedicineBookmarkDAO medicineBookmarkDAO = new MedicineBookmarkDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<Integer> resultList = medicineBookmarkDAO.selectMedicinePk(storage.getCurPk());

        medicineNameList.clear();
        try {
            for (Integer medicine : resultList) {
                MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
                String medicineName = medicineDAO.inquiryMedicineNameBySeqNum(String.valueOf(medicine));
                medicineNameList.add(medicineName);
            }
            medicineNameListField.refresh();
        } catch (Exception e) {
        }
    }

    public void viewMedicineInfo() {
        String selectedMedicine = medicineNameListField.getSelectionModel().getSelectedItem();
        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<RefineData> resultList = medicineDAO.inquiryMedicineByName(selectedMedicine);

        medicineInfo.clear();
        try {
            String list = "주성분: " + splitString(resultList.get(0).getMainIngr())
                    + "분류: " + resultList.get(0).getClassName() + "\n"
                    + "효능효과: " + splitString(resultList.get(0).getEfficacy())
                    + "사용법: " + splitString(resultList.get(0).getUseMethod())
                    + "주의사항: " + splitString(resultList.get(0).getCaution())
                    + "상호작용: " + splitString(resultList.get(0).getIntrc()) + "\n"
                    + "부작용: " + splitString(resultList.get(0).getSideEffect()) + "\n"
                    + "--------------------------------------------------------------------------------------------";
            list = list.replace("null", "");
            medicineInfo.add(list);
        } catch (Exception e) {
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
