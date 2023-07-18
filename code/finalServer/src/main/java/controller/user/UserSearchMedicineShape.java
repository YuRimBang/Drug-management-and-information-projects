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
import persistence.dao.MedicineDAO;
import persistence.dto.RefineData;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class UserSearchMedicineShape implements Initializable {

    @FXML
    private TextField markField;
    @FXML private ChoiceBox<String> colorBox;
    @FXML private ChoiceBox<String> shapeBox;
    @FXML private ChoiceBox<String> chartBox;
    @FXML private Button searchBtn;
    @FXML private Label count;
    @FXML
    private ListView<String> medicineListField;
    private ObservableList<String> medicineList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        medicineList = FXCollections.observableArrayList();
        medicineListField.setItems(medicineList);

        setColorBox();
        setShapeBox();
        setChartBox();

        searchBtn.setOnAction(event -> viewMedicineList(event));

    }

    public void goToPreviousScreen(ActionEvent event) {
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

    public void setColorBox() {
        List<String> list  = new ArrayList<>();
        list.addAll(Arrays.asList("하양", "투명", "분홍", "빨강", "자주", "노랑", "주황", "연두", "초록", "청록", "파랑", "남색", "보라", "갈색", "회색", "검정"));

        for(String item : list) {
            colorBox.getItems().add(item);
        }
    }

    public void setShapeBox() {
        List<String> list  = new ArrayList<>();
        list.addAll(Arrays.asList("원형", "타원형", "장방형", "반원형", "마름모", "삼각형", "사각형", "오각형", "육각형", "팔각형"));

        for(String item : list) {
            shapeBox.getItems().add(item);
        }
    }

    public void setChartBox() {
        List<String> list  = new ArrayList<>();
        list.addAll(Arrays.asList("정제", "필름코팅정", "가루", "분말", "당의정", "캡슐", "제피정", "액상"));

        for(String item : list) {
            chartBox.getItems().add(item);
        }
    }

    public void viewMedicineList(ActionEvent event) {
        try {
            RefineData refineData = new RefineData();
            refineData.setPrintFront(markField.getText());
            refineData.setPrintBack(markField.getText());
            refineData.setColorClassFront(colorBox.getValue());
            refineData.setColorClassBack(colorBox.getValue());
            refineData.setDrugShape(shapeBox.getValue());
            refineData.setChart(chartBox.getValue());

            MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
            List<RefineData> resultList = medicineDAO.inquiryMedicineByShape(refineData);
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
        System.out.println(ImgUrl);

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


}
