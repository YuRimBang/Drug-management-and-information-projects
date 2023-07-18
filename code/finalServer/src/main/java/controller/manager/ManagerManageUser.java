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
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.ManagerDAO;
import persistence.dto.UserDTO;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerManageUser implements Initializable {
    @FXML private TextField idField;
    @FXML private TextField phoneNumField;
    @FXML private ListView<String> userListField;
    private ObservableList<String> userList;
    @FXML private Button deleteUserButton;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deleteUserButton.setOnAction(event -> deleteSelectedUser(event));
    }

    public void searchUser(ActionEvent event) {
        try {
            String id = idField.getText();
            String phoneNum = phoneNumField.getText();

            userList = FXCollections.observableArrayList();
            userListField.setItems(userList);

            ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
            List<UserDTO> userListResult = managerDAO.inquiryUser(id, phoneNum);

            userList.clear();

            for(UserDTO user : userListResult) {
                String list = user.getPk() + " 아이디: " + user.getId() + " 이름: " + user.getName()
                        + " | 전화번호: " + user.getPhone_num()
                        + " | 학교: " + user.getSchool();
                userList.add(list);
            }
        }
        catch (Exception e){

        }
    }

    public void deleteSelectedUser(ActionEvent event) {
        try {
            String selectedUser = userListField.getSelectionModel().getSelectedItem();
            String arr[] = selectedUser.split(" ");
            ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
            System.out.println(arr[0]);
            int pk = Integer.parseInt(arr[0]);
            int result = managerDAO.deleteUser(pk);
            userList.clear();
            idField.clear();
            phoneNumField.clear();
        } catch(Exception e) {

        }

    }

    public void goToViewUserListScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/manager/managerViewUserListUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }

    public void goToPreviousScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/manager/managerMainUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }
}