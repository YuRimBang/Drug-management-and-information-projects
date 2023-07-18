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
@NoArgsConstructor
@ToString
public class RefineData
{
    private String seqNum = ""; //일련번호
    private String itemName = ""; //품목명
    private String entpName = ""; //업체명
    private String mainIngr = ""; //주성분
    private String className = ""; //분류 classNoName
    private String etcOtcName = ""; //전문/일반
    private String efficacy = ""; //효능효과, 효능
    private String useMethod = ""; //용법용량, 사용법
    private String caution = ""; //주의 사항(1), 주의사항 경고(2)
    private String cautionPeople = ""; //주의사항(주의해야하는 사람)
    private String intrc = ""; //상호작용
    private String sideEffect = ""; //부작용
    private String storage = ""; //보관법
    private String image = ""; //이미지
    private String chart = ""; //성상
    private String printFront = ""; //표시(앞)
    private String printBack = ""; //표시(뒤)
    private String drugShape = ""; //의약품 모양
    private String colorClassFront = ""; //색깔 앞
    private String colorClassBack = ""; //색깔 뒤
    private String validTerm = ""; //유통기한


    public RefineData(String seqNum, String itemName, String entpName, String mainIngr, String className, String etcOtcName, String efficacy, String useMethod, String caution, String cautionPeople, String intrc, String sideEffect, String storage, String image, String chart, String printFront, String printBack, String drugShape, String colorClassFront, String colorClassBack, String validTerm) {
        this.seqNum = seqNum;
        this.itemName = itemName;
        this.entpName = entpName;
        this.mainIngr = mainIngr;
        this.className = className;
        this.etcOtcName = etcOtcName;
        this.efficacy = efficacy;
        this.useMethod = useMethod;
        this.caution = caution;
        this.cautionPeople = cautionPeople;
        this.intrc = intrc;
        this.sideEffect = sideEffect;
        this.storage = storage;
        this.image = image;
        this.chart = chart;
        this.printFront = printFront;
        this.printBack = printBack;
        this.drugShape = drugShape;
        this.colorClassFront = colorClassFront;
        this.colorClassBack = colorClassBack;
        this.validTerm = validTerm;
    }

    public static RefineData readRefineData(DataInputStream inputStream) throws IOException {
        String seqNum = inputStream.readUTF();
        String itemName = inputStream.readUTF();
        String entpName = inputStream.readUTF();
        String mainIngr = inputStream.readUTF();
        String className = inputStream.readUTF();
        String etcOtcName = inputStream.readUTF();
        String efficacy = inputStream.readUTF();
        String useMethod = inputStream.readUTF();
        String caution = inputStream.readUTF();
        String cautionPeople = inputStream.readUTF();
        String intrc = inputStream.readUTF();
        String sideEffect = inputStream.readUTF();
        String storage = inputStream.readUTF();
        String image = inputStream.readUTF();
        String chart = inputStream.readUTF();
        String printFront = inputStream.readUTF();
        String printBack = inputStream.readUTF();
        String drugShape = inputStream.readUTF();
        String colorClassFront = inputStream.readUTF();
        String colorClassBack = inputStream.readUTF();
        String validTerm = inputStream.readUTF();

        return new RefineData(seqNum, itemName, entpName, mainIngr, className, etcOtcName,
                efficacy, useMethod, caution, cautionPeople,intrc, sideEffect, storage, image, chart, printFront
                , printBack, drugShape, colorClassFront, colorClassBack, validTerm );

    }

