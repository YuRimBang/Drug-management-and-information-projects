package controller.manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.ManagerDAO;
import persistence.dao.UserDAO;
import persistence.dto.AdminDTO;
import persistence.dto.UserDTO;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerViewUserList implements Initializable {
    @FXML
    private ListView<String> userListField;
    private ObservableList<String> userList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userList = FXCollections.observableArrayList();
        userListField.setItems(userList);

        viewUserList();
    }

    public void viewUserList() { // 추가됨(5/23)
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<UserDTO> userListResult = managerDAO.inquiryUserListAll();
        try {
            for (UserDTO user : userListResult) {
                String list = "아이디: " + user.getId() + " | 이름: " + user.getName() + " | 전화번호: " + user.getPhone_num()
                        + " | 학교: " + user.getSchool();
                userList.add(list);
            }
            userListField.refresh();
        } catch (Exception e) {
        }
    }

    public void goToPreviousScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/manager/managerManageUserUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }
}