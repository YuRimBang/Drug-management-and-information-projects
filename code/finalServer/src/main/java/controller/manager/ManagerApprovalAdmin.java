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
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.commons.lang3.ArrayUtils;
import persistence.MyBatisConnectionFactory;
import persistence.dao.AdminDAO;
import persistence.dao.ManagerDAO;
import persistence.dto.AdminDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerApprovalAdmin implements Initializable {

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
        List<AdminDTO> adminListResult = managerDAO.inquiryNotApprovedAdminListAll();
        try {
            adminList.clear();
            for (AdminDTO admin : adminListResult) {
                String list = admin.getPk() + " 아이디: " + admin.getId() + " 이름: " + admin.getName()
                        + " | 전화번호: " + admin.getPhone_num();
                adminList.add(list);
            }
            adminListField.refresh();
        } catch (Exception e) {
        }
    }

    public void acceptAdmin() {
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        String selectedAdmin = adminListField.getSelectionModel().getSelectedItem();
        String arr[] = selectedAdmin.split(" ");
        int pk = Integer.parseInt(arr[0]);
        System.out.println("pk : " + pk);
        managerDAO.acceptAdmin(pk);

        adminListField.getSelectionModel().clearSelection();
        viewAdminList();
    }

    public void deleteSelectedAdmin() {
        try {
            ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());

            String selectedAdmin = adminListField.getSelectionModel().getSelectedItem();
            String arr[] = selectedAdmin.split(" ");
            int pk = Integer.parseInt(arr[0]);
            managerDAO.deleteAdmin(pk);
            adminListField.getSelectionModel().clearSelection();
            viewAdminList();
        } catch(Exception e) {
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

    public void showImage() throws IOException {
        String selectedItem = adminListField.getSelectionModel().getSelectedItem();
        String arr[] = selectedItem.split(" ");
        String pk = arr[0];

        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        // 데이터베이스에서 BLOB 타입 파일 가져오기
        int adminPk = Integer.parseInt(pk);
        Byte[] fileData = managerDAO.inquiryAdminFile(adminPk);

        // Alert 다이얼로그 생성
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        if (fileData != null) {
            try {
                byte[] byteArray = ArrayUtils.toPrimitive(fileData);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
                Image image = new Image(inputStream);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(500);
                imageView.setFitHeight(500);

                // 이미지 보기
                alert.setGraphic(imageView);
            } catch (Exception e) {
                e.printStackTrace();
                alert.setContentText("이미지 로딩 중 오류가 발생했습니다.");
            }
        } else {
            alert.setContentText("이미지가 존재하지 않습니다.");
        }

        // Alert 다이얼로그 표시
        alert.showAndWait();
    }
}