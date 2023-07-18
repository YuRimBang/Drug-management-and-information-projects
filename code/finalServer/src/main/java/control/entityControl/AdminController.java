package control.entityControl;

import persistence.MyBatisConnectionFactory;
import persistence.dao.*;
import persistence.dto.*;
import protocol.BodyMaker;
import protocol.Header;

import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

public class AdminController
{
    public void updateUnfo(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        AdminDAO adminDAO = new AdminDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        AdminDTO adminDTO = new AdminDTO();

        int pk = inputStream.readInt();
        String name = inputStream.readUTF();
        String phoneNum = inputStream.readUTF();

        adminDTO.setPk(pk);
        adminDTO.setName(name);
        adminDTO.setPhone_num(phoneNum);

        int result = adminDAO.updateAdminInfo(adminDTO);
        outputStream.writeInt(result);
    }
    public void changePW(DataInputStream inputStream) throws IOException {
        AdminDAO adminDAO = new AdminDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        AdminDTO adminDTO = new AdminDTO();
        int curPk = inputStream.readInt();
        String newPw = inputStream.readUTF();
        adminDTO.setPk(curPk);
        adminDTO.setPw(newPw);
        adminDAO.updatePw(adminDTO);
    }
    public void checkPW(DataInputStream inputStream ,DataOutputStream outputStream) throws IOException {
        AdminDAO adminDAO = new AdminDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        String pw = inputStream.readUTF();
        int curPk = inputStream.readInt();
        boolean result = pw.equals(adminDAO.checkPw(curPk));
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
    public void viewInfo(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        int pk = inputStream.readInt();
        System.out.println(pk);
        AdminDAO adminDAO = new AdminDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        AdminDTO adminDTO = new AdminDTO();

        adminDTO.setPk(pk);
        List<AdminDTO> result = adminDAO.inquiryAdmin(adminDTO);
        String id = result.get(0).getId();
        String name = result.get(0).getName();
        String phoneNum = result.get(0).getPhone_num();

        Header resHeader;
        BodyMaker bodyMaker = new BodyMaker();
        byte [] resBody;
        bodyMaker.addStringBytes(id);
        bodyMaker.addStringBytes(name);
        bodyMaker.addStringBytes(phoneNum);
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
    public void registInfirmary(DataInputStream inputStream , DataOutputStream outputStream) throws IOException {
        int pk = inputStream.readInt();
        String school = inputStream.readUTF();
        String location = inputStream.readUTF();
        String infirmaryPhoneNum = inputStream.readUTF();
        int hour1 = inputStream.readInt();
        int minute1 = inputStream.readInt();
        int second1 = inputStream.readInt();
        LocalTime localTime1 = LocalTime.of(hour1, minute1, second1);
        int hour2 = inputStream.readInt();
        int minute2 = inputStream.readInt();
        int second2 = inputStream.readInt();
        LocalTime localTime2 = LocalTime.of(hour2, minute2, second2);
        int imgSize = inputStream.readInt();
        System.out.println(imgSize);
        byte[] imgData = new byte[imgSize];
        inputStream.readFully(imgData);

        InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        Header resHeader;
        if(infirmaryDAO.isPhoneNumDuplicateCheck(infirmaryPhoneNum))
        {
            resHeader = new Header(
                    Header.TYPE_RESULT,
                    Header.ACTOR_SERVER,
                    Header.CODE_RESULT_FAIL,
                    0
            );
        }
        else if(!infirmaryPhoneNum.matches("^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}$"))
        {
            resHeader = new Header(
                    Header.TYPE_RESULT,
                    Header.ACTOR_SERVER,
                    Header.CODE_RESULT_HOLD,
                    0
            );
        }
        else
        {
            InfirmaryDTO infirmaryDTO = new InfirmaryDTO();
            infirmaryDTO.setAdmin_pk(pk);

            infirmaryDTO.setSchool(school);
            infirmaryDTO.setLocation(location);
            infirmaryDTO.setInfirmary_phone_num(infirmaryPhoneNum);
            infirmaryDTO.setOpen_time(localTime1);
            infirmaryDTO.setClose_time(localTime2);
            infirmaryDTO.setFile(imgData);
            InfirmaryDTO check = infirmaryDAO.registerInfirmary(infirmaryDTO);
            infirmaryDTO.setAdmin_name(check.getAdmin_name());
            resHeader = new Header(
                    Header.TYPE_RESULT,
                    Header.ACTOR_SERVER,
                    Header.CODE_RESULT_SUCCESS,
                    0
            );
        }
        outputStream.write(resHeader.getBytes());
    }
    public void viewInfirmaryInfo(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryDTO infirmaryDTO = new InfirmaryDTO();
        int pk = inputStream.readInt();
        String schoolName = infirmaryDAO.getSchoolName(pk);
        infirmaryDTO.setSchool(schoolName);
        InfirmaryDTO resultInfirmaryDTO = infirmaryDAO.inquiryInfirmary(infirmaryDTO);
        BodyMaker bodyMaker = new BodyMaker();
        byte[] resBody;
        bodyMaker.addInfimary(resultInfirmaryDTO);
        resBody = bodyMaker.getBody();
        Header resHeader = new Header(
                Header.TYPE_RESPONSE,
                Header.ACTOR_SERVER,
                Header.CODE_SERVER_RES_INFIRMARY_INFO,
                resBody.length
        );
        outputStream.write(resHeader.getBytes());
        outputStream.write(resBody);
    }

    public void addMedicine(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        int pk = inputStream.readInt();
        int medicine_pk = inputStream.readInt();
        int stock = inputStream.readInt();
        System.out.println(pk);
        System.out.println(medicine_pk);
        System.out.println(stock);
        InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        int infirmary_pk = infirmaryDAO.getInfirmaryPk(pk);
        InfirmaryMedicineDAO infirmaryMedicineDAO = new InfirmaryMedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryMedicineDTO infirmaryMedicineDTO = new InfirmaryMedicineDTO();
        infirmaryMedicineDTO.setInfirmary_pk(infirmary_pk);
        infirmaryMedicineDTO.setMedicine_pk(medicine_pk);
        infirmaryMedicineDTO.setMedicine_stock(stock);

        int row = infirmaryMedicineDAO.insertInfirmaryMedicine(infirmaryMedicineDTO);
        System.out.println(row);
        Header resHeader = new Header(
                Header.TYPE_RESULT,
                Header.ACTOR_SERVER,
                Header.CODE_RESULT_SUCCESS,
                0
        );
        outputStream.write(resHeader.getBytes());
        outputStream.writeInt(row);
    }

    public void deleteMedicine(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryMedicineDAO infirmaryMedicineDAO = new InfirmaryMedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        int pk = inputStream.readInt();
        int infirmary_pk = infirmaryDAO.getInfirmaryPk(pk);
        int selectMedicinePk = inputStream.readInt();
        infirmaryMedicineDAO.deleteInfirmaryMedicine(selectMedicinePk);
        List<InfirmaryMedicineDTO> infirmaryMedicineDTOS = infirmaryMedicineDAO.selectAllByInfirmaryPk(infirmary_pk);
        outputStream.writeInt(infirmaryMedicineDTOS.size());
        for(int i = 0; i< infirmaryMedicineDTOS.size(); i++)
        {
            InfirmaryMedicineDTO medicine = infirmaryMedicineDTOS.get(i);
            String medicineName = medicineDAO.inquiryMedicineNameBySeqNum(String.valueOf(medicine.getMedicine_pk()));
            outputStream.write(infirmaryMedicineDTOS.get(i).getBytes());
            outputStream.writeUTF(medicineName);
        }

    }

    public void viewMedicineList(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {

        int pk = inputStream.readInt();
        InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        int infirmary_pk = infirmaryDAO.getInfirmaryPk(pk);

        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryMedicineDAO infirmaryMedicineDAO = new InfirmaryMedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<InfirmaryMedicineDTO> medicineListResult = infirmaryMedicineDAO.inquiryMedicineStock(infirmary_pk);
        outputStream.writeInt(medicineListResult.size());
        for(int i = 0; i<medicineListResult.size(); i++)
        {
            InfirmaryMedicineDTO medicine = medicineListResult.get(i);
            String medicineName = medicineDAO.inquiryMedicineNameBySeqNum(String.valueOf(medicine.getMedicine_pk()));
            medicine.setMedicine_name(medicineName);
            outputStream.write(medicineListResult.get(i).getBytes());
            outputStream.writeUTF(medicineName);
        }


    }

    public void updateAmount(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        InfirmaryMedicineDAO infirmaryMedicineDAO = new InfirmaryMedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        InfirmaryMedicineDTO infirmaryMedicineDTO = InfirmaryMedicineDTO.readInfirmaryMedicine(inputStream);
        int stock = inputStream.readInt();
        int option = inputStream.readInt();
        int row = 0;
        if(option == 1)
        {
            infirmaryMedicineDTO.setMedicine_stock(stock);
            row = infirmaryMedicineDAO.updateInfirmaryMedicine(infirmaryMedicineDTO);
            outputStream.writeInt(row);
        }
        else if(option == 2)//plus
        {
            row = infirmaryMedicineDAO.plusInfirmaryMedicineNum(infirmaryMedicineDTO.getPk());
        }
        else//minus
        {
            row = infirmaryMedicineDAO.minusInfirmaryMedicineNum(infirmaryMedicineDTO.getPk());
        }
        outputStream.writeInt(row);
    }
    public void setAlarm(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        InfirmaryAlarmDAO infirmaryAlarmDAO = new InfirmaryAlarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryAlarmDTO infirmaryAlarmDTO = InfirmaryAlarmDTO.readInfirmaryAlarm(inputStream);
        int pk = inputStream.readInt();

        infirmaryAlarmDTO.setInfirmary_pk(infirmaryDAO.getInfirmaryPk(pk));
        int result = infirmaryAlarmDAO.registerInfirmaryAlarm(infirmaryAlarmDTO);

        outputStream.writeInt(result);
    }

    public void showAlarm(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryAlarmDAO infirmaryAlarmDAO = new InfirmaryAlarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryAlarmDTO infirmaryAlarmDTO;

        long epochSecond = inputStream.readLong();
        LocalDateTime time = LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC);
        int pk = inputStream.readInt();
        infirmaryAlarmDTO = infirmaryAlarmDAO.inquiryAlarmToTime(time, infirmaryDAO.getInfirmaryPk(pk));

        InfirmaryAlarmDTO finalInfirmaryAlarmDTO = infirmaryAlarmDTO;
        outputStream.write(finalInfirmaryAlarmDTO.getBytes());
    }

    public void viewNotice(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryNoticeDAO infirmaryNoticeDAO = new InfirmaryNoticeDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        int pk = inputStream.readInt();
        int infirmary_pk = infirmaryDAO.getInfirmaryPk(pk);

        List<InfirmaryNoticeDTO> noticeListResult =  infirmaryNoticeDAO.inqueryNotice(infirmary_pk);
        BodyMaker bodyMaker = new BodyMaker();
        byte[] resBody;
        bodyMaker.addInfirmaryNoticeList(noticeListResult);
        resBody = bodyMaker.getBody();
        outputStream.write(resBody);
    }
    public void deleteNotice(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryNoticeDAO infirmaryNoticeDAO = new InfirmaryNoticeDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        int pk = inputStream.readInt();
        InfirmaryNoticeDTO infirmaryNoticeDTO = InfirmaryNoticeDTO.readInfirmaryNotice(inputStream);

        int infirmary_pk = infirmaryDAO.getInfirmaryPk(pk);
        infirmaryNoticeDAO.deleteNotice(infirmaryNoticeDTO.getPk());
        List<InfirmaryNoticeDTO> updatedNoticeListResult = infirmaryNoticeDAO.inqueryNotice(infirmary_pk);

        BodyMaker bodyMaker = new BodyMaker();
        byte[] resBody;
        bodyMaker.addInfirmaryNoticeList(updatedNoticeListResult);
        resBody = bodyMaker.getBody();
        outputStream.write(resBody);
    }
    public void updateNotice(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryNoticeDAO infirmaryNoticeDAO = new InfirmaryNoticeDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        int pk = inputStream.readInt();
        InfirmaryNoticeDTO infirmaryNoticeDTO = InfirmaryNoticeDTO.readInfirmaryNotice(inputStream);

        int infirmary_pk = infirmaryDAO.getInfirmaryPk(pk);
        infirmaryNoticeDAO.updateNotice(infirmaryNoticeDTO);

        List<InfirmaryNoticeDTO> updatedNoticeListResult = infirmaryNoticeDAO.inqueryNotice(infirmary_pk);

        BodyMaker bodyMaker = new BodyMaker();
        byte[] resBody;
        bodyMaker.addInfirmaryNoticeList(updatedNoticeListResult);
        resBody = bodyMaker.getBody();
        outputStream.write(resBody);
    }

    public void registNotice(DataInputStream inputStream) throws IOException {
        InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryNoticeDAO infirmaryNoticeDAO = new InfirmaryNoticeDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        int pk = inputStream.readInt();
        String title = inputStream.readUTF();
        String content = inputStream.readUTF();

        int infirmaryPk = infirmaryDAO.getInfirmaryPk(pk);
        InfirmaryNoticeDTO infirmaryNoticeDTO = new InfirmaryNoticeDTO();
        infirmaryNoticeDTO.setInfirmary_pk(infirmaryPk);
        infirmaryNoticeDTO.setTitle(title);
        infirmaryNoticeDTO.setContent(content);
        infirmaryNoticeDAO.insertNotice(infirmaryNoticeDTO);
    }

    public void updateInfirmaryInfo(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        InfirmaryDTO infirmaryDTO = new InfirmaryDTO();

        int pk = inputStream.readInt();
        infirmaryDTO.setAdmin_pk(pk);
        String infirmaryPhoneNum = inputStream.readUTF();

        String hour1Value = inputStream.readUTF();
        String minute1Value = inputStream.readUTF();
        LocalTime localTime1 = null;
        if (hour1Value != null && !hour1Value.isEmpty() && minute1Value != null && !minute1Value.isEmpty()) {
            localTime1 = LocalTime.parse(hour1Value + ":" + minute1Value);
            infirmaryDTO.setOpen_time(localTime1);
        }
        String hour2Value = inputStream.readUTF();
        String minute2Value = inputStream.readUTF();
        LocalTime localTime2 = null;
        if (hour2Value != null && !hour2Value.isEmpty() && minute2Value != null && !minute2Value.isEmpty()) {
            localTime2 = LocalTime.parse(hour2Value + ":" + minute2Value);
            infirmaryDTO.setClose_time(localTime2);
        }


        Header resHeader;
        if(infirmaryPhoneNum != null && !infirmaryPhoneNum.equals("")) {
            // 전화번호 형식 검사 (숫자와 '-'로 이루어진 11~13자)
            if (!infirmaryPhoneNum.matches("^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}$")) {
                resHeader = new Header(
                        Header.TYPE_RESULT,
                        Header.ACTOR_SERVER,
                        Header.CODE_RESULT_FAIL,
                        0
                );
                outputStream.write(resHeader.getBytes());
            }

            // 전화번호 중복 체크
            if (infirmaryDAO.isPhoneNumDuplicateCheck(infirmaryPhoneNum)) {
                resHeader = new Header(
                        Header.TYPE_RESULT,
                        Header.ACTOR_SERVER,
                        Header.CODE_RESULT_HOLD,
                        0
                );
                outputStream.write(resHeader.getBytes());
            }

            infirmaryDTO.setInfirmary_phone_num(infirmaryPhoneNum);
        }
        infirmaryDAO.updateInfirmaryInfo(infirmaryDTO);
        resHeader = new Header(
                Header.TYPE_RESULT,
                Header.ACTOR_SERVER,
                Header.CODE_RESULT_SUCCESS,
                0
        );
        outputStream.write(resHeader.getBytes());
    }
    public void adminRegist(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        AdminDAO adminDAO = new AdminDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        AdminDTO adminDTO = new AdminDTO();
        Header resHeader;
        String id = inputStream.readUTF();
        String pw = inputStream.readUTF();
        String name = inputStream.readUTF();
        String phone_num = inputStream.readUTF();
        int size = inputStream.readInt();
        byte[] imageData = new byte[size];
        System.out.println(size);
        inputStream.readFully(imageData);
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
        else if (adminDAO.isIdDuplicateCheck(id)) {

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
        else if (adminDAO.isPhoneNumDuplicateCheck(phone_num)) {
            error=("전화번호가 이미 등록되어 있습니다. 다른 전화번호를 입력해주세요.");
            resHeader = new Header(
                    Header.TYPE_RESULT,
                    Header.ACTOR_SERVER,
                    Header.CODE_RESULT_HOLD,
                    0
            );
        }
        else {
            adminDTO.setId(id);
            adminDTO.setPw(pw);
            adminDTO.setName(name);
            adminDTO.setPhone_num(phone_num);
            adminDTO.setFile(imageData);
            adminDAO.signUpAdmin(adminDTO);

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
}
