package persistence.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

@Getter
@Setter
@NoArgsConstructor
public class InfirmaryAlarmDTO {
    private int pk;
    private int infirmary_pk;
    private String notification_title;
    private String alarm_content;
    private LocalDateTime transfer_time;

    public InfirmaryAlarmDTO(int pk, int infirmary_pk, String notification_title, String alarm_content, LocalDateTime transfer_time) {
        this.pk = pk;
        this.infirmary_pk = infirmary_pk;
        this.notification_title = notification_title;
        this.alarm_content = alarm_content;
        this.transfer_time = transfer_time;
    }



    //    public InfirmaryAlarmDTO(int infirmary_pk, String notification_title, String alarm_content, LocalDateTime transfer_time) {
//        this.infirmary_pk = infirmary_pk;
//        this.notification_title = notification_title;
//        this.alarm_content = alarm_content;
//        this.transfer_time = transfer_time;
//    }
    public String toString() {
        return
                "제목: " + notification_title +
                        "\n내용: " + alarm_content +
                        "\n시간: " + transfer_time;
    }
    public static void sendLocalDateTime(DataOutputStream dos, LocalDateTime dateTime) throws IOException {
        long epochSecond = dateTime.toEpochSecond(ZoneOffset.UTC);
        dos.writeLong(epochSecond);
    }
    public static LocalDateTime readLocalDateTime(DataInputStream dis) throws IOException {
        long epochSecond = dis.readLong();
        return LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC);
    }
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeInt(pk);
        dos.writeInt(infirmary_pk);
        dos.writeUTF(notification_title);
        dos.writeUTF(alarm_content);
        sendLocalDateTime(dos, transfer_time);

        return buf.toByteArray();
    }
    public static InfirmaryAlarmDTO readInfirmaryAlarm(DataInputStream inputStream) throws IOException {
        int pk = inputStream.readInt();
        int infirmary_pk = inputStream.readInt();
        String notification_title = inputStream.readUTF();
        String alarm_content = inputStream.readUTF();
        LocalDateTime transfer_time = readLocalDateTime(inputStream);

        return new InfirmaryAlarmDTO(pk, infirmary_pk, notification_title, alarm_content, transfer_time);
    }
}
