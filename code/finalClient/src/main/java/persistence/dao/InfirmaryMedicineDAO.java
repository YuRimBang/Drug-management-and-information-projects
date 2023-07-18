package persistence.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import persistence.dto.AdminDTO;
import persistence.dto.InfirmaryDTO;
import persistence.dto.InfirmaryMedicineDTO;
import persistence.dto.InfirmaryNoticeDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfirmaryMedicineDAO {

    private final SqlSessionFactory sqlSessionFactory;

    public InfirmaryMedicineDAO(SqlSessionFactory sqlSessionFactory){
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public List<InfirmaryMedicineDTO> selectAllByInfirmaryPk(InfirmaryDTO infirmaryDTO){ //보건실의 모든 보건실 약 조회
        SqlSession session = sqlSessionFactory.openSession();
        List<InfirmaryMedicineDTO> dtos = new ArrayList<>();
        try{
            dtos = session.selectList("mapper.InfirmaryMedicineMapper.selectAll", infirmaryDTO.getPk());
        }finally {
            session.close();
        }
        return dtos;
    }

    public List<InfirmaryMedicineDTO> selectAllByInfirmaryPk(int infirmary_pk){ //보건실의 모든 보건실 약 조회
        SqlSession session = sqlSessionFactory.openSession();
        List<InfirmaryMedicineDTO> dtos = new ArrayList<>();
        try{
            dtos = session.selectList("mapper.InfirmaryMedicineMapper.selectAll", infirmary_pk);
            for(InfirmaryMedicineDTO infirmaryMedicineDTO : dtos){
                Map<String, Object> map = new HashMap<>();
                System.out.println("medicine_pk: " + infirmaryMedicineDTO.getMedicine_pk());
                map.put("medicine_pk", infirmaryMedicineDTO.getMedicine_pk());
                String medicine_name = session.selectOne("mapper.InfirmaryMedicineMapper.selectMedicineName", map);
                infirmaryMedicineDTO.setMedicine_name(medicine_name);
            }

        }finally {
            session.close();
        }
        return dtos;
    }

    public int insertInfirmaryMedicine(InfirmaryMedicineDTO infirmaryMedicineDTO) {// 보건실 약 추가
        SqlSession session = sqlSessionFactory.openSession(); // SqlSession 객체 열기
        int row = 0;
        try {
            row = session.insert("mapper.InfirmaryMedicineMapper.insertInfirmaryMedicine", infirmaryMedicineDTO); //파라메터로 받은 infirmaryMedicineDTO 데이터 INSERT
            session.commit();
        }
        catch (Exception e) {
            session.rollback();
        }
        finally {
            session.close();
        }
        return row;
    }

    public List<InfirmaryMedicineDTO> inquiryMedicineStock(int infirmary_pk) // 보건실 약 재고 조회
    {
        SqlSession session = sqlSessionFactory.openSession();
        List<InfirmaryMedicineDTO> list = new ArrayList<>();

        try{
            list = session.selectList("mapper.InfirmaryMedicineMapper.selectMedicineStock", infirmary_pk);
            session.commit();
        } catch(Exception e) {
            session.rollback();
        } finally {
            session.close();
        }

        return list;
    }

    public int updateInfirmaryMedicine(InfirmaryMedicineDTO infirmaryMedicineDTO) {
        SqlSession session = sqlSessionFactory.openSession();
        int row = 0;
        try {
            row = session.insert("mapper.InfirmaryMedicineMapper.updateMedicineStock", infirmaryMedicineDTO);
            session.commit();
        }finally {
            session.close();
        }
        return row;
    }

    public int plusInfirmaryMedicineNum(int pk) {
        SqlSession session = sqlSessionFactory.openSession();
        int row = 0;
        try {
            row = session.insert("mapper.InfirmaryMedicineMapper.plusMedicineStock", pk);
            session.commit();
        }finally {
            session.close();
        }
        return row;
    }

    public int minusInfirmaryMedicineNum(int pk) {
        SqlSession session = sqlSessionFactory.openSession();
        int row = 0;
        try {
            row = session.insert("mapper.InfirmaryMedicineMapper.minusMedicineStock", pk);
            session.commit();
        }finally {
            session.close();
        }
        return row;
    }

    public void deleteInfirmaryMedicine(int pk)
    {
        SqlSession session = sqlSessionFactory.openSession();

        Map<String, Object> map = new HashMap<>();
        map.put("pk", pk);

        try{
            session.delete("mapper.InfirmaryMedicineMapper.deleteInfirmaryMedicine", map);
            session.commit();
        } catch(Exception e) {
            session.rollback();
        } finally {
            session.close();
        }
    }
}
