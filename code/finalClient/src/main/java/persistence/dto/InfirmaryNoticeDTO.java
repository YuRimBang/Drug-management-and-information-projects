package persistence.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class InfirmaryNoticeDTO {

    private int pk;
    private int infirmary_pk;
    private String title;
    private String content;

    public InfirmaryNoticeDTO(int pk, int infirmary_pk, String title, String content)
    {
        this.pk = pk;
        this.infirmary_pk = infirmary_pk;
        this.title = title;
        this.content = content;
    }

    public InfirmaryNoticeDTO(int infirmary_pk, String title, String content)
    {
        this.infirmary_pk = infirmary_pk;
        this.title = title;
        this.content = content;
    }

    public static InfirmaryNoticeDTO readInfirmaryNotice(DataInputStream inputStream) throws IOException {
        int pk = inputStream.readInt();
        int infirmary_pk = inputStream.readInt();
        String title = inputStream.readUTF();
        String content = inputStream.readUTF();

        return new InfirmaryNoticeDTO(pk,infirmary_pk,title,content);
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeInt(pk);
        dos.writeInt(infirmary_pk);
        dos.writeUTF(title);
        dos.writeUTF(content);

        return buf.toByteArray();
    }
    public String toString() {
        return
                "제목: " + title +
                        "\n내용: " + content;
    }
}