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
import persistence.dto.AdminDTO;
import persistence.dto.UserDTO;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerViewAdminList implements Initializable {

    @FXML
    private ListView<String> adminListField;
    private ObservableList<String> adminList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        adminList = FXCollections.observableArrayList();
        adminListField.setItems(adminList);

        viewAdminList();
    }

    public void viewAdminList() {
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<AdminDTO> adminListResult = managerDAO.inquiryAdminListAll();
        try {
            for (AdminDTO admin : adminListResult) {
                String list = " 아이디: " + admin.getId() + " 이름: " + admin.getName()
                        + " | 전화번호: " + admin.getPhone_num();
                adminList.add(list);
            }
            adminListField.refresh();
        } catch (Exception e) {
        }
    }

    public void goToPreviousScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/manager/managerManageAdminUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }
}