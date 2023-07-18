package controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.InfirmaryDAO;
import persistence.dao.InfirmaryNoticeDAO;
import persistence.dao.UserDAO;
import persistence.dto.InfirmaryDTO;
import persistence.dto.InfirmaryNoticeDTO;
import storage.PkStorage;

import javax.xml.soap.Text;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

//AdminRegisterNotice
public class AdminRegisterInfirmary implements Initializable {
    private PkStorage storage;

    @FXML private TextField schoolField;
    @FXML private TextField locationField;
    @FXML private TextField infirmaryPhoneNumField;
    @FXML private ChoiceBox<String> hour1Field;
    @FXML private ChoiceBox<String> minute1Field;
    @FXML private ChoiceBox<String> hour2Field;
    @FXML private ChoiceBox<String> minute2Field;
    @FXML private TextField fileTextField;

    Alert alert = new Alert(Alert.AlertType.INFORMATION);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        infirmaryPhoneNumField.setPromptText("숫자와 '-'로 이루어진 11~13자");

        setChoiceBox();
    }

    public void registerInfirmary(ActionEvent event) {
        InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        String school = schoolField.getText();
        String location = locationField.getText();
        String infirmaryPhoneNum = infirmaryPhoneNumField.getText();
        LocalTime localTime1 = LocalTime.parse(hour1Field.getValue() + ":" + minute1Field.getValue());
        LocalTime localTime2 = LocalTime.parse(hour2Field.getValue() + ":" + minute2Field.getValue());

        // 전화번호 형식 검사 (숫자와 '-'로 이루어진 11~13자)
        if (!infirmaryPhoneNum.matches("^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}$")) {
            showAlert("전화번호 형식이 올바르지 않습니다.", "다시 입력해주세요.");
            return;
        }

        // 전화번호 중복 체크
        if (infirmaryDAO.isPhoneNumDuplicateCheck(infirmaryPhoneNum)) {
            showAlert("전화번호가 이미 등록되어 있습니다.", "다른 전화번호를 입력해주세요.");
            return;
        }

        InfirmaryDTO infirmaryDTO = new InfirmaryDTO();
        infirmaryDTO.setAdmin_pk(storage.getCurPk());

        infirmaryDTO.setSchool(school);
        infirmaryDTO.setLocation(location);
        infirmaryDTO.setInfirmary_phone_num(infirmaryPhoneNum);
        infirmaryDTO.setOpen_time(localTime1);
        infirmaryDTO.setClose_time(localTime2);

        File selectedFile = null;

        if (!fileTextField.getText().isEmpty()){
            selectedFile = new File(fileTextField.getText());
        }

        if (selectedFile != null) {
            try {
                byte[] imageData = convertFileToByteArray(selectedFile);
                infirmaryDTO.setFile(imageData);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(infirmaryDTO);
        InfirmaryDTO check = infirmaryDAO.registerInfirmary(infirmaryDTO);
        infirmaryDTO.setAdmin_name(check.getAdmin_name());

        schoolField.clear();
        locationField.clear();
        infirmaryPhoneNumField.clear();
        fileTextField.clear();
        hour1Field.getSelectionModel().clearSelection();
        minute1Field.getSelectionModel().clearSelection();
        hour2Field.getSelectionModel().clearSelection();
        minute2Field.getSelectionModel().clearSelection();

        alert.setTitle("보건실 등록");
        alert.setHeaderText("보건실 등록 성공");
        alert.setContentText("보건실 등록이 되었습니다.");

        alert.showAndWait();
    }

    private void showAlert(String message, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("입력 오류");
        alert.setHeaderText(message);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setChoiceBox() {
        List<String> list1  = new ArrayList<>();
        list1.addAll(Arrays.asList("08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"));

        List<String> list2  = new ArrayList<>();
        list2.addAll(Arrays.asList("00", "10", "20", "30", "40", "50"));

        for(String item : list1) {
            hour1Field.getItems().add(item);
            hour2Field.getItems().add(item);
        }

        for(String item : list2) {
            minute1Field.getItems().add(item);
            minute2Field.getItems().add(item);
        }
    }

    public void chooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("재직 증명서 선택");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                byte[] imageData = convertFileToByteArray(selectedFile);
                fileTextField.setText(selectedFile.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private byte[] convertFileToByteArray(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] byteArray = new byte[(int) file.length()];
        fileInputStream.read(byteArray);
        fileInputStream.close();
        return byteArray;
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