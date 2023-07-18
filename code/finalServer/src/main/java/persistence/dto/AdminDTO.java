package persistence.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
@Setter
@NoArgsConstructor
public class AdminDTO {
    private int pk;
    private int infirmary_pk;
    private String status;
    private String id;
    private String pw;
    private String name;
    private String phone_num;
    private boolean certificate_check;
    private byte[] file;

    public AdminDTO(String id, String pw, String name, String phone_num, boolean certificate_check, byte[] file)
    {
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.phone_num = phone_num;
        this.certificate_check = certificate_check;
        this.file = file;
    }

    public String toString() {
        return
                "pk: " + pk +
                "아이디: " + id +
                        "\n이름: " + name +
                        "\n전화번호: " + phone_num +
                        "\ncertificate_check: " + certificate_check + "\n 이미지 : " + file;
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeInt(pk);
        dos.writeUTF(id);
        dos.writeUTF(pw);
        dos.writeUTF(name);
        dos.writeUTF(phone_num);
//        dos.writeUTF(file);
        return buf.toByteArray();
    }
}
