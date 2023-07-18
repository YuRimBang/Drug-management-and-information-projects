package persistence.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import persistence.dto.InfirmaryNoticeDTO;
import persistence.dto.UserDTO;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InfirmaryNoticeDAO {
    private final SqlSessionFactory sqlSessionFactory;

    public InfirmaryNoticeDAO(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public List<InfirmaryNoticeDTO> inqueryNotice(int infirmary_pk){ //이용자 - 공지사항 조회
        SqlSession session = sqlSessionFactory.openSession();
        List<InfirmaryNoticeDTO> noticeList = null;

        try {
            noticeList = session.selectList("mapper.InfirmaryNoticeMapper.selectInfirmaryNotice", infirmary_pk);
        }
        finally {
            session.close();
        }
        return noticeList;
    }

    public int insertNotice(InfirmaryNoticeDTO infirmaryNoticeDTO) {// 운영자 - 공지사항 등록
        SqlSession session = sqlSessionFactory.openSession();
        int row = 0;
        try {
            row = session.insert("mapper.InfirmaryNoticeMapper.insertInfirmaryNotice", infirmaryNoticeDTO);
            session.commit();
        }finally {
            session.close();
        }
        return row;
    }

    public int updateNotice(InfirmaryNoticeDTO infirmaryNoticeDTO) {// 운영자 - 공지사항 수정
        SqlSession session = sqlSessionFactory.openSession();
        int row = 0;
        try {
            row = session.insert("mapper.InfirmaryNoticeMapper.updateInfirmaryNotice", infirmaryNoticeDTO);
            session.commit();
        }finally {
            session.close();
        }
        return row;
    }

    public int deleteNotice(int pk) {// 운영자 - 공지사항 삭제
        SqlSession session = sqlSessionFactory.openSession();
        int row = 0;
        try {
            row = session.insert("mapper.InfirmaryNoticeMapper.deleteInfirmaryNotice", pk);
            session.commit();
        }finally {
            session.close();
        }
        return row;
    }

    public void deleteInfirmaryPK(int pk) {
        SqlSession session = sqlSessionFactory.openSession();
        Map<String, Integer> map = new HashMap<>();
        map.put("infirmary_pk", pk);

        try {
            session.delete("mapper.InfirmaryNoticeMapper.deleteInfirmaryPK", map);
            session.commit();
        } catch(Exception e) {
            System.out.println(e);
            session.rollback();
        }  finally {
            session.close();
        }
    }
}
