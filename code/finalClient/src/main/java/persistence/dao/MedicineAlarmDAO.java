package persistence.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import persistence.dto.MedicineAlarmDTO;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicineAlarmDAO {
    private final SqlSessionFactory sqlSessionFactory;

    public MedicineAlarmDAO(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public int insertMedicineAlarm(MedicineAlarmDTO medicineAlarmDTO) {// 이용자 복용 알림 설정
        SqlSession session = sqlSessionFactory.openSession();

        int row = 0;
        try {
            row = session.insert("mapper.MedicineAlarmMapper.insertMedicineAlarm", medicineAlarmDTO);
            session.commit();
        } finally {
            session.close();
        }
        return row;
    }

    public int selectUserAlarm(int userPk, int bookPk, LocalTime time)
    {
        SqlSession session = sqlSessionFactory.openSession();
        int pk;

        Map<String, Object> map = new HashMap<>();
        map.put("user_pk", userPk);
        map.put("bookmark_pk", bookPk);
        map.put("time", time);

        int row = 0;
        try {
            pk = session.selectOne("mapper.MedicineAlarmMapper.selectUserMed", map);
        } finally {
            session.close();
        }
        return pk;
    }

    public void deleteUserAlarm(int pk)
    {
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.delete("mapper.MedicineAlarmMapper.deleteUserMed", pk);
            session.commit();
        } finally {
            session.close();
        }
    }

    public void changeInfirmaryPK(int pk) {
        SqlSession session = sqlSessionFactory.openSession();
        Map<String, Integer> map = new HashMap<>();
        map.put("infirmary_pk", null);
        map.put("before", pk);

        try {
            session.update("mapper.MedicineAlarmMapper.changeInfirmaryPK", map);
            session.commit();
        } catch(Exception e) {
            System.out.println(e);
            session.rollback();
        }
        finally {
            session.close();
        }
    }
}