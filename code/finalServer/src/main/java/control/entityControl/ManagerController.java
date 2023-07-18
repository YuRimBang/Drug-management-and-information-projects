package control.entityControl;

import persistence.MyBatisConnectionFactory;
import persistence.dao.*;
import persistence.dto.AdminDTO;
import persistence.dto.InfirmaryDTO;
import persistence.dto.UserDTO;
import protocol.BodyMaker;
import protocol.Header;
import protocol.MySerializableClass;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class ManagerController {

    public void searchAdmin(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        String id = inputStream.readUTF();
        String phoneNum = inputStream.readUTF();
        List<AdminDTO> adminListResult = managerDAO.inquiryAdmin(id, phoneNum);
        System.out.println("받은거 : " + id + ", " + phoneNum);
        BodyMaker bodyMaker = new BodyMaker();
        byte[] resBody;

        int size = adminListResult.size();
        if (size > 0) {
            bodyMaker.addAdminList(adminListResult);
        } else {
            bodyMaker.addIntBytes(0);
        }
        resBody = bodyMaker.getBody();
        Header resHeader = new Header(
                Header.TYPE_RESPONSE,
                Header.ACTOR_SERVER,
                Header.CODE_SERVER_RES_ADMIN_INFO,
                resBody.length
        );
        outputStream.write(resHeader.getBytes());
        outputStream.write(resBody);
    }
    public void searchUser(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        String id = inputStream.readUTF();
        String phoneNum = inputStream.readUTF();
        List<UserDTO> userListResult = managerDAO.inquiryUser(id, phoneNum);
        System.out.println("받은거 : " + id + ", " + phoneNum);
        BodyMaker bodyMaker = new BodyMaker();
        byte[] resBody;
        int size = userListResult.size();
        if (size > 0) {
            bodyMaker.addUserList(userListResult);
        } else {
            bodyMaker.addIntBytes(0);
        }
        resBody = bodyMaker.getBody();
        Header resHeader = new Header(
                Header.TYPE_RESPONSE,
                Header.ACTOR_SERVER,
                Header.CODE_SERVER_RES_USER_INFO,
                resBody.length
        );
        outputStream.write(resHeader.getBytes());
        outputStream.write(resBody);
    }
    public void searchInfirmary(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        String name = inputStream.readUTF();
        List<InfirmaryDTO> infirmaryListResult = managerDAO.inquiryInfirmary(name);

        BodyMaker bodyMaker = new BodyMaker();
        byte[] resBody;

        int size = infirmaryListResult.size();
        if (size > 0) {
            bodyMaker.addInfimaryList(infirmaryListResult);
        } else {
            bodyMaker.addIntBytes(0);
        }
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
    public void viewAdminList(DataOutputStream outputStream) throws IOException {
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<AdminDTO> adminListResult = managerDAO.inquiryAdminListAll();

        BodyMaker bodyMaker = new BodyMaker();
        byte[] resBody;

        int size = adminListResult.size();
        if (size > 0) {
            bodyMaker.addAdminList(adminListResult);
        } else {
            bodyMaker.addIntBytes(0);
        }
        resBody = bodyMaker.getBody();
        Header resHeader = new Header(
                Header.TYPE_RESPONSE,
                Header.ACTOR_SERVER,
                Header.CODE_SERVER_RES_ADMIN_LIST,
                resBody.length
        );
        outputStream.write(resHeader.getBytes());
        outputStream.write(resBody);
    }
    public void viewUserList(DataOutputStream outputStream) throws IOException {
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<UserDTO> userListResult = managerDAO.inquiryUserListAll();

        BodyMaker bodyMaker = new BodyMaker();
        byte[] resBody;

        int size = userListResult.size();
        if (size > 0) {
            bodyMaker.addUserList(userListResult);
        } else {
            bodyMaker.addIntBytes(0);
        }
        resBody = bodyMaker.getBody();
        Header resHeader = new Header(
                Header.TYPE_RESPONSE,
                Header.ACTOR_SERVER,
                Header.CODE_SERVER_RES_USER_LIST,
                resBody.length
        );
        outputStream.write(resHeader.getBytes());
        outputStream.write(resBody);
    }
    public void viewInfirmaryList(DataOutputStream outputStream) throws IOException {
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        AdminDAO adminDAO = new AdminDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        List<InfirmaryDTO> infirmaryListResult = managerDAO.inquiryInfirmaryListAll();

        for(int i = 0; i < infirmaryListResult.size(); i++)
        {
            AdminDTO adminDTO = adminDAO.inquiryAdmin(infirmaryListResult.get(i).getAdmin_pk());
            infirmaryListResult.get(i).setAdmin_name(adminDTO.getName());
            infirmaryListResult.get(i).setAdmin_phone_num(adminDTO.getPhone_num());
            System.out.println(infirmaryListResult.get(i));
        }
        System.out.println("첫번째 : " +infirmaryListResult.get(0));
        BodyMaker bodyMaker = new BodyMaker();
        byte[] resBody;
        bodyMaker.addInfimaryList(infirmaryListResult);
        resBody = bodyMaker.getBody();
        outputStream.write(resBody);
    }

    public void viewNotApprovedInfirmaryList(DataOutputStream outputStream) throws IOException {
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        AdminDAO adminDAO = new AdminDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        List<InfirmaryDTO> infirmaryDTOS = managerDAO.inquiryNotApprovedInfirmaryAll();
        for(int i = 0; i < infirmaryDTOS.size(); i++)
        {
            AdminDTO adminDTO = adminDAO.inquiryAdmin(infirmaryDTOS.get(i).getAdmin_pk());
            infirmaryDTOS.get(i).setAdmin_name(adminDTO.getName());
            infirmaryDTOS.get(i).setAdmin_phone_num(adminDTO.getPhone_num());
        }
        BodyMaker bodyMaker = new BodyMaker();
        byte[] resBody;

        bodyMaker.addInfimaryList(infirmaryDTOS);
        resBody = bodyMaker.getBody();
        System.out.println(2);
        outputStream.write(resBody);

        System.out.println("보냄");
    }

    public void acceptInfirmary(DataInputStream inputStream) throws IOException {
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        int pk = inputStream.readInt();
        managerDAO.acceptInfirmary(pk);
    }
    public void deleteAdmin(DataInputStream inputStream) throws IOException {
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        int pk = inputStream.readInt();
        managerDAO.deleteAdmin(pk);
    }
    public void deleteUser(DataInputStream inputStream) throws IOException {
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        int pk = inputStream.readInt();
        managerDAO.deleteUser(pk);
        System.out.println("삭제함 : "+pk);
    }
    public void deleteInfirmary(DataInputStream inputStream) throws IOException {
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryMedicineDAO infirmaryMedicineDAO = new InfirmaryMedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        MedicineBookmarkDAO medicineBookmarkDAO = new MedicineBookmarkDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        MedicineAlarmDAO medicineAlarmDAO = new MedicineAlarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryNoticeDAO infirmaryNoticeDAO = new InfirmaryNoticeDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryAlarmDAO infirmaryAlarmDAO = new InfirmaryAlarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        int pk = inputStream.readInt();
        userDAO.changeInfirmaryPK(pk);
        medicineBookmarkDAO.changeInfirmaryPK(pk);
        medicineAlarmDAO.changeInfirmaryPK(pk);
        infirmaryNoticeDAO.deleteInfirmaryPK(pk);
        infirmaryMedicineDAO.deleteInfirmaryMedicine(pk);
        infirmaryAlarmDAO.deleteInfirmaryPK(pk);
        managerDAO.deleteInfirmary(pk);

        System.out.println("삭제함 : "+pk);
    }
    public void viewNotApproveAdminList(DataOutputStream outputStream) throws IOException {
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<AdminDTO> adminListResult = managerDAO.inquiryNotApprovedAdminListAll();

        BodyMaker bodyMaker = new BodyMaker();
        byte[] resBody;

        int size = adminListResult.size();
        if (size > 0) {
            bodyMaker.addAdminList(adminListResult);
        } else {
            bodyMaker.addIntBytes(0);
        }
        resBody = bodyMaker.getBody();
        Header resHeader = new Header(
                Header.TYPE_RESPONSE,
                Header.ACTOR_SERVER,
                Header.CODE_SERVER_RES_ADMIN_LIST,
                resBody.length
        );
        outputStream.write(resHeader.getBytes());
        outputStream.write(resBody);
    }

    public void acceptAdmin(DataInputStream inputStream) throws IOException {
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        int pk = inputStream.readInt();
        managerDAO.acceptAdmin(pk);
    }

    public void viewAdminImage(DataInputStream inputStream,DataOutputStream outputStream) throws IOException {
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        int pk = inputStream.readInt();

        Byte[] fileData = managerDAO.inquiryAdminFile(pk);
        byte[] resBody =  new byte[fileData.length];
        int size = fileData.length;
        for (int i = 0; i < fileData.length; i++) {
            resBody[i] = fileData[i];
        }
        outputStream.writeInt(size);
        outputStream.write(resBody);
    }
    public void viewInfirmaryImage(DataInputStream inputStream,DataOutputStream outputStream) throws IOException {
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        int pk = inputStream.readInt();
        Byte[] fileData = managerDAO.inquiryInfirmaryFile(pk);
        byte[] resBody =  new byte[fileData.length];
        int size = fileData.length;
        for (int i = 0; i < fileData.length; i++) {
            resBody[i] = fileData[i];
        }
        outputStream.writeInt(size);
        outputStream.write(resBody);
    }
}
