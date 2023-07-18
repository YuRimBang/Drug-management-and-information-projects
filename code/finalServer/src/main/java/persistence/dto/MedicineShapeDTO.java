package persistence.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
@Setter
public class MedicineShapeDTO extends MedicineDTO
{
    private String chart = ""; //성상
    private String printFront = ""; //표시(앞)
    private String printBack = ""; //표시(뒤)
    private String drugShape = ""; //의약품 모양
    private String colorClass1 = ""; //색깔 앞
    private String colorClass2 = ""; //색깔 뒤
    private String etcOtcName = ""; //전문/일반
    private String itemImage = ""; //이미지
    private String className = ""; //분류

    public MedicineShapeDTO(String itemSeq, String itemName, String entpName, String chart,
                            String printFront, String printBack, String drugShape, String colorClass1, String colorClass2, String etcOtcName, String itemImage, String className)
    {
        super(itemSeq, itemName, entpName);
        this.chart = chart;
        this.printFront = printFront;
        this.printBack = printBack;
        this.drugShape = drugShape;
        this.colorClass1 = colorClass1;
        this.colorClass2 = colorClass2;
        this.etcOtcName = etcOtcName;
        this.itemImage = itemImage;
        this.className = className;
    }

    public void print()
    {
        System.out.println("일련번호 : " + getItemSeq());
        System.out.println("품목명 : " + getItemName());
        System.out.println("업체명" + getEntpName());
        System.out.println("성상 : " + chart);
        System.out.println("표시(앞) : " + printFront);
        System.out.println("표시(뒤) : " + printBack);
        System.out.println("의약품 모양 : " + drugShape);
        System.out.println("색깔(앞) : " + colorClass1);
        System.out.println("색깔 (뒤) : " + colorClass2);
        System.out.println("전문/일반" + etcOtcName);
        System.out.println("이미지" + itemImage);
        System.out.println("분류" + className);
        System.out.println();
    }
    public byte[] getBytes() throws IOException
    {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeUTF(getItemSeq());
        dos.writeUTF(getItemName());
        dos.writeUTF(getEntpName());
        dos.writeUTF(chart);
        dos.writeUTF(printFront);
        dos.writeUTF(printBack);
        dos.writeUTF(drugShape);
        dos.writeUTF(colorClass1);
        dos.writeUTF(colorClass2);
        dos.writeUTF(etcOtcName);
        dos.writeUTF(itemImage);
        dos.writeUTF(className);

       return buf.toByteArray();
    }

}
