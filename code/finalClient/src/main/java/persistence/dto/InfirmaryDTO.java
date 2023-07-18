package persistence.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class InfirmaryDTO {
    private int pk;
    private int admin_pk;
    private String school;
    private String location;
    private String admin_name;
    private String admin_phone_num;
    private String infirmary_phone_num;
    private boolean infirmary_certificate_check;
    private LocalTime open_time;
    private LocalTime close_time;
    private byte[] file;


    public InfirmaryDTO(int pk, int admin_pk, String school, String location, String infirmary_phone_num,
                        boolean infirmary_certificate_check, LocalTime open_time, LocalTime close_time, byte[] file)
    {
        this.pk = pk;
        this.admin_pk = admin_pk;
        this.school = school;
        this.location = location;
        this.infirmary_phone_num = infirmary_phone_num;
        this.infirmary_certificate_check = infirmary_certificate_check;
        this.open_time = open_time;
        this.close_time = close_time;
        this.file = file;
    }
    public InfirmaryDTO(int pk,int admin_pk, String school, String location,String admin_name, String infirmary_phone_num,
                        boolean infirmary_certificate_check, LocalTime open_time, LocalTime close_time, byte[] file)
    {
        this.pk = pk;
        this.admin_pk = admin_pk;
        this.school = school;
        this.location = location;
        this.admin_name = admin_name;
        this.infirmary_phone_num = infirmary_phone_num;
        this.infirmary_certificate_check = infirmary_certificate_check;
        this.open_time = open_time;
        this.close_time = close_time;
        this.file = file;
    }



    public String toString() {
        return
                "pk: " + pk +
                        "\nadmin_pk: " + admin_pk +
                        "\n학교: " + school +
                        "\n위치: " + location +
                        "\n운영자 이름: " + admin_name +
                        "\n운영자 전화번호: " + admin_phone_num +
                        "\n보건실 전화번호: " + infirmary_phone_num +
                        "\n운영 시간: " + open_time + " ~ " + close_time +
                        "\n보건실 승인: " + infirmary_certificate_check;
    }

    public static void sendLocalTime(DataOutputStream dos, LocalTime time) throws IOException {
        int hour = time.getHour();
        int minute = time.getMinute();
        int second = time.getSecond();

        dos.writeInt(hour);
        dos.writeInt(minute);
        dos.writeInt(second);
    }

    public static LocalTime receiveLocalTime(DataInputStream dis) throws IOException {
        int hour = dis.readInt();
        int minute = dis.readInt();
        int second = dis.readInt();

        return LocalTime.of(hour, minute, second);
    }

    public InfirmaryDTO(int pk, int admin_pk, String school, String location, String admin_name,String infirmary_phone_num, boolean infirmary_certificate_check, LocalTime open_time, LocalTime close_time) {
        this.pk = pk;
        this.admin_pk = admin_pk;
        this.school = school;
        this.location = location;
        this.admin_name = admin_name;
        this.infirmary_phone_num = infirmary_phone_num;
        this.infirmary_certificate_check = infirmary_certificate_check;
        this.open_time = open_time;
        this.close_time = close_time;
    }

    public static InfirmaryDTO readInfirmary(DataInputStream inputStream) throws IOException {
        int pk = inputStream.readInt();
        int admin_pk = inputStream.readInt();
        String school = inputStream.readUTF();
        String location = inputStream.readUTF();
        String admin_name = inputStream.readUTF();
        String infirmary_phone_num = inputStream.readUTF();
        boolean infirmary_certificate_check = inputStream.readBoolean();
        LocalTime open_time = receiveLocalTime(inputStream);
        LocalTime close_time = receiveLocalTime(inputStream);
        int size = inputStream.readInt();
        byte[] file = new byte[size];
        inputStream.readFully(file);
        // 읽은 값들로 InfirmaryDTO 객체를 생성하고 반환합니다
        return new InfirmaryDTO(pk, admin_pk, school, location, admin_name,infirmary_phone_num, infirmary_certificate_check, open_time, close_time, file);
    }
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeInt(pk);
        dos.writeInt(admin_pk);
        dos.writeUTF(school);
        dos.writeUTF(location);
        dos.writeUTF(admin_name);
        dos.writeUTF(infirmary_phone_num);
        dos.writeBoolean(infirmary_certificate_check);
        sendLocalTime(dos, open_time);
        sendLocalTime(dos, close_time);
        dos.writeInt(file.length);
        dos.write(file);

        return buf.toByteArray();
    }
    public byte[] getInfirmaryByte() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeUTF(admin_name);
        dos.writeUTF(school);
        dos.writeUTF(location);
        dos.writeUTF(infirmary_phone_num);
        sendLocalTime(dos, open_time);
        sendLocalTime(dos, close_time);

        return buf.toByteArray();
    }
    public static InfirmaryDTO readInfirmaryInfo(DataInputStream inputStream) throws IOException {
        String admin_name = inputStream.readUTF();
        String school = inputStream.readUTF();
        String location = inputStream.readUTF();
        String infirmary_phone_num = inputStream.readUTF();
        LocalTime open_time = receiveLocalTime(inputStream);
        LocalTime close_time = receiveLocalTime(inputStream);

        return new InfirmaryDTO(admin_name, school, location, infirmary_phone_num, open_time, close_time);
    }
    public InfirmaryDTO(String school, String location, String admin_name, String infirmary_phone_num, LocalTime open_time, LocalTime close_time) {
        this.school = school;
        this.location = location;
        this.admin_name = admin_name;
        this.infirmary_phone_num = infirmary_phone_num;
        this.open_time = open_time;
        this.close_time = close_time;
    }
}
