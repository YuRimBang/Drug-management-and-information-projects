package persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.soap.Detail;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
@Setter

public class DetailInfoDTO extends MedicineDTO
{
    private String mainIngr = ""; //주성분
    private String classNoName = ""; //품목코드명
    private String eeDocData = ""; //효능효과
    private String udDocData = ""; //용법용량
    private String nbDocData = ""; //주의 사항
    private String etcOtcName = ""; //전문/일반
    private String validTerm = ""; //유통기한

//    public DetailInfoDTO(){
//        super();
//    }

    public DetailInfoDTO(String itemSeq, String itemName, String entpName, String mainIngr,
                         String classNoName, String eeDocData, String udDocData, String nbDocData, String etcOtcName, String validTerm)
    {
        super(itemSeq, itemName, entpName);
        this.mainIngr = mainIngr;
        this.classNoName = classNoName;
        this.eeDocData = eeDocData;
        this.udDocData = udDocData;
        this.nbDocData = nbDocData;
        this.etcOtcName = etcOtcName;
        this.validTerm = validTerm;
    }

    @Override
    public String toString()
    {
        return "일련번호 : " + getItemSeq()
                + "\n품목명 : " + getItemName()
                + "\n업체명 : " + getEntpName()
                + "\n주성분 : " + getMainIngr()
                + "\n품목코드명 : " + getClassNoName()
                + "\n효능효과 : " + getEeDocData()
                + "\n용법용량 : " + getUdDocData()
                + "\n주의사항 : " + getNbDocData()
                + "\n유통기한 : " +getValidTerm();
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeUTF(getItemSeq());
        dos.writeUTF(getItemName());
        dos.writeUTF(getEntpName());
        dos.writeUTF(getMainIngr());
        dos.writeUTF(getClassNoName());
        dos.writeUTF(getEeDocData());
        dos.writeUTF(getUdDocData());
        dos.writeUTF(getNbDocData());
        dos.writeUTF(getValidTerm());

        return buf.toByteArray();
    }
}
