package persistence.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class MedicineAlarmDTO implements Serializable {

    private int pk;
    private int bookmark_pk;
    private int user_pk;
    private LocalDate start_period;
    private LocalDate end_period;
    private LocalTime time;

    public MedicineAlarmDTO(int pk, int bookmark_pk, int user_pk, LocalDate start_peroid, LocalDate end_peroid, LocalTime time) {
        this.pk = pk;
        this.bookmark_pk = bookmark_pk;
        this.user_pk = user_pk;
        this.start_period = start_peroid;
        this.end_period = end_peroid;
        this.time = time;
    }

    public static void sendLocalTime(DataOutputStream dos, LocalTime time) throws IOException {
        int hour = time.getHour();
        int minute = time.getMinute();
        int second = time.getSecond();

        dos.writeInt(hour);
        dos.writeInt(minute);
        dos.writeInt(second);
    }
    public static void sendLocalDate(DataOutputStream dos, LocalDate date) throws IOException {
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        dos.writeInt(year);
        dos.writeInt(month);
        dos.writeInt(day);
    }
    public static LocalDate readLocalDate(DataInputStream inputStream) throws IOException {
        int year = inputStream.readInt();
        int month = inputStream.readInt();
        int day = inputStream.readInt();

        return LocalDate.of(year, month, day);
    }
    public static LocalTime readLocalTime(DataInputStream inputStream) throws IOException {
        int hour = inputStream.readInt();
        int minute = inputStream.readInt();
        int second = inputStream.readInt();

        return LocalTime.of(hour, minute, second);
    }
    public static MedicineAlarmDTO readMedicineAlarm(DataInputStream inputStream) throws IOException {
        int pk = inputStream.readInt();
        int bookmark_pk = inputStream.readInt();
        int user_pk = inputStream.readInt();
        LocalDate start_period = readLocalDate(inputStream);
        LocalDate end_period = readLocalDate(inputStream);
        LocalTime time = readLocalTime(inputStream);

        return new MedicineAlarmDTO(pk,bookmark_pk,user_pk,start_period,end_period,time);
    }

    public byte[] getBytes() throws IOException
    {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeInt(pk);
        dos.writeInt(bookmark_pk);
        dos.writeInt(user_pk);
        sendLocalDate(dos,start_period);
        sendLocalDate(dos,end_period);
        sendLocalTime(dos, time);

        return buf.toByteArray();
    }
}