package persistence.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import persistence.dto.InfirmaryDTO;
import persistence.dto.InfirmaryNoticeDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfirmaryDAO {

    private final SqlSessionFactory sqlSessionFactory;

    public InfirmaryDAO(SqlSessionFactory sqlSessionFactory){
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public InfirmaryDTO registerInfirmary(InfirmaryDTO infirmaryDTO) {// 보건실 등록
        SqlSession session = sqlSessionFactory.openSession(); // SqlSession 객체 열기
        try {
            session.insert("mapper.InfirmaryMapper.insertInfirmary", infirmaryDTO); //파라메터로 받은 infirmaryDTO 데이터 INSERT
            String name = session.selectOne("mapper.InfirmaryMapper.selectAdminName", infirmaryDTO);
            String phone_num = session.selectOne("mapper.InfirmaryMapper.selectAdminPhoneNum", infirmaryDTO);
            session.commit();
        }
        catch (Exception e) {
            session.rollback();
        }
        finally {
            session.close(); //SqlSession 객체 닫기
        }
        return infirmaryDTO;
    }

    public boolean isPhoneNumDuplicateCheck(String phoneNum){ // 전화번호 중복 체크
        SqlSession session = sqlSessionFactory.openSession();
        Map<String, Object> map = new HashMap<>();
        map.put("phone_num", phoneNum);
        boolean flag = false;
        int cnt = 0;
        try{
            cnt = session.selectOne("mapper.InfirmaryMapper.isPhoneNumDuplicateCheck", map);
        } finally {
            session.close();
        }
        if(cnt != 0)
            flag = true;

        return flag;
    }

    public InfirmaryDTO inquiryInfirmary(InfirmaryDTO infirmaryDTO) { // 보건실 정보 조회
        SqlSession session = sqlSessionFactory.openSession();

        System.out.println("[보건실 정보 조회]");
        try {
            infirmaryDTO = session.selectOne("mapper.InfirmaryMapper.selectInfirmaryInfo", infirmaryDTO);
            String admin_name = session.selectOne("mapper.InfirmaryMapper.selectAdminName", infirmaryDTO);
            infirmaryDTO.setAdmin_name(admin_name);
        }
        finally {
            session.close();
        }

        return infirmaryDTO;
    }

    public InfirmaryDTO updateInfirmaryInfo(InfirmaryDTO infirmaryDTO) { // 보건실 정보 수정
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.update("mapper.InfirmaryMapper.updateInfirmaryInfo", infirmaryDTO);
            String admin_name = session.selectOne("mapper.InfirmaryMapper.selectAdminName", infirmaryDTO);
            infirmaryDTO = session.selectOne("mapper.InfirmaryMapper.selectInfirmaryInfoByAdminPk", infirmaryDTO);
            infirmaryDTO.setAdmin_name(admin_name);
            session.commit();
        } catch (Exception e) {
            session.rollback();
        } finally {
            session.close();
        }
        return infirmaryDTO;
    }

    public int getInfirmaryPk(int admin_pk) { // 보건실 pk 반환
        SqlSession session = sqlSessionFactory.openSession();
        int infirmaryPk = 0;
        try {
            infirmaryPk = session.selectOne("mapper.InfirmaryMapper.getInfirmaryPkByAdminPk", admin_pk);
            session.commit();
        } catch (Exception e) {
            session.rollback();
        } finally {
            session.close();
        }
        return infirmaryPk;
    }


    public int getAdminPk(int pk) { //admin_pk반환
        SqlSession session = sqlSessionFactory.openSession();

        int result = 0;

        try {
            result = session.selectOne("mapper.InfirmaryMapper.selectAdminPk", pk);

            session.commit();
        } catch (Exception e) {
            session.rollback();
        } finally {
            session.close();
        }
        return result;
    }

    public String getSchoolName(int admin_pk) { // 학교 이름 반환
        SqlSession session = sqlSessionFactory.openSession();
        String schoolName = null;
        try {
            schoolName = session.selectOne("mapper.InfirmaryMapper.getSchoolNameByAdminPk", admin_pk);
            session.commit();
        } catch (Exception e) {
            session.rollback();
        } finally {
            session.close();
        }
        return schoolName;
    }
}
