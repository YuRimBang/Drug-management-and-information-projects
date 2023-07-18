package service;

import persistence.MyBatisConnectionFactory;
import persistence.dao.ManagerDAO;
import persistence.dto.ManagerDTO;

import java.util.ArrayList;
import java.util.List;

public class ManagerService {
    ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());

    public List<ManagerDTO> managerLogin(String id, String pw)
    {
        List<ManagerDTO> list = new ArrayList<>();
        //list = managerDAO.login(id, pw);

        return list;
    }
}
