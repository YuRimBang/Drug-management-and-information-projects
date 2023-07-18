package persistence.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
@Setter
@ToString
public class MedicineDTO {
    private String itemSeq = ""; //일련번호
    private String itemName = ""; //품목명
    private String entpName = ""; //업체명

    public MedicineDTO(String itemSeq, String itemName, String entpName)
    {
        this.itemSeq = itemSeq;
        this.itemName = itemName;
        this.entpName = entpName;
    }

    @Override
    public String toString(){
        return "일련번호 : " + getItemSeq()
                + "\n품목명 : " + getItemName()
                + "\n업체명 : " + getEntpName();
    }
    public byte[] getBytes() throws IOException
    {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeUTF(itemSeq);
        dos.writeUTF(itemName);
        dos.writeUTF(entpName);

        return buf.toByteArray();
    }
}
