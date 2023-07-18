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
public class ThisMedicineDTO extends MedicineDTO
{
    private String efcyQesitm = ""; //효능
    private String useMethodQesitm = ""; //사용법
    private String atpnWarnQesitm = ""; //주의사항 경고
    private String atpnQesitm = ""; //주의사항
    private String intrcQesitm = ""; //상호작용
    private String seQesitm = ""; //부작용
    private String depositMethodQesitm = ""; //보관법
    private String itemImage = ""; //이미지

    public ThisMedicineDTO(String itemSeq, String itemName, String entpName, String efcyQesitm, String useMethodQesitm, String atpnWarnQesitm,
                           String atpnQesitm, String intrcQesitm, String seQesitm, String depositMethodQesitm, String itemImage)
    {
        super(itemSeq, itemName, entpName);
        this.efcyQesitm =efcyQesitm;
        this.useMethodQesitm = useMethodQesitm;
        this.atpnWarnQesitm = atpnWarnQesitm;
        this.atpnQesitm = atpnQesitm;
        this.intrcQesitm = intrcQesitm;
        this.seQesitm = seQesitm;
        this.depositMethodQesitm = depositMethodQesitm;
        this.itemImage = itemImage;
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.write(super.getBytes());
        dos.writeUTF(efcyQesitm);
        dos.writeUTF(useMethodQesitm);
        dos.writeUTF(atpnWarnQesitm);
        dos.writeUTF(atpnQesitm);
        dos.writeUTF(intrcQesitm);
        dos.writeUTF(seQesitm);
        dos.writeUTF(depositMethodQesitm);
        dos.writeUTF(itemImage);

        return buf.toByteArray();
    }

    @Override
    public String toString()
    {
        return "일련번호 : " + getItemSeq()
                + "품목명 : " + getItemName()
                + "업체명" + getEntpName()
                + "이미지 : " + getItemImage()
                + "효능 : "+ getEfcyQesitm()
                + "사용법 : "+ getUseMethodQesitm()
                + "주의사항 경고 : "+ getAtpnWarnQesitm()
                + "주의 사항 : "+ getAtpnQesitm()
                + "상호작용 : "+ getIntrcQesitm()
                + "부작용 : " + getSeQesitm()
                + "보관법 : "+ getDepositMethodQesitm();
    }
}
