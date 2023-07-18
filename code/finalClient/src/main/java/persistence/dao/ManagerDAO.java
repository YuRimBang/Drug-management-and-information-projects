package persistence.dao;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import persistence.MyBatisConnectionFactory;
import persistence.dto.AdminDTO;
import persistence.dto.InfirmaryDTO;
import persistence.dto.ManagerDTO;
import persistence.dto.UserDTO;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagerDAO {

    private final SqlSessionFactory sqlSessionFactory;

    public ManagerDAO(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;}

    public Byte[] inquiryInfirmaryFile(int pk) { // 보건실 운영 증명서 불러오기
        SqlSession session = sqlSessionFactory.openSession();
        Byte[] fileData = null;
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("pk", pk);
            fileData = session.selectOne("mapper.InfirmaryMapper.selectInfirmaryFile", map);
        } finally {
            session.close();
        }
        return fileData;
    }

    public Byte[] inquiryAdminFile(int pk) { // 운영자 파일 가져오기
        SqlSession session = sqlSessionFactory.openSession();
        Byte[] fileData = null;
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("pk", pk);
            fileData = session.selectOne("mapper.AdminMapper.selectAdminFileByPk", map);
        } finally {
            session.close();
        }
        return fileData;
    }

    public String inquiryMangerName(int pk){
        SqlSession session = sqlSessionFactory.openSession();
        String name = null;
        Map<String, Object> map = new HashMap<>();
        map.put("pk", pk);
        try{
            name = session.selectOne("mapper.ManagerMapper.selectName", pk);
        } finally {
            session.close();
        }
        return name;
    }

    public List<ManagerDTO> selectAll(){
        List<ManagerDTO> dtos = new ArrayList<>();
        SqlSession session = sqlSessionFactory.openSession();
        try{
            dtos = session.selectList("mapper.ManagerMapper.selectAll");
        }finally {
            session.close();
        }
        return dtos;
    }