    public byte[] getBytes() throws IOException
    {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeUTF(seqNum);
        dos.writeUTF(itemName);
        dos.writeUTF(entpName);
        dos.writeUTF(mainIngr);
        dos.writeUTF(className);
        dos.writeUTF(etcOtcName);
        dos.writeUTF(efficacy);
        dos.writeUTF(useMethod);
        dos.writeUTF(caution);
        dos.writeUTF(cautionPeople);

        dos.writeUTF(intrc);
        dos.writeUTF(sideEffect);
        dos.writeUTF(storage);
        dos.writeUTF(image);
        dos.writeUTF(chart);
        dos.writeUTF(printFront);
        dos.writeUTF(printBack);
        dos.writeUTF(drugShape);
        dos.writeUTF(colorClassFront);
        dos.writeUTF(colorClassBack);
        dos.writeUTF(validTerm);

        return buf.toByteArray();
    }
    public RefineData(String seqNum , String itmeName , String entpName)
    {
        this.seqNum = seqNum;
        this.itemName = itmeName;
        this.entpName = entpName;
    }

    public boolean checkDuplication(String newSeq)
    {
        if(seqNum == newSeq){return true;}
        else{return  false;}
    }
    
    public void changeNull()
    {
        if(seqNum == "" || seqNum == null)
        {
            seqNum = "x";
        }
//        return "일련번호 : " + seqNum
//                + "\n품목명 : " + itemName
//                + "\n업체명 : " + entpName
//                + "\n주성분 : " + mainIngr
//                + "\n분류 : " + className
//                + "\n전문 일반 : " + etcOtcName
//                + "\n효능 : " + efficacy
//                + "\n사용법 : " + use
//                + "\n주의 사항 : " + caution
//                + "\n주의 대상 : " + cautionPeople
//                + "\n상호작용 : " + intrc
//                + "\n부작용 : " + sideEffect
//                + "\n보관법 : " + storage
//                + "\n이미지 : " + image
//                + "\n성상 : " + chart
//                + "\n표시(앞) : " + printFront
//                + "\n표시(뒤) : " + printBack
//                + "\n의약품 모양 : " + drugShape
//                + "\n색깔(앞) : " + colorClassFront
//                + "\n색깔 (뒤) : " + colorClassBack
//                + "\n유통 기한 : " + validTerm;
    }

//    public void print()
//    {
//        System.out.println("일련번호 : " + seqNum);
//        System.out.println("품목명 : " + itmeName);
//        System.out.println("업체명" + entpName);
//        System.out.println("주성분 : " + mainIngr);
//        System.out.println("분류 : " + className);
//        System.out.println("전문 일반 : " + etcOtcName);
//        System.out.println("효능 : " + efficacy);
//        System.out.println("사용법 : " + use);
//        System.out.println("주의 사항 : " + caution);
//        System.out.println("주의 대상 : " + cautionPeople);
//        System.out.println("상호작용 : " + intrc);
//        System.out.println("부작용 : " + sideEffect);
//        System.out.println("보관법 : " + storage);
//        System.out.println("이미지 : " + image);
//        System.out.println("성상 : " + chart);
//        System.out.println("표시(앞) : " + printFront);
//        System.out.println("표시(뒤) : " + printBack);
//        System.out.println("의약품 모양 : " + drugShape);
//        System.out.println("색깔(앞) : " + colorClassFront);
//        System.out.println("색깔 (뒤) : " + colorClassBack);
//        System.out.println();
//    }

    @Override
    public String toString()
    {
        return "일련번호 : " + seqNum
                + "\n품목명 : " + itemName
                + "\n업체명 : " + entpName
                + "\n주성분 : " + mainIngr
                + "\n분류 : " + className
                + "\n전문 일반 : " + etcOtcName
                + "\n효능 : " + efficacy
                + "\n사용법 : " + useMethod
                + "\n주의 사항 : " + caution
                + "\n주의 대상 : " + cautionPeople
                + "\n상호작용 : " + intrc
                + "\n부작용 : " + sideEffect
                + "\n보관법 : " + storage
                + "\n이미지 : " + image
                + "\n성상 : " + chart
                + "\n표시(앞) : " + printFront
                + "\n표시(뒤) : " + printBack
                + "\n의약품 모양 : " + drugShape
                + "\n색깔(앞) : " + colorClassFront
                + "\n색깔 (뒤) : " + colorClassBack
                + "\n유통 기한 : " + validTerm;
    }
}
