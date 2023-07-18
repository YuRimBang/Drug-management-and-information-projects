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
import persistence.dto.InfirmaryDTO;
import persistence.dto.UserDTO;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerViewInfirmaryList implements Initializable {
    @FXML
    private ListView<String> infirmaryListField;
    private ObservableList<String> infirmaryList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        infirmaryList = FXCollections.observableArrayList();
        infirmaryListField.setItems(infirmaryList);

        viewInfirmaryList();
    }

    public void viewInfirmaryList() { // 추가됨(5/23)
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<InfirmaryDTO> infirmaryListResult = managerDAO.inquiryInfirmaryListAll();
        try {
            for (InfirmaryDTO infirmary : infirmaryListResult) {
                String list = "학교: " + infirmary.getSchool() + " | 위치: " + infirmary.getLocation()
                        + "\n운영자 이름 : " + infirmary.getAdmin_name() + " | 운영자 전화번호 : " + infirmary.getInfirmary_phone_num()
                        + "\n보건실 전화번호 : " + infirmary.getInfirmary_phone_num()
                        + " | 운영 시간 : " + infirmary.getOpen_time() + " ~ " + infirmary.getClose_time();
                infirmaryList.add(list);
            }
            infirmaryListField.refresh();
        } catch (Exception e) {
        }
    }

    public void goToPreviousScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/manager/managerManageInfimaryUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }
}