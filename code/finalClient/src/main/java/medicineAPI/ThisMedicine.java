/* Java 1.8 샘플 코드 */
package medicineAPI;

import persistence.dto.ThisMedicineDTO;
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

//e약은요
public class ThisMedicine implements Api
{

    StringBuilder urlBuilder;
    String serviceKey;

    public ThisMedicine()
    {

        serviceKey = "pyYbdf%2F8p21D3ZKCrGyOKQylkt82Ar0H8HtdNbaqSvFvO3D1Z4Mv5%2Bc2rlWxKcA21J2UcUusdYZ85m5h9fR2OQ%3D%3D"; /*Service Key */
    }

    public String pullMed(String page, String row, String num, String name) throws IOException {
        urlBuilder = new StringBuilder("http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(page, "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(row, "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("entpName", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*업체명*/
        urlBuilder.append("&" + URLEncoder.encode("itemName", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")); /*제품명*/
        urlBuilder.append("&" + URLEncoder.encode("itemSeq", "UTF-8") + "=" + URLEncoder.encode(num, "UTF-8")); /*품목기준코드*/
        urlBuilder.append("&" + URLEncoder.encode("efcyQesitm", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*이 약의 효능은 무엇입니까?*/
        urlBuilder.append("&" + URLEncoder.encode("useMethodQesitm", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*이 약은 어떻게 사용합니까?*/
        urlBuilder.append("&" + URLEncoder.encode("atpnWarnQesitm", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*이 약을 사용하기 전에 반드시 알아야 할 내용은 무엇입니까?*/
        urlBuilder.append("&" + URLEncoder.encode("atpnQesitm", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*이 약의 사용상 주의사항은 무엇입니까?*/
        urlBuilder.append("&" + URLEncoder.encode("intrcQesitm", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*이 약을 사용하는 동안 주의해야 할 약 또는 음식은 무엇입니까?*/
        urlBuilder.append("&" + URLEncoder.encode("seQesitm", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*이 약은 어떤 이상반응이 나타날 수 있습니까?*/
        urlBuilder.append("&" + URLEncoder.encode("depositMethodQesitm", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*이 약은 어떻게 보관해야 합니까?*/
        urlBuilder.append("&" + URLEncoder.encode("openDe", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*공개일자*/
        urlBuilder.append("&" + URLEncoder.encode("updateDe", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*수정일자*/
        urlBuilder.append("&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*응답데이터 형식(xml/json) Default:xml*/
        URL url = new URL(urlBuilder.toString());
        System.out.println(url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
//        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
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
    public List<ThisMedicineDTO> parsingJson(String str) throws ParseException
    {
        ThisMedicineDTO thisMedicineData = null;
        List<ThisMedicineDTO> list = new ArrayList<>();

        str = str.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "x");

        if(!str.equals("x"))
        {
            org.json.simple.parser.JSONParser jsonParser = new org.json.simple.parser.JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(str);

            JSONObject body = (JSONObject) jsonObject.get("body");

            if(body.get("totalCount").toString().equals("0"))
            {
                return list;
            }
            String cnt = body.get("totalCount").toString();

            if(!cnt.equals("0"))
            {
                JSONArray items = (JSONArray) body.get("items");

                if(items == null){return list;}

                for(int i = 0; i < items.size(); i++)
                {
                    JSONObject result = (JSONObject) items.get(i);

//                    String itemName = Objects.toString(result.get("itemName"), "x"); //품목명
//                    String itemImage = Objects.toString(result.get("itemImage"), "x"); //이미지
//                    String entpName = Objects.toString(result.get("entpName"), "x"); //업체명
//                    String itemSeq = Objects.toString(result.get("itemSeq"), "x"); //일련번호
//                    String efcyQesitm = tagParsing(Objects.toString(result.get("efcyQesitm"), "x")); //효능
//                    String useMethodQesitm = tagParsing(Objects.toString(result.get("useMethodQesitm"), "x")); //사용법
//                    String atpnWarnQesitm = tagParsing(Objects.toString(result.get("atpnWarnQesitm"), "x")); //주의사항 경고
//                    String atpnQesitm = tagParsing(Objects.toString(result.get("atpnQesitm"), "x")); //주의사항
//                    String intrcQesitm = tagParsing(Objects.toString(result.get("intrcQesitm"), "x"));//상호작용
//                    String seQesitm = tagParsing(Objects.toString(result.get("seQesitm"), "x")); //부작용
//                    String depositMethodQesitm = tagParsing(Objects.toString(result.get("depositMethodQesitm"), "x")); //보관법

                    String itemName = String.valueOf(result.get("itemName")); //품목명
                    String itemImage = String.valueOf(result.get("itemImage")); //이미지
                    String entpName = String.valueOf(result.get("entpName")); //업체명
                    String itemSeq = String.valueOf(result.get("itemSeq")); //일련번호
                    String efcyQesitm = tagParsing(String.valueOf(result.get("efcyQesitm"))); //효능
                    String useMethodQesitm = tagParsing(String.valueOf(result.get("useMethodQesitm"))); //사용법
                    String atpnWarnQesitm = tagParsing(String.valueOf(result.get("atpnWarnQesitm"))); //주의사항 경고
                    String atpnQesitm = tagParsing(String.valueOf(result.get("atpnQesitm"))); //주의사항
                    String intrcQesitm = tagParsing(String.valueOf(result.get("intrcQesitm")));//상호작용
                    String seQesitm = tagParsing(String.valueOf(result.get("seQesitm"))); //부작용
                    String depositMethodQesitm = tagParsing(String.valueOf(result.get("depositMethodQesitm"))); //보관법


                    thisMedicineData = new ThisMedicineDTO(itemSeq, itemName, entpName, efcyQesitm, useMethodQesitm, atpnWarnQesitm, atpnQesitm, intrcQesitm , seQesitm, depositMethodQesitm, itemImage);

                    list.add(i, thisMedicineData);
                }
            }
        }

        return list;
    }

    private String tagParsing(String data) {
        String paragraphText = data.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*/?>|<br />|<p>|</p>", "");

        return paragraphText;
    }

}