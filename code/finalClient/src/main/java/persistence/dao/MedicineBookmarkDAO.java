package persistence.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import persistence.dto.InfirmaryNoticeDTO;
import persistence.dto.MedicineBookmarkDTO;
import persistence.dto.UserDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicineBookmarkDAO {
    private final SqlSessionFactory sqlSessionFactory;

    public MedicineBookmarkDAO(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public int registerBookmark(MedicineBookmarkDTO medicineBookmarkDTO) {// 즐겨찾기 등록(약 저장)
        SqlSession session = sqlSessionFactory.openSession();
        int row = 0;
        try {
            row = session.insert("mapper.MedicineBookmarkMapper.insertMedicineBookmark", medicineBookmarkDTO);
            session.commit();
        }finally {
            session.close();
        }

        return row;
    }

    public int selectMedBookmarkPk(String medPk, int userPk)
    {
        SqlSession session = sqlSessionFactory.openSession();
        Map<String, Object> map = new HashMap<>();
        int medicine_pk = Integer.parseInt(medPk);
        map.put("medicine_pk", medicine_pk);
        map.put("user_pk", userPk);
        
        int pk = 0;

        try {
            pk = session.selectOne("mapper.MedicineBookmarkMapper.selectMedBookmarkPk", map);
            session.commit();
        } catch(Exception e) {
            System.out.println(e);
            session.rollback();
        }
        finally {
            session.close();
        }
        
        return pk;
    }

    public void changeInfirmaryPK(int pk) {
        SqlSession session = sqlSessionFactory.openSession();
        Map<String, Integer> map = new HashMap<>();
        map.put("infirmary_pk", null);
        map.put("before", pk);

        try {
            session.update("mapper.MedicineBookmarkMapper.changeInfirmaryPK", map);
            session.commit();
        } catch(Exception e) {
            System.out.println(e);
            session.rollback();
        }
        finally {
            session.close();
        }
    }

//    public List<MedicineBookmarkDTO> selectMedicinePk(int user_pk){
//        SqlSession session = sqlSessionFactory.openSession();
//        List<MedicineBookmarkDTO> list = null;
//
//        try {
//            list = session.selectList("mapper.MedicineBookmarkMapper.selectMedicinePk", user_pk);
//        }
//        finally {
//            session.close();
//        }
//        return list;
//    }

    public List<Integer> selectMedicinePk(int user_pk) {
        SqlSession session = sqlSessionFactory.openSession();
        List<Integer> list = null;

        try {
            list = session.selectList("mapper.MedicineBookmarkMapper.selectMedicinePk", user_pk);
        } finally {
            session.close();
        }

        return list;
    }
}
