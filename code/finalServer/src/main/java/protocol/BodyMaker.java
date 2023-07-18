package protocol;

import persistence.MyBatisConnectionFactory;
import persistence.dao.MedicineDAO;
import persistence.dto.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class BodyMaker
{
    ByteArrayOutputStream buf;
    DataOutputStream dos;

    public BodyMaker() {
        buf = new ByteArrayOutputStream();
        dos = new DataOutputStream(buf);
    }

    public int getSize() {return buf.size();}

    public void add(MySerializableClass object) throws IOException {
        dos.write(object.getBytes());
    }

    public void add(List<MySerializableClass> list) throws IOException {

        dos.writeInt(list.size());
        for(MySerializableClass object : list) dos.write(object.getBytes());

    }
    public void addAdminList(List<AdminDTO> list) throws IOException {

        dos.writeInt(list.size());
        for(AdminDTO adminDTO : list) dos.write(adminDTO.getBytes());

    }
    public void addUserList(List<UserDTO> list) throws IOException {
    dos.writeInt(list.size());
    for(UserDTO adminDTO : list) dos.write(adminDTO.getBytes());
}
    public void addInfimaryList(List<InfirmaryDTO> list) throws IOException {
        dos.writeInt(list.size());
        for(InfirmaryDTO infirmaryDTO : list)
        {
            dos.write(infirmaryDTO.getBytes());
        }
    }
    public void addInfimary(InfirmaryDTO infirmaryDTO) throws IOException {
        dos.write(infirmaryDTO.getBytes());
    }
    public void addIntBytes(int integer) throws IOException {
        dos.writeInt(integer);
    }

    public void addStringBytes(String str) throws IOException {
        dos.writeUTF(str);
    }
    public void addBoolean(boolean str) throws IOException {
        dos.writeBoolean(str);
    }

    public byte[] getBody() {
        return buf.toByteArray();
    }


    public void addRefineData(List<RefineData> resultList) throws IOException {
        dos.writeInt(resultList.size());
        for(RefineData refineData : resultList) dos.write(refineData.getBytes());
    }

    public void addInfimaryMedicineList(List<InfirmaryMedicineDTO> infirmaryMedicineDTOS) throws IOException {
        dos.writeInt(infirmaryMedicineDTOS.size());
        for(InfirmaryMedicineDTO infirmaryMedicineDTO : infirmaryMedicineDTOS) dos.write(infirmaryMedicineDTO.getBytes());
    }

    public void addInfirmaryNoticeList(List<InfirmaryNoticeDTO> noticeListResult) throws IOException {
        dos.writeInt(noticeListResult.size());
        for(InfirmaryNoticeDTO infirmaryNoticeDTO : noticeListResult) dos.write(infirmaryNoticeDTO.getBytes());
    }

    public void addInfirmaryMedicineNum(List<InfirmaryMedicineDTO> medicineListResult) throws IOException {
        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        dos.writeInt(medicineListResult.size());
        for(InfirmaryMedicineDTO infirmaryMedicineDTO : medicineListResult)
        {
            String medicineName = medicineDAO.inquiryMedicineNameBySeqNum(String.valueOf(infirmaryMedicineDTO.getMedicine_pk()));
            dos.write(infirmaryMedicineDTO.getBytes());
            dos.writeUTF(medicineName);
        }
    }
}
