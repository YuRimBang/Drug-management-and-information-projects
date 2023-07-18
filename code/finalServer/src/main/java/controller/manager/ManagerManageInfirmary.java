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
import persistence.dao.*;
import persistence.dto.InfirmaryDTO;

import javax.swing.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerManageInfirmary implements Initializable {

    @FXML private TextField nameField;
    @FXML private ListView<String> infirmaryListField;
    private ObservableList<String> infirmaryList;
    @FXML private Button deleteInfirmaryButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deleteInfirmaryButton.setOnAction(event -> deleteSelectedInfirmary(event));
    }

    public void searchInfirmary(ActionEvent event) {

        try {
            String name = nameField.getText();
            infirmaryList = FXCollections.observableArrayList();
            infirmaryListField.setItems(infirmaryList);

            ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
            List<InfirmaryDTO> infirmaryListResult = managerDAO.inquiryInfirmary(name);

            for(InfirmaryDTO infirmary : infirmaryListResult) {
                String list = infirmary.getPk() + " | 학교: " + infirmary.getSchool() + " | 위치: " + infirmary.getLocation()
                        + "\n운영자 이름 : " + infirmary.getAdmin_name() + " | 운영자 전화번호 : " + infirmary.getInfirmary_phone_num()
                        + "\n보건실 전화번호 : " + infirmary.getInfirmary_phone_num()
                        + " | 운영 시간 : " + infirmary.getOpen_time() + " ~ " + infirmary.getClose_time();
                infirmaryList.add(list);
            }
        } catch (Exception e) {

        }
    }

    public void deleteSelectedInfirmary(ActionEvent event) {
        System.out.println("보건실 삭제 들어감");
        try {
            System.out.println("try");
            ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
            InfirmaryMedicineDAO infirmaryMedicineDAO = new InfirmaryMedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
            UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
            MedicineBookmarkDAO medicineBookmarkDAO = new MedicineBookmarkDAO(MyBatisConnectionFactory.getSqlSessionFactory());
            MedicineAlarmDAO medicineAlarmDAO = new MedicineAlarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());
            InfirmaryNoticeDAO infirmaryNoticeDAO = new InfirmaryNoticeDAO(MyBatisConnectionFactory.getSqlSessionFactory());
            InfirmaryAlarmDAO infirmaryAlarmDAO = new InfirmaryAlarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());

            InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());

            String selectedInfirmary = infirmaryListField.getSelectionModel().getSelectedItem();
            String arr[] = selectedInfirmary.split(" ");
            int pk = Integer.parseInt(arr[0]); //보건실 PK
            int adminPK = infirmaryDAO.getAdminPk(pk);
            System.out.println(pk + " " + adminPK);


            userDAO.changeInfirmaryPK(pk);
            medicineBookmarkDAO.changeInfirmaryPK(pk);
            medicineAlarmDAO.changeInfirmaryPK(pk);
            infirmaryNoticeDAO.deleteInfirmaryPK(pk);
            infirmaryMedicineDAO.deleteInfirmaryMedicine(pk);
            infirmaryAlarmDAO.deleteInfirmaryPK(pk);
            int result = managerDAO.deleteInfirmary(pk);

            System.out.println("result: " + result);

            infirmaryList.clear();
            nameField.clear();

            infirmaryListField.getSelectionModel().clearSelection();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }



    public void goToViewInfimaryListScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/manager/managerViewInfimaryListUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }

    public void goToApprovalInfimaryScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/manager/managerApprovalInfimaryUI.fxml"));

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
        } catch (Exception e)
        {
        }
    }
}