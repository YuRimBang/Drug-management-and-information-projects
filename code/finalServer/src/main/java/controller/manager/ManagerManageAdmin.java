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
import persistence.dto.AdminDTO;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerManageAdmin implements Initializable {
    @FXML private TextField idField;
    @FXML private TextField phoneNumField;
    @FXML private ListView<String> adminListField;
    private ObservableList<String> adminList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void searchAdmin(ActionEvent event) {
        try {
            String id = idField.getText();
            String phoneNum = phoneNumField.getText();

            adminList = FXCollections.observableArrayList();
            adminListField.setItems(adminList);

            ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
            List<AdminDTO> adminListResult = managerDAO.inquiryAdmin(id, phoneNum);

            adminList.clear();

            for(AdminDTO admin : adminListResult) {
                String list = admin.getPk() + " 아이디: " + admin.getId() + " 이름: " + admin.getName()
                        + " | 전화번호: " + admin.getPhone_num();
                adminList.add(list);
            }
        }
        catch (Exception e){

        }
    }

    public void deleteSelectedAdmin() {
        try {
            ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());

            String selectedAdmin = adminListField.getSelectionModel().getSelectedItem();
            String arr[] = selectedAdmin.split(" ");
            int pk = Integer.parseInt(arr[0]);
            managerDAO.deleteAdmin(pk);

            adminList.clear();
            idField.clear();
            phoneNumField.clear();

            adminListField.getSelectionModel().clearSelection();
        } catch(Exception e) {
        }
    }

    public void goToViewAdminListScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/manager/managerViewAdminListUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
        }
    }

    public void goToApprovalAdminScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/manager/managerApprovalAdminUI.fxml"));

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