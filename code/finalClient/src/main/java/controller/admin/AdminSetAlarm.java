package controller.admin;

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
import persistence.dao.InfirmaryAlarmDAO;
import persistence.dao.InfirmaryDAO;
import persistence.dto.InfirmaryAlarmDTO;
import protocol.Header;
import storage.PkStorage;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.net.URL;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AdminSetAlarm implements Initializable {
    private PkStorage storage;

    @FXML private TextField titleField;
    @FXML private TextArea contentField;
    @FXML private DatePicker dateField;
    @FXML private ChoiceBox<String> hourField;
    @FXML private ChoiceBox<String> minuteField;

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    private Timeline timeline;
    private LocalDateTime alarmTime;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        setChoiceBox();
        start();
        alarmTime = LocalDateTime.now();
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

    public void registerAlarm(ActionEvent event) throws IOException {
        String ip = Header.ip;
        int pk = storage.getCurPk();
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        int result = 0;
        try {
            String infirmaryTitle = titleField.getText();
            String infirmaryContent = contentField.getText();
            LocalDate date = dateField.getValue();
            String hour = hourField.getValue();
            String minute = minuteField.getValue();
            // 알람 시간 생성
            alarmTime = LocalDateTime.of(date, LocalTime.of(Integer.parseInt(hour), Integer.parseInt(minute), 00));

            InfirmaryAlarmDTO infirmaryAlarmDTO = new InfirmaryAlarmDTO();
            infirmaryAlarmDTO.setNotification_title(infirmaryTitle);
            infirmaryAlarmDTO.setAlarm_content(infirmaryContent);
            infirmaryAlarmDTO.setTransfer_time(alarmTime);

            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buf);

            dos.write(infirmaryAlarmDTO.getBytes());
            dos.writeInt(pk);
            byte[] body = buf.toByteArray();
            Header header = new Header(
                    Header.TYPE_REQUEST,
                    Header.ACTOR_ADMIN,
                    Header.CODE_ADMIN_NOTIFICATION_SETTING,
                    body.length
            );
            outputStream.write(header.getBytes());
            outputStream.write(body);

        } catch (Exception e) {
            System.out.println(e+ "여기");
        }
        result = inputStream.readInt();
        if(result==1)
        {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("알림");
            alert.setHeaderText("알림 설정 완료");
            alert.setContentText("알림 설정이 완료 되었습니다");
            alert.showAndWait();
        }else
        {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("알림");
            alert.setHeaderText("알림 설정 실패");
            alert.setContentText("알림 설정을 실패했습니다.");
            alert.showAndWait();
        }
    }

    public void updateClock() throws IOException {
        LocalDateTime currentTime = LocalDateTime.now(); // 현재 날짜와 시간을 가져옴
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentTime.format(formatter);
        String formattedAlarmTime = alarmTime.format(formatter);

        if (formattedAlarmTime != null && formattedDateTime.equals(formattedAlarmTime)) {
            showAlarmAlert(alarmTime);
        }
    }

    public void showAlarmAlert(LocalDateTime time) throws IOException {
        String ip = Header.ip;

        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        stop();
        try{
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buf);

            long epochSecond = time.toEpochSecond(ZoneOffset.UTC);
            dos.writeLong(epochSecond);
            dos.writeInt(storage.getCurPk());
            byte[] body = buf.toByteArray();
            Header header = new Header(
                    Header.TYPE_REQUEST,
                    Header.ACTOR_ADMIN,
                    Header.CODE_ADMIN_SHOW_ALARM,
                    body.length
            );
            outputStream.write(header.getBytes());
            outputStream.write(body);


            InfirmaryAlarmDTO finalInfirmaryAlarmDTO = InfirmaryAlarmDTO.readInfirmaryAlarm(inputStream);

            Platform.runLater(() -> {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("알림");
                alert.setHeaderText(finalInfirmaryAlarmDTO.getNotification_title());
                alert.setContentText(finalInfirmaryAlarmDTO.getAlarm_content());
                alert.showAndWait();
            });

        }catch (Exception e)
        {
            System.out.println(e);
        }

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