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
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.commons.lang3.ArrayUtils;
import persistence.MyBatisConnectionFactory;
import persistence.dao.AdminDAO;
import persistence.dao.InfirmaryDAO;
import persistence.dao.ManagerDAO;
import persistence.dao.MedicineDAO;
import persistence.dto.AdminDTO;
import persistence.dto.InfirmaryDTO;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.io.ByteArrayInputStream;

public class ManagerApprovalInfirmary implements Initializable {

    @FXML
    private ListView<String> infirmaryListField;
    private ObservableList<String> infirmaryList;
    @FXML private Button certificateBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        infirmaryList = FXCollections.observableArrayList();
        infirmaryListField.setItems(infirmaryList);

        viewInfirmaryList();

    }

    public void viewInfirmaryList() {
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        AdminDAO adminDAO = new AdminDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        List<InfirmaryDTO> infirmaryDTOS = managerDAO.inquiryNotApprovedInfirmaryAll();

        for(int i = 0; i < infirmaryDTOS.size(); i++)
        {
            AdminDTO adminDTO = adminDAO.inquiryAdmin(infirmaryDTOS.get(i).getAdmin_pk());
            infirmaryDTOS.get(i).setAdmin_name(adminDTO.getName());
            infirmaryDTOS.get(i).setAdmin_phone_num(adminDTO.getPhone_num());
        }

        try {
            infirmaryList.clear();
            for (InfirmaryDTO infirmary : infirmaryDTOS) {

                String list = infirmary.getPk() + " | 학교: " + infirmary.getSchool() + " | 위치: " + infirmary.getLocation()
                        + "\n운영자 이름 : " + infirmary.getAdmin_name() + " | 운영자 전화번호 : " + infirmary.getAdmin_phone_num()
                        + "\n보건실 전화번호 : " + infirmary.getInfirmary_phone_num()
                        + " | 운영 시간 : " + infirmary.getOpen_time() + " ~ " + infirmary.getClose_time();
                infirmaryList.add(list);
            }
            infirmaryListField.refresh();
        } catch (Exception e) {
        }
    }

    public void acceptInfirmary() {
        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        String selectedInfirmary = infirmaryListField.getSelectionModel().getSelectedItem();
        String arr[] = selectedInfirmary.split(" ");
        int pk = Integer.parseInt(arr[0]);
        managerDAO.acceptInfirmary(pk);
        infirmaryListField.getSelectionModel().clearSelection();
        viewInfirmaryList();
    }

    public void deleteSelectedInfirmary() {
        try {
            ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());

            String selectedAdmin = infirmaryListField.getSelectionModel().getSelectedItem();
            String arr[] = selectedAdmin.split(" ");
            int pk = Integer.parseInt(arr[0]);
            managerDAO.deleteInfirmary(pk);
            infirmaryListField.getSelectionModel().clearSelection();
            viewInfirmaryList();
        } catch(Exception e) {
        }
    }

    public void showImage() throws IOException {
        String selectedItem = infirmaryListField.getSelectionModel().getSelectedItem();
        String arr[] = selectedItem.split(" ");
        String pk = arr[0];


        ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        // 데이터베이스에서 BLOB 타입 파일 가져오기
        int infirmaryPk = Integer.parseInt(pk);
        Byte[] fileData = managerDAO.inquiryInfirmaryFile(infirmaryPk);

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

                System.out.println("이거 나오니? :  " + byteArray);

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