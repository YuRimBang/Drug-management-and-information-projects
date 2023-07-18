package control.entityControl;

import persistence.MyBatisConnectionFactory;
import persistence.dao.*;
import persistence.dto.*;
import protocol.BodyMaker;
import protocol.Header;
import storage.PkStorage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class UserController {
    private PkStorage storage = PkStorage.getInstance();;

    public void login(DataInputStream inputStream, DataOutputStream outputStream) throws IOException
    {
        String id = inputStream.readUTF();
        String pw = inputStream.readUTF();
        System.out.println(id + " , " + pw);
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        AdminDAO adminDAO = new AdminDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        int pk = 0;
        String name = null;
        String status = null;
        UserDTO userDTO = userDAO.login(id, pw);
        AdminDTO adminDTO = adminDAO.login(id, pw);
        ManagerDTO managerDTO = managerDAO.login(id, pw);
        BodyMaker bodyMaker = new BodyMaker();
        byte [] resBody = new byte[0];
        Header resHeader;
        try {
            if (userDTO != null) {
                pk = userDTO.getPk();
                name = userDTO.getName();
                status = userDTO.getStatus();
            } else if (adminDTO != null) {
                pk = adminDTO.getPk();
                name = adminDTO.getName();
                status = adminDTO.getStatus();
            } else if (managerDTO != null) {
                pk = managerDTO.getPk();
                name = managerDTO.getName();
                status = managerDTO.getStatus();
            } else {
                throw new Exception("No matching user found");
            }
            bodyMaker.addIntBytes(pk);
            bodyMaker.addStringBytes(name);
            bodyMaker.addStringBytes(status);
            resBody = bodyMaker.getBody();
            System.out.println(status);
            resHeader = new Header(
                    Header.TYPE_RESULT,
                    Header.ACTOR_SERVER,
                    Header.CODE_RESULT_SUCCESS,
                    resBody.length
            );

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            resHeader = new Header(
                    Header.TYPE_RESULT,
                    Header.ACTOR_SERVER,
                    Header.CODE_RESULT_FAIL,
                    0
            );
            e.printStackTrace();
        }
        outputStream.write(resHeader.getBytes());
        outputStream.write(resBody);
        System.out.println("보내기 완료");
        System.out.println(id + " , " + pw);
    }

    public void findPw(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        AdminDAO adminDAO = new AdminDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        String userPw = null;
        String adminPw = null;
        String managerPw = null;

        String name = inputStream.readUTF();
        String phoneNum = inputStream.readUTF();
        String id = inputStream.readUTF();

        userPw = userDAO.findPw(name, phoneNum, id);
        adminPw = adminDAO.findPw(name, phoneNum, id);
        managerPw = managerDAO.inquiryPw(name, phoneNum, id);

        BodyMaker bodyMaker = new BodyMaker();
        byte [] resBody;

        Header resHeader;
        try {
                if(userPw != null) {
                    bodyMaker.addStringBytes(userPw);
                } else if (adminPw != null){
                    bodyMaker.addStringBytes(adminPw);
                }
                else if (managerPw != null) {
                    bodyMaker.addStringBytes(managerPw);
                } else {
                    bodyMaker.addStringBytes("존재하지 않는 비밀번호");
                }
            resBody = bodyMaker.getBody();
            resHeader = new Header(
                    Header.TYPE_RESPONSE,
                    Header.ACTOR_SERVER,
                    Header.CODE_SERVER_RES_LOST_PW,
                    resBody.length
            );
            outputStream.write(resHeader.getBytes());
            outputStream.write(resBody);
        } catch (Exception e) {
        }
    }
    public void findId(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        AdminDAO adminDAO = new AdminDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        String userId = null;
        String adminId = null;
        String managerId = null;

        String name = inputStream.readUTF();
        String phoneNum = inputStream.readUTF();

        userId = userDAO.findId(name, phoneNum);
        adminId = adminDAO.findId(name, phoneNum);
        managerId = managerDAO.inquiryId(name, phoneNum);

        BodyMaker bodyMaker = new BodyMaker();
        byte [] resBody;

        Header resHeader;
        try {
            if(userId != null) {
                bodyMaker.addStringBytes(userId);
            } else if (adminId != null){
                bodyMaker.addStringBytes(adminId);
            }
            else if (managerId != null) {
                bodyMaker.addStringBytes(managerId);
            } else {
                bodyMaker.addStringBytes("존재하지 않는 아이디");
            }
            resBody = bodyMaker.getBody();
            resHeader = new Header(
                    Header.TYPE_RESPONSE,
                    Header.ACTOR_SERVER,
                    Header.CODE_SERVER_RES_LOST_ID,
                    resBody.length
            );
            outputStream.write(resHeader.getBytes());
            outputStream.write(resBody);
        } catch (Exception e) {
        }
    }
    public void searchMedicineList(DataInputStream inputStream, DataOutputStream outputStream) throws IOException
    {
        String medicineName = inputStream.readUTF();

        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<RefineData> resultList = medicineDAO.inquiryMedicineByName(medicineName);
        BodyMaker bodyMaker = new BodyMaker();
        byte [] resBody;
        bodyMaker.addRefineData(resultList);
        Header resHeader;
        resBody = bodyMaker.getBody();
        resHeader = new Header(
                Header.TYPE_RESPONSE,
                Header.ACTOR_SERVER,
                Header.CODE_SERVER_RES_ALL_MADICINE,
                resBody.length
        );
        outputStream.write(resHeader.getBytes());
        outputStream.write(resBody);
    }
    //---------------------------------------------------------------------
    public void showImage(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        String name = inputStream.readUTF();

        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        String ImgUrl = null;
        ImgUrl = medicineDAO.inquiryMedicineImageByName(name);

        outputStream.writeUTF(ImgUrl);
    }
    //-------------------------------------------------------------------------
    public void viewUserInfo(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        UserDTO userDTO = new UserDTO();

        int pk = inputStream.readInt();
        userDTO.setPk(pk);

        List<UserDTO> userInfoList = userDAO.inquiryUser(userDTO);
        BodyMaker bodyMaker = new BodyMaker();
        byte[] resBody;
        bodyMaker.addUserList(userInfoList);
        resBody = bodyMaker.getBody();
        outputStream.write(resBody);
    }

    public void showSchoolList(DataOutputStream outputStream) throws IOException {
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        System.out.println("왔슈");
        List<InfirmaryDTO> list = userDAO.viewSchoolList();
        List<String> arr = new ArrayList<>();
        for(InfirmaryDTO item : list) {
            String schoolName = item.getSchool();
            arr.add(schoolName);
        }
        BodyMaker bodyMaker = new BodyMaker();
        byte[] resBody;
        outputStream.writeInt(arr.size());
        for(int i = 0; i<arr.size(); i++ )
            bodyMaker.addStringBytes(arr.get(i));
        resBody = bodyMaker.getBody();

        outputStream.write(resBody);
    }

    public void userRegist(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        UserDTO userDTO = new UserDTO();
        Header resHeader;
        String id = inputStream.readUTF();
        String pw = inputStream.readUTF();
        String name = inputStream.readUTF();
        String phone_num = inputStream.readUTF();
        String selectedSchool = inputStream.readUTF();
        String error = "";
        // 아이디 형식 검사 (영문 대소문자와 숫자로 이루어진 4~10자)
        if (!id.matches("^[a-zA-Z0-9]{4,10}$")) {
            error=("아이디 형식이 올바르지 않습니다. 다시 입력해주세요.");
            resHeader = new Header(
                    Header.TYPE_RESULT,
                    Header.ACTOR_SERVER,
                    Header.CODE_RESULT_HOLD,
                    0
            );
        }
        // 아이디 중복 체크
        else if (userDAO.isIdDuplicateCheck(id)) {
            error=("중복된 아이디입니다. 다른 아이디를 입력해주세요.");
            resHeader = new Header(
                    Header.TYPE_RESULT,
                    Header.ACTOR_SERVER,
                    Header.CODE_RESULT_HOLD,
                    0
            );
        }
        // 비밀번호 형식 검사 (영문 대소문자와 숫자로 이루어진 8~20자)
        else if (!pw.matches("^[a-zA-Z0-9]{8,20}$")) {
            error=("비밀번호 형식이 올바르지 않습니다. 다시 입력해주세요.");
            resHeader = new Header(
                    Header.TYPE_RESULT,
                    Header.ACTOR_SERVER,
                    Header.CODE_RESULT_HOLD,
                    0
            );
        }
        // 이름 형식 검사 (2-10자 한글)
        else if (!name.matches("^[가-힣]{2,10}$")) {
            error=("이름 형식이 올바르지 않습니다. 다시 입력해주세요.");
            resHeader = new Header(
                    Header.TYPE_RESULT,
                    Header.ACTOR_SERVER,
                    Header.CODE_RESULT_HOLD,
                    0
            );
        }
        // 전화번호 형식 검사 (숫자와 '-'로 이루어진 11~13자)
        else if (!phone_num.matches("^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}$")) {
            error=("전화번호 형식이 올바르지 않습니다. 다시 입력해주세요.");
            resHeader = new Header(
                    Header.TYPE_RESULT,
                    Header.ACTOR_SERVER,
                    Header.CODE_RESULT_HOLD,
                    0
            );
        }
        // 전화번호 중복 체크
        else if (userDAO.isPhoneNumDuplicateCheck(phone_num)) {
            error=("전화번호가 이미 등록되어 있습니다. 다른 전화번호를 입력해주세요.");
            resHeader = new Header(
                    Header.TYPE_RESULT,
                    Header.ACTOR_SERVER,
                    Header.CODE_RESULT_HOLD,
                    0
            );
        }
        else {
            userDTO.setId(id);
            userDTO.setPw(pw);
            userDTO.setName(name);
            userDTO.setPhone_num(phone_num);
            userDTO.setSchool(selectedSchool);
            userDTO.setInfirmary_pk(userDAO.getInfirmaryPk(selectedSchool).getPk());
            userDAO.signUpUser(userDTO);

            resHeader = new Header(
                    Header.TYPE_RESULT,
                    Header.ACTOR_SERVER,
                    Header.CODE_RESULT_SUCCESS,
                    0
            );
        }
        outputStream.write(resHeader.getBytes());
        outputStream.writeUTF(error);
    }

    // 개인정보 수정
    public void userUpdateInfo(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        UserDTO userDTO = new UserDTO();

        int pk = inputStream.readInt();
        String name = inputStream.readUTF();
        String phoneNum = inputStream.readUTF();
        String selectedSchool = inputStream.readUTF();

        userDTO.setPk(pk);
        if(!name.equals("")){
            userDTO.setName(name);
        }
        if(!phoneNum.equals("")){
            userDTO.setPhone_num(phoneNum);
        }
        if(!selectedSchool.equals("")){
            userDTO.setSchool(selectedSchool);
        }
        userDTO.setInfirmary_pk(userDAO.getInfirmaryPk(selectedSchool).getPk());

        int result = userDAO.updateUserInfo(userDTO);
        userDAO.updateInfirmaryPK(userDTO);

        BodyMaker bodyMaker = new BodyMaker();
        byte [] resBody;
        bodyMaker.addIntBytes(result);
        Header resHeader;
        resBody = bodyMaker.getBody();
        resHeader = new Header(
                Header.TYPE_RESPONSE,
                Header.ACTOR_SERVER,
                Header.CODE_RESULT_SUCCESS,
                resBody.length
        );
        outputStream.write(resHeader.getBytes());
        outputStream.write(resBody);

    }

    public void changePW(DataInputStream inputStream) throws IOException {
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        UserDTO userDTO = new UserDTO();
        int curPk = inputStream.readInt();
        String newPw = inputStream.readUTF();
        userDTO.setPk(curPk);
        userDTO.setPw(newPw);
        userDAO.updatePw(userDTO);
    }

    public void checkPW(DataInputStream inputStream ,DataOutputStream outputStream) throws IOException {
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        String pw = inputStream.readUTF();
        int curPk = inputStream.readInt();
        boolean result = pw.equals(userDAO.checkPw(curPk));
        Header resHeader;
        BodyMaker bodyMaker = new BodyMaker();
        byte [] resBody;
        bodyMaker.addBoolean(result);
        resBody = bodyMaker.getBody();
        resHeader = new Header(
                Header.TYPE_RESULT,
                Header.ACTOR_SERVER,
                Header.CODE_RESULT_SUCCESS,
                resBody.length
        );
        outputStream.write(resHeader.getBytes());
        outputStream.write(resBody);
    }

    // 대학교 보건실 공지사항 조회
    public void viewNotice(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        InfirmaryNoticeDAO infirmaryNoticeDAO = new InfirmaryNoticeDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        int pk = inputStream.readInt();
        int infirmary_pk = userDAO.getInfirmary_pk(pk);

        List<InfirmaryNoticeDTO> noticeListResult =  infirmaryNoticeDAO.inqueryNotice(infirmary_pk);
        BodyMaker bodyMaker = new BodyMaker();
        byte[] resBody;
        bodyMaker.addInfirmaryNoticeList(noticeListResult);
        resBody = bodyMaker.getBody();
        outputStream.write(resBody);
    }

    // 대학교 보건실 약 검색
    public void viewInfirmaryMedicineList(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryMedicineDAO infirmaryMedicineDAO = new InfirmaryMedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        int pk = inputStream.readInt();
        String medicineName = inputStream.readUTF();

        UserDTO userDTO = new UserDTO();
        userDTO.setPk(pk);
        int infirmary_pk = userDAO.getInfirmary_pk(pk);
        List<InfirmaryMedicineDTO> infirmaryMedicineDTOS = infirmaryMedicineDAO.selectAllByInfirmaryPk(infirmary_pk);

        BodyMaker bodyMaker = new BodyMaker();
        byte [] resBody;
        outputStream.writeInt(infirmaryMedicineDTOS.size());
        List<RefineData> refineDataList = new ArrayList<>();
        System.out.println(refineDataList.size());
        for (InfirmaryMedicineDTO infirmaryMedicineDTO : infirmaryMedicineDTOS) {
            String medicine_Name = medicineDAO.inquiryMedicineNameBySeqNum(String.valueOf(infirmaryMedicineDTO.getMedicine_pk()));
            infirmaryMedicineDTO.setMedicine_name(medicine_Name);
            if ((infirmaryMedicineDTO.getMedicine_name()).contains(medicineName)) {
                String seqNum = String.valueOf(infirmaryMedicineDTO.getMedicine_pk());
                RefineData refineData = medicineDAO.inquiryMedicineBySeqNum(seqNum);
                refineDataList.add(refineData);
            }
        }
        bodyMaker.addRefineData(refineDataList);
        resBody = bodyMaker.getBody();
        outputStream.write(resBody);
    }

    // 노약자 주의 약 검색
    public void searchElderlyMedicineList(DataInputStream inputStream, DataOutputStream outputStream) throws IOException
    {
        String medicineName = inputStream.readUTF();

        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<RefineData> resultList = medicineDAO.inquiryCautionMedicine(medicineName);

        BodyMaker bodyMaker = new BodyMaker();
        byte [] resBody;
        bodyMaker.addRefineData(resultList);
        Header resHeader;
        resBody = bodyMaker.getBody();
        outputStream.write(resBody);
    }
    public void secession(DataInputStream inputStream) throws IOException {
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        int pk = inputStream.readInt();
        managerDAO.deleteUser(pk);
    }

    public void viewCautionElderly(DataOutputStream outputStream) throws IOException {
        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<RefineData> resultList = medicineDAO.inquiryElderlyCautionMedicine();
        BodyMaker bodyMaker = new BodyMaker();
        byte [] resBody;
        bodyMaker.addRefineData(resultList);
        resBody = bodyMaker.getBody();
        outputStream.write(resBody);
    }

    public void viewCautionPregnant(DataOutputStream outputStream) throws IOException {
        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<RefineData> resultList = medicineDAO.inquiryPregnantWomanCautionMedicine();
        BodyMaker bodyMaker = new BodyMaker();
        byte [] resBody;
        bodyMaker.addRefineData(resultList);
        resBody = bodyMaker.getBody();
        outputStream.write(resBody);
    }

    public void viewCautionChild(DataOutputStream outputStream) throws IOException {
        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<RefineData> resultList = medicineDAO.inquiryChildCautionMedicine();
        BodyMaker bodyMaker = new BodyMaker();
        byte [] resBody;
        bodyMaker.addRefineData(resultList);
        resBody = bodyMaker.getBody();
        outputStream.write(resBody);
    }

    public void viewInfirmaryInfo(DataInputStream inputStream, DataOutputStream outputStream) throws IOException
    {
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        int pk = inputStream.readInt();
        System.out.println(pk);
        String school = userDAO.getSchool(pk);
        System.out.println(school);
        InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryDTO infirmaryDTO = new InfirmaryDTO();
        InfirmaryDTO resultInfirmaryDTO = new InfirmaryDTO();
        infirmaryDTO.setSchool(school);
        resultInfirmaryDTO = infirmaryDAO.inquiryInfirmary(infirmaryDTO);
        System.out.println(resultInfirmaryDTO.toString());
        outputStream.write(resultInfirmaryDTO.getInfirmaryByte());
    }

    public void searcMedicineNum(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        int pk = inputStream.readInt();
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        int infirmary_pk = userDAO.getInfirmary_pk(pk);

        InfirmaryMedicineDAO infirmaryMedicineDAO = new InfirmaryMedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<InfirmaryMedicineDTO> medicineListResult = infirmaryMedicineDAO.inquiryMedicineStock(infirmary_pk);

        BodyMaker bodyMaker = new BodyMaker();
        byte [] resBody;
        bodyMaker.addInfirmaryMedicineNum(medicineListResult);
        resBody = bodyMaker.getBody();
        outputStream.write(resBody);
        System.out.println("보낼게~");
    }

    public void viewInfirmarySelectMedicine(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        InfirmaryMedicineDTO infirmaryMedicineDTO = InfirmaryMedicineDTO.readInfirmaryMedicine(inputStream);

        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        String name = medicineDAO.inquiryMedicineNameBySeqNum(String.valueOf(infirmaryMedicineDTO.getMedicine_pk()));
        List<RefineData> resultList = medicineDAO.inquiryMedicineByName(name);
        BodyMaker bodyMaker = new BodyMaker();
        byte [] resBody;
        bodyMaker.addRefineData(resultList);
        resBody = bodyMaker.getBody();
        outputStream.write(resBody);
    }

    public void viewBookmarkMedicine(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        int pk = inputStream.readInt();
        MedicineBookmarkDAO medicineBookmarkDAO = new MedicineBookmarkDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<Integer> resultList = medicineBookmarkDAO.selectMedicinePk(pk);
        outputStream.writeInt(resultList.size());
        for(int i =0; i<resultList.size(); i++)
        {
            MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
            String medicineName = medicineDAO.inquiryMedicineNameBySeqNum(String.valueOf(resultList.get(i)));
            outputStream.writeUTF(medicineName);
        }
    }

    public void viewBookmarkSelectMedicine(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        String selectedMedicine = inputStream.readUTF();

        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<RefineData> resultList = medicineDAO.inquiryMedicineByName(selectedMedicine);
        BodyMaker bodyMaker = new BodyMaker();
        byte [] resBody;
        bodyMaker.addRefineData(resultList);
        resBody = bodyMaker.getBody();
        outputStream.write(resBody);
    }

    public void saveBoomark(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        String name = inputStream.readUTF();
        int pk = inputStream.readInt();
        MedicineBookmarkDAO medicineBookmarkDAO = new MedicineBookmarkDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        MedicineBookmarkDTO medicineBookmarkDTO = new MedicineBookmarkDTO();

        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        int infirmary_pk = userDAO.getInfirmary_pk(pk);

        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        String seqNum = medicineDAO.inquiryMedicineSeqNumByName(name);

        medicineBookmarkDTO.setUser_pk(pk);
        medicineBookmarkDTO.setInfirmary_pk(infirmary_pk);
        medicineBookmarkDTO.setMedicine_pk(Integer.parseInt(seqNum));

        int row = medicineBookmarkDAO.registerBookmark(medicineBookmarkDTO);

        outputStream.writeInt(row);

    }

    public void getBookmarkPk(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        MedicineBookmarkDAO medicineBookmarkDAO = new MedicineBookmarkDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        String medicineField = inputStream.readUTF();
        int pk = inputStream.readInt();

        String medSeq = medicineDAO.inquiryMedicineSeqNumByName(medicineField);
        int bookmarkPk = medicineBookmarkDAO.selectMedBookmarkPk(medSeq, pk);
        outputStream.writeUTF(medSeq);
        outputStream.writeInt(bookmarkPk);
    }
    public void registMediAlarm(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        MedicineAlarmDAO medicineAlarmDAO = new MedicineAlarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        MedicineAlarmDTO medicineAlarmDTO = MedicineAlarmDTO.readMedicineAlarm(inputStream);
        int result = medicineAlarmDAO.insertMedicineAlarm(medicineAlarmDTO);
        outputStream.writeInt(result);
    }

    public void checkEndDay(DataInputStream inputStream) throws IOException {
        MedicineAlarmDAO medicineAlarmDAO = new MedicineAlarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        int pk = inputStream.readInt();
        int book_pk = inputStream.readInt();
        int hour =  inputStream.readInt();
        int minute = inputStream.readInt();
        int second = inputStream.readInt();

        LocalTime alarmTime = LocalTime.of(hour,minute,second);
        //테이블 검색
        int alarmPk = medicineAlarmDAO.selectUserAlarm(pk, book_pk, alarmTime);
        System.out.println("alaramPk: " +alarmPk);
        //테이블 삭제
        medicineAlarmDAO.deleteUserAlarm(alarmPk);
    }

    public void setChoiceBox(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        MedicineBookmarkDAO medicineBookmarkDAO = new MedicineBookmarkDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        int pk = inputStream.readInt();
        List<Integer> resultList = medicineBookmarkDAO.selectMedicinePk(pk);

        int size = resultList.size();
        outputStream.writeInt(size);
        for(int i = 0; i<size; i++)
        {
            MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
            String medicineName = medicineDAO.inquiryMedicineNameBySeqNum(String.valueOf(resultList.get(i)));
            outputStream.writeUTF(medicineName);
        }
    }

    public void viewBewreConbine(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        String text1 = inputStream.readUTF();
        String text2 = inputStream.readUTF();

        String result = medicineDAO.combination(text1,text2);
        outputStream.writeUTF(result);
    }

    public void searchMedicineShape(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        String front = inputStream.readUTF();
        String back = inputStream.readUTF();
        String colorFront = inputStream.readUTF();
        String colorBack = inputStream.readUTF();
        String shape = inputStream.readUTF();
        String chart = inputStream.readUTF();

        RefineData refineData = new RefineData();
        refineData.setPrintFront(front);
        refineData.setPrintBack(back);
        refineData.setColorClassFront(colorFront);
        refineData.setColorClassBack(colorBack);
        refineData.setDrugShape(shape);
        refineData.setChart(chart);

        List<RefineData> resultList = medicineDAO.inquiryMedicineByShape(refineData);
        BodyMaker bodyMaker = new BodyMaker();
        byte [] resBody;
        bodyMaker.addRefineData(resultList);
        resBody = bodyMaker.getBody();
        outputStream.write(resBody);

    }
}
