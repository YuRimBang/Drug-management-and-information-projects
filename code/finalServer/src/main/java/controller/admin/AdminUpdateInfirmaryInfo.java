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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.InfirmaryDAO;
import persistence.dto.InfirmaryDTO;
import storage.PkStorage;

import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class AdminUpdateInfirmaryInfo implements Initializable {
    private PkStorage storage;


    @FXML private TextField infirmaryPhoneNumField;
    @FXML private ChoiceBox<String> hour1Field;
    @FXML private ChoiceBox<String> minute1Field;
    @FXML private ChoiceBox<String> hour2Field;
    @FXML private ChoiceBox<String> minute2Field;

    Alert alert = new Alert(Alert.AlertType.INFORMATION);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        infirmaryPhoneNumField.setPromptText("숫자와 '-'로 이루어진 11~13자");

        setChoiceBox();
    }

    public void updateInfirmaryInfo(ActionEvent event) {
        try {
            InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());

            InfirmaryDTO infirmaryDTO = new InfirmaryDTO();
            infirmaryDTO.setAdmin_pk(storage.getCurPk());

            String infirmaryPhoneNum = infirmaryPhoneNumField.getText();

            String hour1Value = hour1Field.getValue();
            String minute1Value = minute1Field.getValue();

            System.out.println("전화번호:" + infirmaryPhoneNum + ";");
            if(infirmaryPhoneNum != null && !infirmaryPhoneNum.equals("")) {
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

                infirmaryDTO.setInfirmary_phone_num(infirmaryPhoneNum);
            }


            LocalTime localTime1 = null;
            if (hour1Value != null && !hour1Value.isEmpty() && minute1Value != null && !minute1Value.isEmpty()) {
                localTime1 = LocalTime.parse(hour1Value + ":" + minute1Value);
                infirmaryDTO.setOpen_time(localTime1);
            }

            String hour2Value = hour2Field.getValue();
            String minute2Value = minute2Field.getValue();

            LocalTime localTime2 = null;
            if (hour2Value != null && !hour2Value.isEmpty() && minute2Value != null && !minute2Value.isEmpty()) {
                localTime2 = LocalTime.parse(hour2Value + ":" + minute2Value);
                infirmaryDTO.setClose_time(localTime2);
            }


            infirmaryDTO = infirmaryDAO.updateInfirmaryInfo(infirmaryDTO);

            infirmaryPhoneNumField.clear();
            hour1Field.getSelectionModel().clearSelection();
            minute1Field.getSelectionModel().clearSelection();
            hour2Field.getSelectionModel().clearSelection();
            minute2Field.getSelectionModel().clearSelection();


            System.out.println("infirmaryDTO:" + infirmaryDTO.toString());
            alert.setTitle("보건실 정보 수정");
            alert.setHeaderText("보건실 정보 수정 성공");
            alert.setContentText("보건실 정보 수정이 완료되었습니다.");

            alert.showAndWait();


        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
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