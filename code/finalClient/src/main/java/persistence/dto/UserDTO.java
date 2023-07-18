package persistence.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.*;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class UserDTO implements Serializable {

    private int pk;
    private int infirmary_pk;
    private String status;
    private String id;
    private String pw;
    private String name;
    private String phone_num;
    private String school;

    public UserDTO(int pk, int infirmary_pk_user, String status, String id, String pw, String name, String phone_num, String school)
    {
        this.pk = pk;
        this.infirmary_pk = infirmary_pk_user;
        this.status = status;
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.phone_num = phone_num;
        this.school = school;
    }

    public static UserDTO readUser(DataInputStream inputStream) throws IOException {
        int pk = inputStream.readInt();
        int infimary_pk = inputStream.readInt();
        String status = inputStream.readUTF();
        String id = inputStream.readUTF();
        String pw = inputStream.readUTF();
        String name = inputStream.readUTF();
        String phone_num = inputStream.readUTF();
        String school = inputStream.readUTF();
        return new UserDTO(pk,infimary_pk,status,id,pw,name, phone_num,school);
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeInt(pk);
        dos.writeInt(infirmary_pk);
        dos.writeUTF(status);
        dos.writeUTF(id);
        dos.writeUTF(pw);
        dos.writeUTF(name);
        dos.writeUTF(phone_num);
        dos.writeUTF(school);

        return buf.toByteArray();
    }
}