package medicineAPI;

import persistence.dto.MedicineShapeDTO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

//식품의약품안전처_의약품 낱알식별 정보
public class MedicineShape implements Api
{

    static StringBuilder urlBuilder;
    static String serviceKey;

    public MedicineShape()
    {

        serviceKey = "pyYbdf%2F8p21D3ZKCrGyOKQylkt82Ar0H8HtdNbaqSvFvO3D1Z4Mv5%2Bc2rlWxKcA21J2UcUusdYZ85m5h9fR2OQ%3D%3D";
    }


    public String pullMed(String page, String row, String num, String name) throws IOException {
        urlBuilder = new StringBuilder("http://apis.data.go.kr/1471000/MdcinGrnIdntfcInfoService01/getMdcinGrnIdntfcInfoList01"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + serviceKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("item_name","UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")); /*품목명*/
        urlBuilder.append("&" + URLEncoder.encode("entp_name","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*업체명*/
        urlBuilder.append("&" + URLEncoder.encode("item_seq","UTF-8") + "=" + URLEncoder.encode(num, "UTF-8")); /*품목일련번호*/
        urlBuilder.append("&" + URLEncoder.encode("img_regist_ts","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*약학정보원 이미지 생성일*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode(page, "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode(row, "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("edi_code","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*보험코드*/
        urlBuilder.append("&" + URLEncoder.encode("type","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*응답데이터 형식(xml/json) default : xml*/
        URL url = new URL(urlBuilder.toString());
        System.out.println(url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        String result = rd.readLine();

        //System.out.println(result);

        rd.close();
        conn.disconnect();

        return result;
    }


    @Override
    public List<MedicineShapeDTO> parsingJson(String str) throws ParseException
    {
        List<MedicineShapeDTO> list = new ArrayList<>();

        org.json.simple.parser.JSONParser jsonParser = new org.json.simple.parser.JSONParser();
        JSONObject jsonObject = (JSONObject)jsonParser.parse(str);

        JSONObject body = (JSONObject) jsonObject.get("body");
        if(body.get("totalCount").toString().equals("0"))
        {
            return list;
        }
        JSONArray items = (JSONArray) body.get("items");

        for(int i = 0; i < items.size(); i++)
        {
            JSONObject result = (JSONObject) items.get(i);

//            String className = Objects.toString(result.get("CLASS_NAME"), "x"); //분류
//            String itemName = Objects.toString(result.get("ITEM_NAME"), "x"); //품목명
//            String printFront = Objects.toString(result.get("PRINT_FRONT"), "x"); //표시(앞)
//            String etcOtcName = Objects.toString(result.get("ETC_OTC_NAME"), "x"); //전문/일반
//            String itemImage = Objects.toString(result.get("ITEM_IMAGE"), "x"); //이미지
//            String entpName = Objects.toString(result.get("ENTP_NAME"), "x"); //업체명
//            String colorClass1 = Objects.toString(result.get("COLOR_CLASS1"), "x"); //색깔 앞
//            String colorClass2 = Objects.toString(result.get("COLOR_CLASS2"), "x"); //색깔 뒤
//            String chart = Objects.toString(result.get("CHART"), "x"); //성상
//            String printBack = Objects.toString(result.get("PRINT_BACK"), "x"); //표시(뒤)
//            String drugShape = Objects.toString(result.get("DRUG_SHAPE"), "x"); //의약품 모양
//            String itemSeq = Objects.toString(result.get("ITEM_SEQ"), "x"); //일련번호

            String className = String.valueOf(result.get("CLASS_NAME")); //분류
            String itemName = String.valueOf(result.get("ITEM_NAME")); //품목명
            String printFront = String.valueOf(result.get("PRINT_FRONT")); //표시(앞)
            String etcOtcName = String.valueOf(result.get("ETC_OTC_NAME")); //전문/일반
            String itemImage = String.valueOf(result.get("ITEM_IMAGE")); //이미지
            String entpName = String.valueOf(result.get("ENTP_NAME")); //업체명
            String colorClass1 = String.valueOf(result.get("COLOR_CLASS1")); //색깔 앞
            String colorClass2 = String.valueOf(result.get("COLOR_CLASS2")); //색깔 뒤
            String chart = String.valueOf(result.get("CHART")); //성상
            String printBack = String.valueOf(result.get("PRINT_BACK")); //표시(뒤)
            String drugShape = String.valueOf(result.get("DRUG_SHAPE")); //의약품 모양
            String itemSeq = String.valueOf(result.get("ITEM_SEQ")); //일련번호

            MedicineShapeDTO medicineShapeData = new MedicineShapeDTO(itemSeq, itemName, entpName, chart, printFront, printBack, drugShape, colorClass1, colorClass2, etcOtcName, itemImage, className);

            list.add(i, medicineShapeData);
        }

        return list;
    }

}