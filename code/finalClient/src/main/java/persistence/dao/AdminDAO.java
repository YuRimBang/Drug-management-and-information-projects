package persistence.dao;

//import medicineAPI.DetailInfo;
//import medicineAPI.ThisMedicine;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import persistence.dto.AdminDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDAO {

    private final SqlSessionFactory sqlSessionFactory;

    public AdminDAO(SqlSessionFactory sqlSessionFactory){
        this.sqlSessionFactory = sqlSessionFactory;
    }



    public Byte[] inquiryAdminFile(int pk) { // 파일 출력
        SqlSession session = sqlSessionFactory.openSession(); // sqlSession 열기
        Map<String, Object> map = new HashMap<>();
        Byte[] bytes = null;

        map.put("pk", pk);
        System.out.println("[운영자 파일 조회]");
        try {
            bytes = session.selectOne("mapper.AdminMapper.selectAdminFile", map); // map을 이용하여 select 하고 list에 담음
        }
        finally {
            session.close();
        }

        return bytes;
    }

    public String inquiryAdminName(int pk){
        SqlSession session = sqlSessionFactory.openSession();
        String name = null;
        Map<String, Object> map = new HashMap<>();
        map.put("pk", pk);
        try{
            name = session.selectOne("mapper.AdminMapper.selectName", pk);
        } finally {
            session.close();
        }
        return name;
    }

    public boolean isIdDuplicateCheck(String id){ // id 중복체크
        SqlSession session = sqlSessionFactory.openSession();
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        boolean flag = false;
        int cnt = 0;
        try{
            cnt = session.selectOne("mapper.AdminMapper.isIdDuplicateCheck", map);
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
            cnt = session.selectOne("mapper.AdminMapper.isPhoneNumDuplicateCheck", map);
        } finally {
            session.close();
        }
        if(cnt != 0)
            flag = true;

        return flag;
    }

    public int signUpAdmin(AdminDTO adminDTO) {// 운영자 회원가입
        SqlSession session = sqlSessionFactory.openSession(); // SqlSession 객체 열기
        int row = 0;
        try {
            row = session.insert("mapper.AdminMapper.insertAdmin", adminDTO); //파라미터로 받은 adminDTO 데이터 INSERT
            session.commit();
        }
        catch (Exception e) {
            session.rollback();
        }
        finally {
            session.close(); //SqlSession 객체 닫기
        }
        return row; // 성공 여부 반환
    }

    public List<AdminDTO> inquiryAdmin(AdminDTO adminDTO) { // 운영자 정보 조회
        SqlSession session = sqlSessionFactory.openSession(); // sqlSession 열기
        List<AdminDTO> adminDTOS = null; // adminDTO를 담기 위한 list 생성
        Map<String, Object> map = new HashMap<>();

        map.put("pk", adminDTO.getPk());
        System.out.println("[운영자 정보 조회]");
        try {
            adminDTOS = session.selectList("mapper.AdminMapper.selectAdminInfo", map); // map을 이용하여 select 하고 list에 담음
        }
        finally {
            session.close(); // SqlSession 객체 닫음
        }

        return adminDTOS;
    }



    public String findId(String name, String phoneNum) { // 운영자 아이디 조회
        SqlSession session = sqlSessionFactory.openSession(); // sqlSession 열기
        Object id = null;
        String result = null;
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("phone_num", phoneNum);

        
        System.out.println("[운영자 아이디 조회]");
        try {
            id = session.selectOne("mapper.AdminMapper.selectId", map);
            result = (id != null) ? id.toString() : null;
        }
        finally {
            session.close(); // SqlSession 객체 닫음
        }

        return result; // id 반환
    }

    public String findPw(String name, String phoneNum, String id) { // 운영자 비밀번호 조회
        SqlSession session = sqlSessionFactory.openSession();
        String pw = null;
        Object result = null;
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("phone_num", phoneNum);
        map.put("id", id);

        System.out.println("[운영자 비밀번호 조회]");
        try {
            result = session.selectOne("mapper.AdminMapper.selectPw", map);
            pw = (result != null) ? result.toString() : null;

        }
        finally {
            session.close(); // SqlSession 객체 닫음
        }
        return pw; // pw 반환
    }

    public String checkPw(int pk) { // 비밀번호 수정을 위한 확인
        SqlSession session = sqlSessionFactory.openSession();
        String pw = null;
        try {
            pw = session.selectOne("mapper.AdminMapper.selectPwForUpdate", pk);
        }
        finally {
            session.close();
        }
        return pw;
    }

    public int updatePw(AdminDTO adminDTO) { // 운영자 비밀번호 수정
        SqlSession session = sqlSessionFactory.openSession();
        int row = 0;
        try {
            row =  session.update("mapper.AdminMapper.updatePw", adminDTO);
            session.commit();
        } catch (Exception e) {
            session.rollback();
        } finally {
            session.close();
        }
        return row;
    }

    public int updateAdminInfo(AdminDTO adminDTO) { // 운영자 정보 수정
        SqlSession session = sqlSessionFactory.openSession();
        int row = 0;
        try {
            row =  session.update("mapper.AdminMapper.updateAdminInfo", adminDTO);
            session.commit();
        } catch (Exception e) {
            session.rollback();
        } finally {
            session.close();
        }

        return row;
    }

    public AdminDTO login(String id, String pw) { //로그인
        SqlSession session = sqlSessionFactory.openSession();
        AdminDTO adminDTO = new AdminDTO(); // AdminDTO를 담기 위한 list 생성
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("pw", pw);
        try {
            adminDTO = session.selectOne("mapper.AdminMapper.login", map); // 파라미터로 받은 map을 이용하여 select하고 dto에 담음
        }finally {
            session.close();
        }
        return adminDTO;
    }

    public void deleteAdmin(int pk)
    {
        SqlSession session = sqlSessionFactory.openSession();
        Map<String, Integer> map = new HashMap<>();
        map.put("pk", pk);
        try {
            session.delete("mapper.AdminMapper.deleteAdmin", map); // 파라미터로 받은 map을 이용하여 select하고 dto에 담음
            session.commit();
        }catch (Exception e)
        {
            System.out.println(e);
            session.rollback();
        }
        finally {
            session.close();
        }
    }
}
