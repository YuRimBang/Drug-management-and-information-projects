package controller.user;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import persistence.MyBatisConnectionFactory;
import persistence.dao.*;
import persistence.dto.InfirmaryAlarmDTO;
import persistence.dto.MedicineAlarmDTO;
import storage.PkStorage;

import java.net.URL;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

public class UserSetTakingMedicine implements Initializable {

    private PkStorage storage;
    @FXML private ChoiceBox<String> medicineField;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private ChoiceBox<String> hourField;
    @FXML private ChoiceBox<String> minuteField;
    @FXML private Button saveBtn;
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    private Timeline timeline;
    private LocalTime alarmTime;
    private long takingDay;
    private long takingCnt;
    private String medSeq;
    private int bookmarkPk;

    MedicineAlarmDAO medicineAlarmDAO = new MedicineAlarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());
    InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());
    InfirmaryAlarmDAO infirmaryAlarmDAO = new InfirmaryAlarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());
    MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
    MedicineBookmarkDAO medicineBookmarkDAO = new MedicineBookmarkDAO(MyBatisConnectionFactory.getSqlSessionFactory());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        setChoiceBox();

        saveBtn.setOnAction(event -> registerMedAlarm(event));
    }

    public int getBookmarkPk() {

        medSeq = medicineDAO.inquiryMedicineSeqNumByName(medicineField.getValue());

        MedicineBookmarkDAO medicineBookmarkDAO = new MedicineBookmarkDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        bookmarkPk = medicineBookmarkDAO.selectMedBookmarkPk(medSeq, storage.getCurPk());

        System.out.println(medSeq + " " + bookmarkPk);

        return bookmarkPk;
    }

    public void updateClock() {
        LocalTime currentTime = LocalTime.now(); // 현재 날짜와 시간을 가져옴
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedDateTime = currentTime.format(formatter);
        String formattedAlarmTime = alarmTime.format(formatter);

        if (formattedAlarmTime != null && formattedDateTime.equals(formattedAlarmTime)) {
            showAlarmAlert(alarmTime);
        }
    }

    public void registerMedAlarm(ActionEvent event)
    {
        int result = 0;
        try {

            LocalDate startdate = startDate.getValue();
            LocalDate enddate = endDate.getValue();
            String hour = hourField.getValue();
            String minute = minuteField.getValue();

            // 알람 시간 생성
            alarmTime = LocalTime.of(Integer.parseInt(hour), Integer.parseInt(minute), 00);

            MedicineAlarmDTO medicineAlarmDTO = new MedicineAlarmDTO();
            medicineAlarmDTO.setUser_pk(storage.getCurPk());
            medicineAlarmDTO.setBookmark_pk(getBookmarkPk()); // bookmark pk 위에 메서드 있어

            medicineAlarmDTO.setStart_period(startdate);
            medicineAlarmDTO.setEnd_period(enddate);
            medicineAlarmDTO.setTime(alarmTime);

            takingDay = ChronoUnit.DAYS.between(startdate, enddate) + 1; //오늘도 포함

            result = medicineAlarmDAO.insertMedicineAlarm(medicineAlarmDTO);;

        } catch (Exception e) {
            System.out.println(e);
        }

        if(result==1)
        {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("알림");
            alert.setHeaderText("알림 설정 완료");
            alert.setContentText("알림 설정이 완료 되었습니다");
            alert.showAndWait();
            takingCnt = 0;
            start();
        }else
        {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("알림");
            alert.setHeaderText("알림 설정 실패");
            alert.setContentText("알림 설정을 실패했습니다.");
            alert.showAndWait();
        }
    }

    public void checkEndTakingDay()
    {
        System.out.println("takingCnt: " + takingCnt +",takingDay: " + takingDay);
        if(takingCnt == takingDay) //if문이 안 잡힘
        {
            MedicineAlarmDTO medicineAlarmDTO = new MedicineAlarmDTO();

            //테이블 검색
            int alarmPk = medicineAlarmDAO.selectUserAlarm(storage.getCurPk(), bookmarkPk, alarmTime);
            System.out.println("alaramPk: " +alarmPk);
            //테이블 삭제
            medicineAlarmDAO.deleteUserAlarm(alarmPk);

            stop();
        }
    }

    private void showAlarmAlert(LocalTime time) {
        System.out.println("전"+takingCnt);
        takingCnt++;
        System.out.println("후"+takingCnt);
        try{
            InfirmaryAlarmDTO infirmaryAlarmDTO = new InfirmaryAlarmDTO();
            //infirmaryAlarmDTO = infirmaryAlarmDAO.inquiryAlarmToTime(time, infirmaryDAO.getInfirmaryPk(storage.getCurPk()));
            //InfirmaryAlarmDTO finalInfirmaryAlarmDTO = infirmaryAlarmDTO;

            Platform.runLater(() -> {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("복용 알림");
                alert.setHeaderText(medicineField.getValue() + " 약 복용 알림");
                alert.setContentText("물은 한잔 이상 드세여");
                alert.showAndWait();
            });
        }catch (Exception e)
        {
            System.out.println(e);
        }
        checkEndTakingDay();
    }

    public void start() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateClock()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stop()
    {
        timeline.stop();
    }

    public void setChoiceBox()
    {
        final int HOURSTART = 0 , HOUREND = 23;
        final int MINUTESTART = 0, MINUTEEND = 59;

        for(int i = HOURSTART; i <= HOUREND; i++) {
            hourField.getItems().add(String.valueOf(i));
        }

        for(int i = MINUTESTART; i <= MINUTEEND; i++) {
            minuteField.getItems().add(String.valueOf(i));
        }

        List<Integer> resultList = medicineBookmarkDAO.selectMedicinePk(storage.getCurPk());

        try {
            for (Integer medicine : resultList) {
                MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
                String medicineName = medicineDAO.inquiryMedicineNameBySeqNum(String.valueOf(medicine));
                medicineField.getItems().add(medicineName);
            }
        } catch (Exception e) {
        }

    }

    public void goToPreviousScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/user/userViewMedicineStoredUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }
}