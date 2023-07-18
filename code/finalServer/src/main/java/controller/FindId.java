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
import javafx.scene.control.*;
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

public class FindId implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneNumField;
    @FXML
    private ListView<String> idField;
    private ObservableList<String> idList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idList = FXCollections.observableArrayList();
        idField.setItems(idList);
    }

    public void findId(ActionEvent event) {
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        AdminDAO adminDAO = new AdminDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        String name = nameField.getText();
        String phoneNum = phoneNumField.getText();

        String userId = null;
        String adminId = null;
        String managerId = null;

        userId = userDAO.findId(name, phoneNum);
        adminId = adminDAO.findId(name, phoneNum);
        managerId = managerDAO.inquiryId(name, phoneNum);


        try {
            if(idList.size() != 1) {
                if(userId != null) {
                    idList.add(userId);
                } else if (adminId != null){
                    idList.add(adminId);
                }
                else if (managerId != null) {
                    idList.add(managerId);
                } else {
                    idList.add("존재하지 않는 아이디");
                }
            }

            idField.refresh();

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
