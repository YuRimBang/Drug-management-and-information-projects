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
import protocol.Header;
import storage.PkStorage;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        try {
            setChoiceBox();
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveBtn.setOnAction(event -> {
            try {
                registerMedAlarm(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    public int getBookmarkPk() throws IOException {
        String ip = Header.ip;
        String medicinefield = medicineField.getValue();
        int pk = storage.getCurPk();
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);
        dos.writeUTF(medicinefield);
        dos.writeInt(pk);
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_USER,
                Header.CODE_USER_GET_USER_BOOKMART_PK,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);

        medSeq = inputStream.readUTF();
        bookmarkPk = inputStream.readInt();

        System.out.println(medSeq + " " + bookmarkPk);

        return bookmarkPk;
    }

    public void updateClock() throws IOException {
        LocalTime currentTime = LocalTime.now(); // 현재 날짜와 시간을 가져옴
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedDateTime = currentTime.format(formatter);
        String formattedAlarmTime = alarmTime.format(formatter);

        if (formattedAlarmTime != null && formattedDateTime.equals(formattedAlarmTime)) {
            showAlarmAlert(alarmTime);
        }
    }

    public void registerMedAlarm(ActionEvent event) throws IOException {
        int result = 0;
        String ip = Header.ip;
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        LocalDate startdate = startDate.getValue();
        LocalDate enddate = endDate.getValue();
        String hour = hourField.getValue();
        String minute = minuteField.getValue();

        // 알람 시간 생성
        alarmTime = LocalTime.of(Integer.parseInt(hour), Integer.parseInt(minute), 00);

        MedicineAlarmDTO medicineAlarmDTO = new MedicineAlarmDTO();
        System.out.println(storage.getCurPk());
        medicineAlarmDTO.setUser_pk(storage.getCurPk());
        medicineAlarmDTO.setBookmark_pk(getBookmarkPk()); // bookmark pk 위에 메서드 있어

        medicineAlarmDTO.setStart_period(startdate);
        medicineAlarmDTO.setEnd_period(enddate);
        medicineAlarmDTO.setTime(alarmTime);

        takingDay = ChronoUnit.DAYS.between(startdate, enddate) + 1; //오늘도 포함

        dos.write(medicineAlarmDTO.getBytes());
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_USER,
                Header.CODE_USER_SETTING_DOSE_MEDICINE,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);

        result = inputStream.readInt();


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

    public void checkEndTakingDay() throws IOException {
        System.out.println("takingCnt: " + takingCnt +",takingDay: " + takingDay);
        if(takingCnt == takingDay) //if문이 안 잡힘
        {
            String ip = storage.getIp();

            Socket socket = new Socket(ip, 4000);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buf);
            dos.writeInt(storage.getCurPk());
            dos.writeInt(bookmarkPk);
            dos.writeInt(alarmTime.getHour());
            dos.writeInt(alarmTime.getMinute());
            dos.writeInt(alarmTime.getSecond());
            byte[] body = buf.toByteArray();
            Header header = new Header(
                    Header.TYPE_REQUEST,
                    Header.ACTOR_USER,
                    Header.CODE_USER_CHECK_END_TAKING_DAY,
                    body.length
            );
            outputStream.write(header.getBytes());
            outputStream.write(body);
            stop();
        }
    }

    private void showAlarmAlert(LocalTime time) throws IOException {
        System.out.println("전"+takingCnt);
        takingCnt++;
        System.out.println("후"+takingCnt);
        try{
            Platform.runLater(() -> {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("복용 알림");
                alert.setHeaderText(medicineField.getValue() + " 약 복용 알림");
                alert.setContentText("물은 한잔 이상 드세요");
                alert.showAndWait();
            });
        }catch (Exception e)
        {
            System.out.println(e);
        }
        checkEndTakingDay();
    }

    public void start() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            try {
                updateClock();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stop()
    {
        timeline.stop();
    }

    public void setChoiceBox() throws IOException {
        final int HOURSTART = 0 , HOUREND = 23;
        final int MINUTESTART = 0, MINUTEEND = 59;

        for(int i = HOURSTART; i <= HOUREND; i++) {
            hourField.getItems().add(String.valueOf(i));
        }

        for(int i = MINUTESTART; i <= MINUTEEND; i++) {
            minuteField.getItems().add(String.valueOf(i));
        }

        String ip = storage.getIp();

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
                Header.CODE_USER_SET_CHOICE_BOX,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);

        int size = inputStream.readInt();
        for(int i = 0; i<size; i++)
        {
            String medicineName = inputStream.readUTF();
            medicineField.getItems().add(medicineName);
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