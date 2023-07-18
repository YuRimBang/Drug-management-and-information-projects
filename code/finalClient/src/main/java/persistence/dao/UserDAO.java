package persistence.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import persistence.dto.InfirmaryDTO;
import persistence.dto.UserDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO {
    private final SqlSessionFactory sqlSessionFactory;

    public UserDAO(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public boolean isIdDuplicateCheck(String id){ // id 중복체크
        SqlSession session = sqlSessionFactory.openSession();
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        boolean flag = false;
        int cnt = 0;
        try{
            cnt = session.selectOne("mapper.UserMapper.isIdDuplicateCheck", map);
        } finally {
            session.close();
        }
        if(cnt != 0)
            flag = true;

        return flag;
    }

    public boolean isPhoneNumDuplicateCheck(String phoneNum){ // 전화번호 중복 체크
        SqlSession session = sqlSessionFactory.openSession();
        Map<String, Object> map = new HashMap<>();
        map.put("phone_num", phoneNum);
        boolean flag = false;
        int cnt = 0;
        try{
            cnt = session.selectOne("mapper.UserMapper.isPhoneNumDuplicateCheck", map);
        } finally {
            session.close();
        }
        if(cnt != 0)
            flag = true;

        return flag;
    }

    public int signUpUser(UserDTO userDTO) {// 이용자 회원가입
        SqlSession session = sqlSessionFactory.openSession();
        int row = 0;
        try {
            row = session.insert("mapper.UserMapper.insertUser", userDTO);
            session.commit();
        }finally {
            session.close();
        }
        return row;
    }

    public int unRegister(int userPk) {// 이용자 회원탈퇴
        SqlSession session = sqlSessionFactory.openSession();
        Map<String, Object> map = new HashMap<>();
        map.put("pk", userPk);
        int row = 0;
        try {
            row = session.insert("mapper.UserMapper.deleteUser", map);
            session.commit();
        }finally {
            session.close();
        }
        return row;
    }

    public List<InfirmaryDTO> viewSchoolList() //이용자 회원가입, 개인정보 수정 - 학교 리스트 반환
    {
        SqlSession session = sqlSessionFactory.openSession();
        List<InfirmaryDTO> list = new ArrayList<>();

        try{
            list = session.selectList("mapper.InfirmaryMapper.infirmaryAll");
        } finally {
            session.close();
        }
        return list;
    }

    public InfirmaryDTO getInfirmaryPk(String school) { //이용자 회원가입, 개인정보 수정 - 선택학교의 infirmary_pk
        SqlSession session = sqlSessionFactory.openSession();
        InfirmaryDTO infirmaryDTO = new InfirmaryDTO();
        try {
            infirmaryDTO = session.selectOne("mapper.InfirmaryMapper.selectInfirmaryInfo", school);
        }
        finally {
            session.close();
        }

        return infirmaryDTO;
    }

    public String getSchool(int pk) { //이용자의 학교 반환
        SqlSession session = sqlSessionFactory.openSession();
        String school = "";
        try {
            school = session.selectOne("mapper.UserMapper.selectSchool", pk);
        }
        finally {
            session.close();
        }

        return school;
    }

    public int getInfirmary_pk(int pk) { //이용자의 infirmary_pk 반환
        SqlSession session = sqlSessionFactory.openSession();
        int infirmary_pk = 0;
        try {
            infirmary_pk = session.selectOne("mapper.UserMapper.selectInfirmary_pk", pk);
        }
        finally {
            session.close();
        }

        return infirmary_pk;
    }

    public UserDTO login(String id, String pw) { //로그인
        SqlSession session = sqlSessionFactory.openSession();
        UserDTO userDTO = new UserDTO();
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("pw", pw);
        try {
            userDTO = session.selectOne("mapper.UserMapper.login", map); // 파라미터로 받은 map을 이용하여 select하고 dto에 담음

        }finally {
            session.close(); //SqlSession 객체 닫기
        }
        return userDTO; //userDTO 반환
    }

    public String findId(String name, String phoneNum) { // 회원 아이디 조회
        SqlSession session = sqlSessionFactory.openSession();
        Object id = null;
        String result = null;
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("phone_num", phoneNum);

        System.out.println("[회원 아이디 조회]");
        try {
            id = session.selectOne("mapper.UserMapper.selectId", map);
            result = (id != null) ? id.toString() : null;

        }
        finally {
            session.close();
        }

        return result; // id 반환
    }

    public String findPw(String name, String phoneNum, String id) { // 회원 비밀번호 조회
        SqlSession session = sqlSessionFactory.openSession();
        String pw = null;
        Object result = null;
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("phone_num", phoneNum);
        map.put("id", id);

        System.out.println("[회원 비밀번호 조회]");
        try {
            result = session.selectOne("mapper.UserMapper.selectPw", map);
            pw = (result != null) ? result.toString() : null;
        }
        finally {
            session.close();
        }
        return pw; // pw 반환
    }

    public int updateName(UserDTO userDTO) { // 개인 정보 수정 - 이름 수정
        SqlSession session = sqlSessionFactory.openSession();
        int row = 0;
        try {
            row = session.update("mapper.UserMapper.updateName", userDTO);
            session.commit();
        } catch (Exception e) {
            session.rollback();
        } finally {
            session.close();
        }
        return row;
    }

    public int updatePhoneNum(UserDTO userDTO) { // 개인 정보 수정 - 전화번호 수정
        SqlSession session = sqlSessionFactory.openSession();
        int row = 0;
        try {
            row = session.update("mapper.UserMapper.updatePhoneNum", userDTO);
            session.commit();
        } catch (Exception e) {
            session.rollback();
        } finally {
            session.close();
        }
        return row;
    }

    public int updateSchool(UserDTO userDTO) { // 학교명 수정
        SqlSession session = sqlSessionFactory.openSession();
        int row = 0;
        try {
            row = session.update("mapper.UserMapper.updateSchool", userDTO);
            session.commit();
        } catch (Exception e) {
            session.rollback();
        } finally {
            session.close();
        }
        return row;
    }

    public String checkPw(int pk) { // 비밀번호 수정을 위한 확인
        SqlSession session = sqlSessionFactory.openSession();
        String pw = null;
        try {
            pw = session.selectOne("mapper.UserMapper.selectPwForUpdate", pk);
        }
        finally {
            session.close();
        }
        return pw;
    }

    public int updatePw(UserDTO userDTO) { // 회원 비밀번호 수정
        SqlSession session = sqlSessionFactory.openSession();
        int row = 0;
        try {
            row = session.update("mapper.UserMapper.updatePw", userDTO);
            session.commit();
        } catch (Exception e) {
            session.rollback();
        } finally {
            session.close();
        }
        return row;
    }

    public List<UserDTO> inquiryUser(UserDTO userDTO){ //개인 정보 조회
        SqlSession session = sqlSessionFactory.openSession();
        List<UserDTO> userList = null;

        try {
            userList = session.selectList("mapper.UserMapper.selectUserByPk", userDTO);
        }
        finally {
            session.close();
        }
        return userList;
    }

    public void changeInfirmaryPK(int pk)
    {
        SqlSession session = sqlSessionFactory.openSession();
        Map<String, Integer> map = new HashMap<>();
        map.put("infirmary_pk", null);
        map.put("before", pk);

        try {
            session.update("mapper.UserMapper.changeInfirmaryPK", map);
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