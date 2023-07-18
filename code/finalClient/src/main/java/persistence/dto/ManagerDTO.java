package persistence.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import protocol.MySerializableClass;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ManagerDTO implements MySerializableClass
{
    private int pk;
    private String name;
    private String id;
    private String pw;
    private String phone_num;
    private String status;

    public ManagerDTO(int pk, String name, String id, String pw, String phone_num, String status)
    {
        this.pk = pk;
        this.name = name;
        this.id = id;
        this.pw = pw;
        this.phone_num = phone_num;
        this.status = status;
    }

    public ManagerDTO(String name, String id, String pw, String phone_num, String status)
    {
        this.name = name;
        this.id = id;
        this.pw = pw;
        this.phone_num = phone_num;
        this.status = status;
    }

    public static ManagerDTO readManager(DataInputStream dis) throws IOException
    {
        int pk = dis.readInt();
        String name = dis.readUTF();
        String id = dis.readUTF();
        String pw = dis.readUTF();
        String phone_num = dis.readUTF();
        String status = dis.readUTF();

        return new ManagerDTO(pk, name, id, pw, phone_num, status);
    }
    public byte[] getBytes() throws IOException
    {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeInt(pk);
        dos.writeUTF(id);
        dos.writeUTF(pw);
        dos.writeUTF(name);
        dos.writeUTF(phone_num);

        return buf.toByteArray();
    }
}
