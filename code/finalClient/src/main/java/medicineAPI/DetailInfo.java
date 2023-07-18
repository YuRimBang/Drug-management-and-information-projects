package medicineAPI;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import persistence.dto.DetailInfoDTO;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

//식품의약품안전처_의약품 제품 허가 정보
public class DetailInfo implements Api
{

    static StringBuilder urlBuilder;
    static String serviceKey;

    public DetailInfo() {
        //urlBuilder = new StringBuilder("https://apis.data.go.kr/1471000/QdrgPrdtPrmsnInfoService02/getQdrgPrdtPrmsnInfoInq02"); /*URL이잔*/
        //serviceKey = "Z7Os%2FdVAmX2KujicQaCT02Eqx8pmvphgIUvuPXNWyfzUCE%2F98FT%2Fvb8fYCPmtUPayBdeTmZYEmKkLtfpcATtig%3D%3D"; //이전 의약외품
        //urlBuilder = new StringBuilder("https://apis.data.go.kr/1471000/DrugPrdtPrmsnInfoService04/getDrugPrdtPrmsnDtlInq03"); /*URL*/
        serviceKey = "Z7Os%2FdVAmX2KujicQaCT02Eqx8pmvphgIUvuPXNWyfzUCE%2F98FT%2Fvb8fYCPmtUPayBdeTmZYEmKkLtfpcATtig%3D%3D"; //
    }


    /**
     * api를 끌어와서 String으로 변환, 파라미터에 필요한 값을 넣어 정보를 불러올 수 있음.
     * @param page : 페이지번호
     * @param row : 결과 수
     * @param num : 품목일련번호
     * @param name : 품목명
     * @return DetailInfo api를 끌어와서 String으로 변환한 값
     * @throws IOException
     */
    public String pullMed(String page, String row, String num, String name) throws IOException
    {
        urlBuilder = new StringBuilder("https://apis.data.go.kr/1471000/DrugPrdtPrmsnInfoService04/getDrugPrdtPrmsnDtlInq03"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(page, "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(row, "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*응답데이터 형식(xml/json) default : xml*/
        urlBuilder.append("&" + URLEncoder.encode("item_seq", "UTF-8") + "=" + URLEncoder.encode(num, "UTF-8")); /*품목일련번호*/
        urlBuilder.append("&" + URLEncoder.encode("item_name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")); /*품목명*/
        urlBuilder.append("&" + URLEncoder.encode("entp_name", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*업체명*/
        urlBuilder.append("&" + URLEncoder.encode("entp_no", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*업체허가번호*/
        urlBuilder.append("&" + URLEncoder.encode("entp_seq", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*업일련번호*/

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


    /**
     * String으로 된 json 내용을 parsing함.
     * @param str: api의 정보를 string으로 변환한 값
     * @return
     * @throws ParseException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    @Override
    public List<DetailInfoDTO> parsingJson(String str) throws ParseException, ParserConfigurationException, IOException, SAXException {
        List<DetailInfoDTO> list = new ArrayList<>();

        org.json.simple.parser.JSONParser jsonParser = new org.json.simple.parser.JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(str);

        JSONObject body = (JSONObject) jsonObject.get("body");
        if (body.get("totalCount").toString().equals("0")) {
            return list;
        }

        JSONArray items = (JSONArray) body.get("items");

        for (int i = 0; i < items.size(); i++) {
            JSONObject arr = (JSONObject) items.get(i);

            if (arr == null) {
                continue;
            }

            String itemName = String.valueOf(arr.get("ITEM_NAME").toString()); //품목명
            String entpName = String.valueOf(arr.get("ENTP_NAME").toString()); //업체명
            String itemSeq = String.valueOf(arr.get("ITEM_SEQ").toString()); //일련번호
            String mainIngr = "null";
            if(arr.get("MAIN_ITEM_INGR")!=null)
            {
                mainIngr = String.valueOf(arr.get("MAIN_ITEM_INGR").toString()); //주성분
            }
            String etcOtcName = String.valueOf(arr.get("ETC_OTC_CODE").toString()); //전문/일반
            String classNoName = String.valueOf(arr.get("CLASS_NO_NAME")); //품목코드명

            String eeDocData = "null";
            if(arr.get("EE_DOC_DATA") != null)
            {
                eeDocData = String.valueOf(tagParsing(arr.get("EE_DOC_DATA").toString())); //효능효과
            }

            String udDocData = "null";
            if(arr.get("UD_DOC_DATA")!=null)
            {
                udDocData = String.valueOf(tagParsing(arr.get("UD_DOC_DATA").toString())); //용법용량
            }

            String nbDocData = "null";
            if(arr.get("NB_DOC_DATA") != null)
            {
                nbDocData = String.valueOf(tagParsing(arr.get("NB_DOC_DATA").toString())); //주의사항
            }
            String validTerm = String.valueOf(arr.get("VALID_TERM")); //유통기한

            DetailInfoDTO data = new DetailInfoDTO(itemSeq, itemName, entpName, mainIngr, classNoName, eeDocData, udDocData, nbDocData, etcOtcName, validTerm);

            list.add(data);
        }

        return list;
    }


    /**
     * html 형식 파일에서 tag를 없애고 필요한 내용만 String으로 가져옴.
     * @param data: parsing한 json 파일에서 tag가 섞인 내용
     * @return tag를 없앤 순수 String값
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public String tagParsing(String data) throws ParserConfigurationException, IOException, SAXException {

        // XML 파서를 생성합니다.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        String paragraphText = "";


        try{
        // XML 데이터를 파싱하여 Document 객체로 변환합니다.
        InputStream inputStream = new ByteArrayInputStream(data.getBytes());
        Document document = builder.parse(inputStream);

        // 필요한 데이터를 추출합니다.
        Element docElement = document.getDocumentElement();
        NodeList paragraphList = docElement.getElementsByTagName("PARAGRAPH");
        for (int i = 0; i < paragraphList.getLength(); i++) {
            Element paragraphElement = (Element) paragraphList.item(i);
            paragraphText = paragraphElement.getTextContent();
            }
        } catch (Exception e) {
        e.printStackTrace();
        }

        return paragraphText;
    }

}
