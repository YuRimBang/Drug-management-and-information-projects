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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.InfirmaryNoticeDAO;
import persistence.dao.MedicineBookmarkDAO;
import persistence.dao.MedicineDAO;
import persistence.dao.UserDAO;
import persistence.dto.InfirmaryNoticeDTO;
import persistence.dto.MedicineBookmarkDTO;
import persistence.dto.RefineData;
import storage.PkStorage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserSearchMedicine implements Initializable {
    private PkStorage storage;
    @FXML
    private TextField searchField;
    @FXML private Button searchBtn;
    @FXML private Button saveBtn;
    @FXML private Label count;
    @FXML private ListView<String> medicineListField;
    private ObservableList<String> medicineList;
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();

        medicineList = FXCollections.observableArrayList();
        medicineListField.setItems(medicineList);

        searchBtn.setOnAction(event -> viewMedicineList(event));
        saveBtn.setOnAction(event -> saveMedicine(event));
    }

    public void viewMedicineList(ActionEvent event) {
        try {
            String medicineName = searchField.getText();

            MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
            List<RefineData> resultList = medicineDAO.inquiryMedicineByName(medicineName);
            medicineList.clear();
            count.setText(resultList.size() + "개");
            for (RefineData data : resultList) {
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void saveMedicine(ActionEvent event) {
        String selectedAdmin = medicineListField.getSelectionModel().getSelectedItem();
        String arr[] = selectedAdmin.split("\\|");
        String name = arr[1];

        MedicineBookmarkDAO medicineBookmarkDAO = new MedicineBookmarkDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        MedicineBookmarkDTO medicineBookmarkDTO = new MedicineBookmarkDTO();

        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        int infirmary_pk = userDAO.getInfirmary_pk(storage.getCurPk());

        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        String seqNum = medicineDAO.inquiryMedicineSeqNumByName(name);

        medicineBookmarkDTO.setUser_pk(storage.getCurPk());
        medicineBookmarkDTO.setInfirmary_pk(infirmary_pk);
        medicineBookmarkDTO.setMedicine_pk(Integer.parseInt(seqNum));

        int row = medicineBookmarkDAO.registerBookmark(medicineBookmarkDTO);

        if(row == 1) {
            alert.setTitle("즐겨찾기");
            alert.setHeaderText("즐겨찾기 등록 성공");
            alert.setContentText("즐겨찾기 되었습니다.");
        } else {
            alert.setTitle("즐겨찾기");
            alert.setHeaderText("즐겨찾기 등록 실패");
            alert.setContentText("즐겨찾기 실패하였습니다.");
        }
        alert.showAndWait();
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

    public void showImage() throws IOException {
        String selectedAdmin = medicineListField.getSelectionModel().getSelectedItem();
        String arr[] = selectedAdmin.split("\\|");
        String name = arr[1];

        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        String ImgUrl = null;
        ImgUrl = medicineDAO.inquiryMedicineImageByName(name);

        // Alert 다이얼로그 생성
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        if(ImgUrl != null) {
            ImageView imageView = new ImageView();
            imageView.setPreserveRatio(true); // 원본 비율 유지 (Aspect Ratio)

            // 원본 이미지 크기 가져오기
            URL imageUrl = new URL(ImgUrl);
            Image originalImage = new Image(imageUrl.openStream());
            int originalWidth = (int) originalImage.getWidth();
            int originalHeight = (int) originalImage.getHeight();

            // 원하는 크기 계산
            int targetWidth = 500; // 원하는 폭
            int targetHeight = 500 * originalHeight / originalWidth; // Aspect Ratio에 따라 높이 계산

            // 이미지 생성
            Image resizedImage = new Image(imageUrl.openStream(), targetWidth, targetHeight, false, true); // 오류 발생 시, 예외 처리 필요

            // ImageView에 이미지 설정
            imageView.setImage(resizedImage);

            // 이미지 보기
            imageView.setImage(resizedImage);
            alert.setGraphic(imageView);
        } else {
            alert.setContentText("이미지가 존재하지 않습니다.");
        }

        // Alert 다이얼로그 표시
        alert.showAndWait();

    }

    public void goToSearchMedicineUsableTogetherScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/user/userSearchMedicineUsableTogetherUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }

    public void goToSearchMedicineShapeScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/user/userSearchMedicineShapeUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }

    public void goToSearchMedicineElderlyScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/user/userSearchMedicineElderlyUI.fxml"));

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
