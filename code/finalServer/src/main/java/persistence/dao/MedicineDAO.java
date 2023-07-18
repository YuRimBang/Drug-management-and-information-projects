package persistence.dao;

import medicineAPI.MedicineShape;
import org.apache.ibatis.session.SqlSession;
import org.xml.sax.SAXException;
import persistence.dto.*;
import medicineAPI.DetailInfo;
import medicineAPI.ThisMedicine;
import org.apache.ibatis.session.SqlSessionFactory;
import org.json.simple.parser.ParseException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicineDAO {
    private final SqlSessionFactory sqlSessionFactory;

    public MedicineDAO(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public String combination(String name, String nameMed)
    {
        RefineData refineData1 = new RefineData();
        RefineData refineData2 = new RefineData();
        boolean check = false;

        SqlSession session = sqlSessionFactory.openSession(); // SqlSession 객체 열기

        try {
            refineData1 = session.selectOne("mapper.MedicineMapper.selectMedicineName", name);
            String[] mainIngr = refineData1.getMainIngr().replaceAll("\\[M\\d+\\]", "").split("\\|");

            for(int i = 0; i < mainIngr.length; i++)
            {
                Map<String, String> map = new HashMap<>();
                map.put("name", nameMed);
                map.put("ingr", mainIngr[i]);
                refineData2 = session.selectOne("mapper.MedicineMapper.selectMedicineName", map);

                if(refineData2 != null)
                {
                    check = true;
                    break;
                }
            }

            session.commit();
            System.out.println("성공");
        }catch (Exception e) {

            session.rollback();
            System.out.println(e.getMessage());
        }
        finally {
            session.close();
            System.out.println("finally");
        }
        String content = null;
        if(check)
        {
            content = "병용 주의가 필요합니다.";
        }else
        {
            content = "주의 사항이 없습니다.";
        }
        return content;
    }

    public void updateDetailInfo() throws IOException, ParseException, ParserConfigurationException, SAXException {
        DetailInfo detailInfo = new DetailInfo();
        List<DetailInfoDTO> allDataList = new ArrayList<>(); // 중심데이터에서 검색

        SqlSession session = sqlSessionFactory.openSession(); // SqlSession 객체 열기

        for(int i = 1; i<=505; i++)
        {
            List<DetailInfoDTO> list = detailInfo.parsingJson(detailInfo.pullMed(Integer.toString(i), "100", "", "")); // this에서 검색
            //System.out.println(list);
            allDataList.addAll(list);
        }

        try {
            for (int j = 0; j < allDataList.size(); j++) {
                session.insert("mapper.DetailInfoMapper.insertDetailInfo", allDataList.get(j));
            }

            session.commit();
            System.out.println("성공");
        } finally {
            session.close();
            System.out.println("finally");
        }
    }


    public void updateThisMedicine() throws IOException, ParseException {
        ThisMedicine thisMedicine = new ThisMedicine();
        List<ThisMedicineDTO> allThisMedList = new ArrayList<>(); // 이약은요에서 검색

        for (int i = 1; i <= 45; i++) {
            List<ThisMedicineDTO> list = thisMedicine.parsingJson(thisMedicine.pullMed(Integer.toString(i), "100", "", "")); //this에서 검색

            allThisMedList.addAll(list);
        }

        SqlSession session = sqlSessionFactory.openSession(); // SqlSession 객체 열기

        try {

            for(int i = 0; i < allThisMedList.size(); i++)
            {
                session.insert("mapper.ThismedicineMapper.insertThismedicine", allThisMedList.get(i));
                session.commit();
            }

            System.out.println("성공");
        }
        catch (Exception e) {

            session.rollback();
            System.out.println(e.getMessage());
        }
        finally {
            session.close();
            System.out.println(" finally");
        }
    }

    public void updateMedicineShape() throws IOException, ParseException {
        MedicineShape medicineShape = new MedicineShape();
        List<MedicineShapeDTO> allMedicineShapeList = new ArrayList<>(); //약 모양에서 검색

        for(int i = 1; i <= 252; i++)
        {
            List<MedicineShapeDTO> list = medicineShape.parsingJson(medicineShape.pullMed(Integer.toString(i), "100", "", ""));

            for(int j = 0; j < list.size(); j++)
            {
                allMedicineShapeList.add(list.get(j));
            }
        }

        SqlSession session = sqlSessionFactory.openSession(); // SqlSession 객체 열기

        try {

            for(int i = 0; i < allMedicineShapeList.size(); i++)
            {
                session.insert("mapper.MedicineShapeMapper.insertMedicineShape", allMedicineShapeList.get(i));
                session.commit();
            }

            System.out.println("성공");
        }
        catch (Exception e) {

            session.rollback();
            System.out.println(e.getMessage());
        }
        finally {
            session.close();
            System.out.println(" finally");
        }
    }

    /*main에 있는 데이터를 this와 합쳐 테이블에 넣는다.*/
    public void updateRefineData()
    {
        RefineData refineData = null; // DetailInfo와 ThisMedicine에서 필요한 정보를 합친 정제데이터

        SqlSession session = sqlSessionFactory.openSession(); // SqlSession 객체 열기

        try {

            int num = 1;
            while(true)
            {
                DetailInfoDTO detailInfo = session.selectOne("mapper.DetailInfoMapper.selectDetailInfoByNum", num); //main데이터 하나씩 확인

                if(detailInfo == null)
                {
                    break;
                }else
                {
                    refineData = new RefineData(detailInfo.getItemSeq(), detailInfo.getItemName(), detailInfo.getEntpName());
                    refineData.setMainIngr(detailInfo.getMainIngr()); //주성분
                    refineData.setClassName(detailInfo.getClassNoName()); //분류
                    refineData.setEfficacy(detailInfo.getEeDocData()); //효과
                    refineData.setUseMethod(detailInfo.getUdDocData()); //사용법
                    refineData.setCaution(detailInfo.getNbDocData()); //주의사항
                    refineData.setValidTerm(detailInfo.getValidTerm()); //유통기한
                    refineData.setEtcOtcName(detailInfo.getEtcOtcName()); //전문 /일반

                    ThisMedicineDTO thisMedicineDTO = session.selectOne("mapper.ThismedicineMapper.selectThismedicineBySeq",refineData.getSeqNum());

                    if(thisMedicineDTO != null) //main + this
                    {
                        refineData.setEfficacy(refineData.getEfficacy() + " " + thisMedicineDTO.getEfcyQesitm()); //효과
                        refineData.setUseMethod(refineData.getUseMethod() + " " + thisMedicineDTO.getUseMethodQesitm()); //사용법
                        refineData.setCaution(refineData.getCaution() + " " + thisMedicineDTO.getAtpnWarnQesitm()); //주의 경고
                        refineData.setCautionPeople(thisMedicineDTO.getAtpnQesitm()); //주의사항 사람
                        refineData.setIntrc(thisMedicineDTO.getIntrcQesitm()); //상호작용
                        refineData.setSideEffect(thisMedicineDTO.getSeQesitm()); // 부작용
                        refineData.setStorage(thisMedicineDTO.getDepositMethodQesitm());//보관법
                        refineData.setImage(thisMedicineDTO.getItemImage()); //이미지
                    }
                }

                session.insert("mapper.MedicineMapper.insertMedicine", refineData);
                session.commit();

                num++;
            }

            System.out.println("성공");
        }
        catch (Exception e) {

            session.rollback();
            System.out.println(e.getMessage());
        }
        finally {
            session.close();
            System.out.println(" finally");
        }
    }

    public void updateThisMedicineRefineData() {
        RefineData refineData; // DetailInfo와 ThisMedicine에서 필요한 정보를 합친 정제데이터

        SqlSession session = sqlSessionFactory.openSession(); // SqlSession 객체 열기

        try {
            for(int num = 1; num <= 4416; num++)
            {
                ThisMedicineDTO thisMedicineDTO = session.selectOne("mapper.ThismedicineMapper.selectThismedicineByNum", num); //main데이터 하나씩 확인
                refineData = session.selectOne("mapper.MedicineMapper.selectMedicineSeq", thisMedicineDTO.getItemSeq());

                if (thisMedicineDTO == null) {
                    break;
                } else if (refineData == null) {
                    refineData = new RefineData(thisMedicineDTO.getItemSeq(), thisMedicineDTO.getItemName(), thisMedicineDTO.getEntpName());
                    refineData.setEfficacy(thisMedicineDTO.getEfcyQesitm()); //효과
                    refineData.setUseMethod(thisMedicineDTO.getUseMethodQesitm()); //사용법
                    refineData.setCaution(thisMedicineDTO.getAtpnWarnQesitm()); //주의 경고
                    refineData.setCautionPeople(thisMedicineDTO.getAtpnQesitm()); //주의사항 사람
                    refineData.setIntrc(thisMedicineDTO.getIntrcQesitm()); //상호작용
                    refineData.setSideEffect(thisMedicineDTO.getSeQesitm()); // 부작용
                    refineData.setStorage(thisMedicineDTO.getDepositMethodQesitm());//보관법
                    refineData.setImage(thisMedicineDTO.getItemImage()); //이미지

                    session.insert("mapper.MedicineMapper.insertMedicine", refineData);
                    session.commit();
                }
            }

            System.out.println("성공");
        } catch (Exception e) {
            session.rollback();
            System.out.println(e.getMessage());
        } finally {
            session.close();
            System.out.println("finally");
        }
    }

    /*정제 되어있는 refinedata에 모양 정보를 추가 한다.*/
    public void updateShapeRefineData()
    {
        RefineData refineData = null; // DetailInfo와 ThisMedicine에서 필요한 정보를 합친 정제데이터

        SqlSession session = sqlSessionFactory.openSession(); // SqlSession 객체 열기

        try {

            for(int num = 1; num <= 25115; num++)
            {
                MedicineShapeDTO medicineShapeDTO = session.selectOne("mapper.MedicineShapeMapper.selectMedicineShapeBySeq", num); //main데이터 하나씩 확인

                refineData = new RefineData(medicineShapeDTO.getItemSeq(), medicineShapeDTO.getItemName(), medicineShapeDTO.getEntpName());
                refineData.setChart(medicineShapeDTO.getChart());
                refineData.setPrintFront(medicineShapeDTO.getPrintFront());
                refineData.setPrintBack(medicineShapeDTO.getPrintBack());
                refineData.setDrugShape(medicineShapeDTO.getDrugShape());
                refineData.setColorClassFront(medicineShapeDTO.getColorClass1());
                refineData.setColorClassBack(medicineShapeDTO.getColorClass2());
                refineData.setImage(medicineShapeDTO.getItemImage());
                session.update("mapper.MedicineMapper.updateMedicineShape", refineData);
                session.commit();
            }
            System.out.println("성공");
        }
        catch (Exception e) {

            session.rollback();
            System.out.println(e.getMessage());
        }
        finally {
            session.close();
            System.out.println(" finally");
        }
    }


    // 일련번호로 약 검색
    public RefineData inquiryMedicineBySeqNum(String seqNum){
        SqlSession session = sqlSessionFactory.openSession();
        Map<String, Object> map = new HashMap<>();
        map.put("seqNum", seqNum);

        RefineData refineData = new RefineData();
        try {
            refineData = session.selectOne("mapper.MedicineMapper.selectMedicineBySeqNum", map);
        }
        finally {
            session.close();
        }
        return refineData;
    }

    // 제품명으로 약 검색
    public List<RefineData> inquiryMedicineByName(String name)
    {
        SqlSession session = sqlSessionFactory.openSession();
        Map<String, Object> map = new HashMap<>();
        map.put("itemName", name);

        List<RefineData> refineDataList = new ArrayList<>();
        try {
            refineDataList = session.selectList("mapper.MedicineMapper.selectMedicineByName", map);
        }
        finally {
            session.close();
        }

        return refineDataList;
    }

    // 일련번호로 약 검색
/*    public List<RefineData> inquiryMedicineBySeqNum(String seqNum)
    {
        SqlSession session = sqlSessionFactory.openSession();
        Map<String, Object> map = new HashMap<>();
        map.put("seqNum", seqNum);

        List<RefineData> refineDataList = new ArrayList<>();
        try {
            refineDataList = session.selectList("mapper.MedicineMapper.selectMedicineBySeqNum", map);
        }
        finally {
            session.close();
        }

        return refineDataList;
    }*/

    public String inquiryMedicineSeqNumByName(String name){
        SqlSession session = sqlSessionFactory.openSession();
        String seqNum = null;

        try {
            seqNum = session.selectOne("mapper.MedicineMapper.selectSeqNumByName", name);
        }
        finally {
            session.close();
        }

        return seqNum;
    }

    public String inquiryMedicineNameBySeqNum(String seqNum){
        SqlSession session = sqlSessionFactory.openSession();
        String name = null;

        try {
            name = session.selectOne("mapper.MedicineMapper.selectNameBySeqNum", seqNum);
        }
        finally {
            session.close();
        }

        return name;
    }

    public String inquiryMedicineImageByName(String medicineName){
        SqlSession session = sqlSessionFactory.openSession();

        Object result = null;
        String name = null;

        Map<String, Object> map = new HashMap<>();
        map.put("itemName", medicineName);

        try {
            result = session.selectOne("mapper.MedicineMapper.selectMedicineImageByName", map);
            name = (result != null) ? result.toString() : null;
        }
        finally {
            session.close();
        }

        return name;
    }

    // 약 모양으로 약 조회
    public List<RefineData> inquiryMedicineByShape(RefineData refineData) {
        SqlSession session = sqlSessionFactory.openSession();

        List<RefineData> refineDataList = new ArrayList<>();
        try {
            refineDataList = session.selectList("mapper.MedicineMapper.selectMedicineByShape", refineData);
        }
        finally {
            session.close();
        }

        return refineDataList;
    }

    // 노인 주의 약 조회
    public List<RefineData> inquiryElderlyCautionMedicine() {
        SqlSession session = sqlSessionFactory.openSession();

        List<RefineData> refineDataList = new ArrayList<>();
        try {
            refineDataList = session.selectList("mapper.MedicineMapper.selectElderlyMedicine");
        }
        finally {
            session.close();
        }

        return refineDataList;
    }

    // 노약자주의 약 검색(주의 약품인지 여부)
    public List<RefineData> inquiryCautionMedicine(String medicineName) {
        SqlSession session = sqlSessionFactory.openSession();
        Map<String, Object> map = new HashMap<>();

        map.put("itemName", medicineName);

        List<RefineData> refineDataList = new ArrayList<>();
        try {
            refineDataList = session.selectList("mapper.MedicineMapper.selectCautionMedicine", map);
        }
        finally {
            session.close();
        }
        return refineDataList;
    }


    // 노인 주의 약 검색(주의 약품인지 여부)
    public List<RefineData> inquiryElderlyCautionMedicine(String medicineName) {
        SqlSession session = sqlSessionFactory.openSession();
        Map<String, Object> map = new HashMap<>();

        map.put("itemName", medicineName);

        List<RefineData> refineDataList = new ArrayList<>();
        try {
            refineDataList = session.selectList("mapper.MedicineMapper.selectElderlyMedicineByName", map);
        }
        finally {
            session.close();
        }
        return refineDataList;
    }


    // 임산부 주의 약 조회
    public List<RefineData> inquiryPregnantWomanCautionMedicine() {
        SqlSession session = sqlSessionFactory.openSession();

        List<RefineData> refineDataList = new ArrayList<>();
        try {
            refineDataList = session.selectList("mapper.MedicineMapper.selectPregnantWomanMedicine");
        }
        finally {
            session.close();
        }

        return refineDataList;
    }

    // 임산부 주의 약 검색(주의 약품인지 여부)
    public List<RefineData> inquiryPregnantWomanCautionMedicine(String medicineName) {
        SqlSession session = sqlSessionFactory.openSession();
        Map<String, Object> map = new HashMap<>();
        map.put("itemName", medicineName);

        List<RefineData> refineDataList = new ArrayList<>();
        try {
            refineDataList = session.selectList("mapper.MedicineMapper.selectPregnantWomanMedicineByName", map);
        }
        finally {
            session.close();
        }

        return refineDataList;
    }

    // 어린이 주의 약 조회
    public List<RefineData> inquiryChildCautionMedicine() {
        SqlSession session = sqlSessionFactory.openSession();

        List<RefineData> refineDataList = new ArrayList<>();
        try {
            refineDataList = session.selectList("mapper.MedicineMapper.selectChildMedicine");
        }
        finally {
            session.close();
        }

        return refineDataList;
    }

    // 어린이 주의 약 조회(주의 약품인지 여부)
    public List<RefineData> inquiryChildCautionMedicine(String medicineName) {
        SqlSession session = sqlSessionFactory.openSession();
        Map<String, Object> map = new HashMap<>();
        map.put("itemName", medicineName);

        List<RefineData> refineDataList = new ArrayList<>();
        try {
            refineDataList = session.selectList("mapper.MedicineMapper.selectChildMedicineByName", map);
        }
        finally {
            session.close();
        }

        return refineDataList;
    }

    public void inquiryMedicineByNum(String num) throws IOException, ParseException, ParserConfigurationException, SAXException { // 일련번호로 약 정보 가져오기

        DetailInfo detailInfo = new DetailInfo();
        List<DetailInfoDTO> dataList = detailInfo.parsingJson(detailInfo.pullMed("", "", num, "")); // 일련번호로 약 정보 가져옴

        String name = dataList.get(0).getItemName(); // 받아온 약 정보의 이름 찾아냄.
        //inquiryMedicineByName(name); // 찾은 이름으로 약 정보 출력 함수 호출

    }

    // 약 테이블에 넣기
//    public void insertMedicine(RefineData refineData) {
//        System.out.println("test1");
//        SqlSession session = sqlSessionFactory.openSession(); // SqlSession 객체 열기
//        List<RefineData> refineDataList;
//        int row = 0;
//        try {
//            System.out.println("test2");
//            System.out.println("---------refineData: \n" + refineData.toString());
//
//            Map<String, Object> map = new HashMap<>();
//
//            map.put("seqNum", refineData.getSeqNum());
//            System.out.println("seqNum:" + map.get("seqNum"));
////            map.put("itemName", refineData.getItemName());
////            System.out.println("itemName:" + map.get("itemName"));
////            map.put("entpName", refineData.getEntpName());
////            System.out.println("entpName:" + map.get("entpName"));
////            map.put("mainIngr", refineData.getMainIngr());
////            System.out.println("mainIngr:" + map.get("mainIngr"));
////            map.put("className", refineData.getClassName());
////            System.out.println("className:" + map.get("className"));
////            map.put("etcOtcName", refineData.getEtcOtcName());
////            System.out.println("etcOtcName:" + map.get("etcOtcName"));
////
////            map.put("efficacy", refineData.getEfficacy());
////            System.out.println("efficacy:" + map.get("efficacy"));
////
////            map.put("use", refineData.getUse());
////            System.out.println("use:" + map.get("use"));
////
////            map.put("caution", refineData.getCaution());
////            System.out.println("caution:" + map.get("caution"));
////--------------------
////            map.put("cautionPeople", refineData.getCautionPeople());
////            System.out.println("출력:" + map.get("cautionPeople"));
////
////            map.put("intrc", refineData.getIntrc());
////            System.out.println("출력:" + map.get("intrc"));
////
////            map.put("sideEffect", refineData.getSideEffect());
////            System.out.println("출력:" + map.get("sideEffect"));
////
////            map.put("storage", refineData.getStorage());
////            System.out.println("출력:" + map.get("storage"));
////
////            map.put("image", refineData.getImage());
////            System.out.println("출력:" + map.get("image"));
////-------------------
////            map.put("validTerm", refineData.getValidTerm());
////            System.out.println("validTerm:" + map.get("validTerm"));
//
//
//            System.out.println("여기");
//
//            row = session.insert("mapper.MedicineMapper.insertMedicine", map);
////            refineDataList = session.selectList("mapper.MedicineMapper.selectMedicine");
////            System.out.println("size: " +  refineDataList.size());
//            System.out.println("2여기");
//            System.out.println("row: " +row);
//            session.commit();
//        }
//        catch (Exception e) {
//            System.out.println("rollback");
//            session.rollback();
//        }
//        finally {
//            System.out.println("finally");
//            session.close();
//        }
//    }
    public void insertMedicine(RefineData refineData) {

        System.out.println("여기----------------------");
        System.out.println(":: " + refineData.getPrintFront());
        SqlSession session = sqlSessionFactory.openSession(); // SqlSession 객체 열기
        int row = 0;
        try {
            row = session.insert("mapper.MedicineMapper.insertMedicineAll", refineData);
            session.commit();
            System.out.println(row+" 성공");
        }
        catch (Exception e) {
            session.rollback();
            System.out.println(row+" roll");
        }
        finally {
            session.close();
            System.out.println(row+" finally");
        }
    }


    public void insertRefineMedicine() {

        SqlSession session = sqlSessionFactory.openSession(); // SqlSession 객체 열기
        List<RefineData> refineDataList;
        int row = 0;
        try {

            row = session.insert("mapper.MedicineMapper.insertRefineData");
            System.out.println("commit");
            session.commit();
        }
        catch (Exception e) {
            System.out.println("rollback");
            session.rollback();
        }
        finally {
            System.out.println("finally");
            session.close();
        }
    }

}