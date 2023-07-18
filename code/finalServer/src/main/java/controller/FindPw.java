package controller;

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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.AdminDAO;
import persistence.dao.ManagerDAO;
import persistence.dao.UserDAO;
import persistence.dto.AdminDTO;
import persistence.dto.ManagerDTO;
import persistence.dto.UserDTO;

import java.net.URL;
import java.util.ResourceBundle;

public class FindPw implements Initializable {
    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneNumField;
    @FXML
    private TextField idField;
    @FXML
    private ListView<String> pwField;
    private ObservableList<String> pwList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pwList = FXCollections.observableArrayList();
        pwField.setItems(pwList);
    }

    public void findPw(ActionEvent event) {
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        AdminDAO adminDAO = new AdminDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        String name = nameField.getText();
        String phoneNum = phoneNumField.getText();
        String id = idField.getText();

        String userPw = null;
        String adminPw = null;
        String managerPw = null;

        userPw = userDAO.findPw(name, phoneNum, id);
        adminPw = adminDAO.findPw(name, phoneNum, id);
        managerPw = managerDAO.inquiryPw(name, phoneNum, id);


        try {
            if(pwList.size() != 1) {
                if(userPw != null) {
                    pwList.add(userPw);
                } else if (adminPw != null){
                    pwList.add(adminPw);
                }
                else if (managerPw != null) {
                    pwList.add(managerPw);
                } else {
                    pwList.add("존재하지 않는 비밀번호");
                }
            }

            pwField.refresh();

        } catch (Exception e) {
        }
    }


    public void goToPreviousScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/mainUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }
}