/*    public void showImage() throws IOException {
        String selectedAdmin = medicineListField.getSelectionModel().getSelectedItem();
        String arr[] = selectedAdmin.split("\\|");
        String name = arr[1];

        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        String ImgUrl = null;
        // 데이터베이스에서 BLOB 타입 파일 가져오기
        byte[] fileData = adminDAO.inquiryAdminFile(adminDTO);

        // byte[] 배열을 이미지로 변환
        try (InputStream inputStream = new ByteArrayInputStream(fileData)) {
            Image image = new Image(inputStream);

            // 이미지를 표시하는 JavaFX ImageView에 설정
            imageView.setImage(image);
        } catch (IOException e) {
            // 예외 처리
            e.printStackTrace();
        }

        ImgUrl = medicineDAO.inquiryMedicineImageByName(name);

        // Alert 다이얼로그 생성
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        if(ImgUrl != null) {
            ImageView imageView = new ImageView();
            imageView.setPreserveRatio(true); // 원본 비율 유지 (Aspect Ratio)

            // 원본 이미지 크기 가져오기
            URL imageUrl = new URL(ImgUrl);
            Image originalImage = new Image(imageUrl.openStream());
            int originalWidth = (int) originalImage.getWidth();
            int originalHeight = (int) originalImage.getHeight();

            // 원하는 크기 계산
            int targetWidth = 500; // 원하는 폭
            int targetHeight = 500 * originalHeight / originalWidth; // Aspect Ratio에 따라 높이 계산

            // 이미지 생성
            Image resizedImage = new Image(imageUrl.openStream(), targetWidth, targetHeight, false, true); // 오류 발생 시, 예외 처리 필요

            // ImageView에 이미지 설정
            imageView.setImage(resizedImage);

            // 이미지 보기
            imageView.setImage(resizedImage);
            alert.setGraphic(imageView);
        } else {
            alert.setContentText("이미지가 존재하지 않습니다.");
        }

        // Alert 다이얼로그 표시
        alert.showAndWait();

    }*/

    public ManagerDTO login(String id, String pw) //로그인
    {
        SqlSession session = sqlSessionFactory.openSession();
        ManagerDTO managerDTO = new ManagerDTO();
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("pw", pw);

        try{
            managerDTO = session.selectOne("mapper.ManagerMapper.login", map);
        } finally {
            session.close();
        }
        return managerDTO;
    }

    public String inquiryId(String name, String phoneNum) //아이디 찾기
    {
        SqlSession session = sqlSessionFactory.openSession();
        Object id = null;
        String result = null;
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("phone_num", phoneNum);

        try{
            id = session.selectOne("mapper.ManagerMapper.findID", map);
            result = (id != null) ? id.toString() : null;
        } finally {
            session.close();
        }

        return result;
    }

    public String inquiryPw(String name, String phoneNum, String id) //비밀번호 찾기
    {
        SqlSession session = sqlSessionFactory.openSession();
        String pw = null;
        Object result = null;
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("phone_num", phoneNum);
        map.put("id", id);

        try{
            result = session.selectOne("mapper.ManagerMapper.findPW",map);
            pw = (result != null) ? result.toString() : null;
        } finally {
            session.close();
        }
        return pw;
    }

    //운영자 관리
    public List<AdminDTO> inquiryAdminListAll() //운영자 목록 조회
    {
        SqlSession session = sqlSessionFactory.openSession();
        List<AdminDTO> list = new ArrayList<>();

        try{
            list = session.selectList("mapper.AdminMapper.checkedAdminAll");
        } finally {
            session.close();
        }
        return list;
    }

    public List<InfirmaryDTO> inquiryNotApprovedInfirmaryListAll() //보건실 승인 요청 목록 조회
    {
        SqlSession session = sqlSessionFactory.openSession();
        List<InfirmaryDTO> list = new ArrayList<>();

        try{
            list = session.selectList("mapper.AdminMapper.NotApprovedAdminAll");
        } finally {
            session.close();
        }
        return list;
    }

    public List<AdminDTO> inquiryNotApprovedAdminListAll() //운영자 승인 요청 목록 조회
    {
        SqlSession session = sqlSessionFactory.openSession();
        List<AdminDTO> list = new ArrayList<>();

        try{
            list = session.selectList("mapper.AdminMapper.NotApprovedAdminAll");
        } finally {
            session.close();
        }
        return list;
    }

    public List<AdminDTO> inquiryAdmin(String name, String phoneNum) //운영자 검색
    {
        SqlSession session = sqlSessionFactory.openSession();
        List<AdminDTO> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("phone_num", phoneNum);

        try{
            list = session.selectList("mapper.AdminMapper.selectAdmin",map);
        } finally {
            session.close();
        }

        return list;
    }

    public int acceptAdmin(int pk) //운영자 승인
    {
        SqlSession session = sqlSessionFactory.openSession();
        int row = 0;
        Map<String, Object> map = new HashMap<>();
        map.put("pk", pk);

        try{
            row = session.update("mapper.AdminMapper.acceptAdmin", map);
            session.commit();
        } catch(Exception e) {
            session.rollback();
        } finally {
            session.close();
        }

        return row;
    }

    public int deleteAdmin(int pk) //운영자 삭제
    {
        SqlSession session = sqlSessionFactory.openSession();
        int row = 0;
        Map<String, Object> map = new HashMap<>();
        map.put("pk", pk);

        try{
            row = session.delete("mapper.AdminMapper.deleteAdmin", map);
            session.commit();
        } catch(Exception e) {
            session.rollback();
        } finally {
            session.close();
        }
        return row;
    }

    //회원 관리
    public List<UserDTO> inquiryUserListAll() //회원 목록 조회
    {
        SqlSession session = sqlSessionFactory.openSession();
        List<UserDTO> list = new ArrayList<>();

        try{
            list = session.selectList("mapper.UserMapper.selectAll");
        } finally {
            session.close();
        }

        return list;
    }

    public List<UserDTO> inquiryUser(String name, String phoneNum) //회원 검색
    {
        SqlSession session = sqlSessionFactory.openSession();
        List<UserDTO> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("phone_num", phoneNum);

        try{
            list = session.selectList("mapper.UserMapper.selectUser",map);
        } finally {
            session.close();
        }

        return list;
    }

    //회원 삭제
    public int deleteUser(int pk)
    {
        SqlSession session = sqlSessionFactory.openSession();
        int row = 0;
        Map<String, Object> map = new HashMap<>();
        map.put("pk", pk);

        try{
            session.delete("mapper.MedicineBookmarkMapper.deleteInfirmaryPk", map);
            session.delete("mapper.MedicineBookmarkMapper.deleteUser", map);
            session.delete("mapper.MedicineAlarmMapper.deleteUser", map);
            row = session.delete("mapper.UserMapper.deleteUser", map);
            session.commit();

        } catch(Exception e) {
            System.out.println(e);
            session.rollback();
        } finally {
            session.close();
        }
        return row;
    }

    //보건실 관리
    public List<InfirmaryDTO> inquiryInfirmaryListAll() //보건실 목록 조회
    {
        SqlSession session = sqlSessionFactory.openSession();
        List<InfirmaryDTO> list = new ArrayList<>();

        try{
            list = session.selectList("mapper.InfirmaryMapper.infirmaryAll");
            for(InfirmaryDTO infirmaryDTO : list){
                Map<String, Object> map = new HashMap<>();
                map.put("admin_pk", infirmaryDTO.getAdmin_pk());
                String admin_name = session.selectOne("mapper.InfirmaryMapper.selectAdminName", map);
                infirmaryDTO.setAdmin_name(admin_name);
            }
        } finally {
            session.close();
        }

        return list;
    }

    public List<InfirmaryDTO> inquiryNotApprovedInfirmaryAll() //승인되지않은 보건실 목록 조회
    {
        SqlSession session = sqlSessionFactory.openSession();
        List<InfirmaryDTO> list = new ArrayList<>();

        try{
            list = session.selectList("mapper.InfirmaryMapper.NotApprovedInfirmaryAll");
        } finally {
            session.close();
        }

        return list;
    }

    public List<InfirmaryDTO> inquiryInfirmary(String school) //보건실 검색
    {
        SqlSession session = sqlSessionFactory.openSession();
        List<InfirmaryDTO> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("school", school);
        try{
            list = session.selectList("mapper.InfirmaryMapper.selectInfirmary",map);
            for(InfirmaryDTO infirmaryDTO : list){
                Map<String, Object> map1 = new HashMap<>();
                map1.put("admin_pk", infirmaryDTO.getAdmin_pk());
                String admin_name = session.selectOne("mapper.InfirmaryMapper.selectAdminName", map1);
                infirmaryDTO.setAdmin_name(admin_name);
            }
        } finally {
            session.close();
        }

        return list;
    }

    public int acceptInfirmary(int infirmaryPk) //보건실 승인
    {
        SqlSession session = sqlSessionFactory.openSession();
        int row = 0;
        Map<String, Object> map = new HashMap<>();
        map.put("pk", infirmaryPk);

        try{
            row = session.update("mapper.InfirmaryMapper.acceptInfirmary", map);
            session.commit();
        } catch(Exception e) {
            session.rollback();
        } finally {
            session.close();
        }
        return row;
    }

    public int deleteInfirmary(int pk) //보건실 삭제
    {
        SqlSession session = sqlSessionFactory.openSession();
        int row = 0;
        Map<String, Object> map = new HashMap<>();
        map.put("pk", pk);

        try{
            row = session.delete("mapper.InfirmaryMapper.deleteInfirmary",map);
            session.commit();
        } catch(Exception e) {
            System.out.println(e);
            session.rollback();
        } finally {
            session.close();
        }
        return row;
    }


}
