package persistence.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import persistence.dto.InfirmaryAlarmDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfirmaryAlarmDAO {

    private final SqlSessionFactory sqlSessionFactory;

    public InfirmaryAlarmDAO(SqlSessionFactory sqlSessionFactory){
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public int registerInfirmaryAlarm(InfirmaryAlarmDTO infirmaryAlarmDTO) {// 보건실 알림 등록
        SqlSession session = sqlSessionFactory.openSession();
        int row = 0;
        try {
            row = session.insert("mapper.InfirmaryAlarmMapper.insertInfirmaryAlarm", infirmaryAlarmDTO); //파라미터로 받은 infirmaryAlarmDTO를 데이터 INSERT
            session.commit();
        } finally {
            session.close();
        }
        return row;
    }

    public InfirmaryAlarmDTO inquiryAlarmToTime(LocalDateTime transfer_time, int infirmaryPk)
    {
        SqlSession session = sqlSessionFactory.openSession();

        List<InfirmaryAlarmDTO> infirmaryAlarmDTO = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("infirmary_pk", infirmaryPk);
        map.put("transfer_time", transfer_time);

        try {
            infirmaryAlarmDTO = session.selectList("mapper.InfirmaryAlarmMapper.selectAlarm", map);
            session.commit();
        } catch(Exception e) {
            System.out.println(e);
            session.rollback();
        }
        finally {
            session.close();
        }

        return infirmaryAlarmDTO.get(0);
    }

    public String inquiryInfirmaryAlarm(InfirmaryAlarmDTO infirmaryAlarmDTO) { // 보건실 알림 조회
        SqlSession session = sqlSessionFactory.openSession();
        List<InfirmaryAlarmDTO> infirmaryAlarmDTOS = null; // infirmaryAlarmDTO를 담기 위한 list 생성
        System.out.println("[보건실 알림 조회]");
        try {
            infirmaryAlarmDTOS = session.selectList("mapper.InfirmaryAlarmMapper.selectInfirmaryAlarmInfo", infirmaryAlarmDTO);
        }
        finally {
            session.close();
        }
        String str = "";
        for(InfirmaryAlarmDTO item : infirmaryAlarmDTOS)
            str += item.toString() + "\n"; // string으로 변환
        return str; // str 값 반환
    }

    public void deleteInfirmaryPK(int pk) {
        SqlSession session = sqlSessionFactory.openSession();
        Map<String, Integer> map = new HashMap<>();
        map.put("infirmary_pk", pk);

        try {
            session.delete("mapper.InfirmaryAlarmMapper.deleteInfirmaryPK", map);
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
