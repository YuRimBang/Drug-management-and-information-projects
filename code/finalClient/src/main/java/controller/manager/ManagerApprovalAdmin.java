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
import protocol.Header;
import storage.PkStorage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerApprovalAdmin implements Initializable {

    @FXML
    private ListView<String> adminListField;
    private ObservableList<String> adminList;
    private PkStorage storage;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        adminList = FXCollections.observableArrayList();
        adminListField.setItems(adminList);
        try {
            viewAdminList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void viewAdminList() throws IOException {
        String ip = storage.getIp();
        System.out.println(ip);
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        adminList.clear();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_MANAGER,
                Header.CODE_MANAGER_VIEW_NOTAPPROVE_ADMIN,
                0
        );
        outputStream.write(header.getBytes());

        viewNotApproveAdminList(inputStream);
    }
    public void viewNotApproveAdminList(DataInputStream inputStream) throws IOException {
        Header header = Header.readHeader(inputStream);
        int size = inputStream.readInt();
        if(size > 0){
            for(int i=0; i<size; i++){
                AdminDTO admin = AdminDTO.readAdmin(inputStream);
                String adminInfo = admin.getPk() + " 아이디: " + admin.getId() + " 이름: " + admin.getName()
                        + " | 전화번호: " + admin.getPhone_num();
                adminList.add(adminInfo);
            }
        }
    }
    public void acceptAdmin() throws IOException {
        String ip = storage.getIp();
        Socket socket = new Socket(ip, 4000);
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        String selectedAdmin = adminListField.getSelectionModel().getSelectedItem();
        String arr[] = selectedAdmin.split(" ");
        int pk = Integer.parseInt(arr[0]);

        System.out.println("pk : " + pk);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);
        dos.writeInt(pk);
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_RESPONSE,
                Header.ACTOR_MANAGER,
                Header.CODE_MANAGER_AMDIN_APPROVE,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);

        adminListField.getSelectionModel().clearSelection();
        viewAdminList();
    }
    public void deleteSelectedAdmin() {
        try {
            String ip = storage.getIp();
            Socket socket = new Socket(ip, 4000);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            String selectedAdmin = adminListField.getSelectionModel().getSelectedItem();
            String arr[] = selectedAdmin.split(" ");
            int pk = Integer.parseInt(arr[0]);

            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buf);
            dos.writeInt(pk);
            System.out.println("삭제할 pk : " + pk);
            byte[] body = buf.toByteArray();
            Header header = new Header(
                    Header.TYPE_REQUEST,
                    Header.ACTOR_MANAGER,
                    Header.CODE_MANAGER_DELETE_ADMIN,
                    body.length
            );
            outputStream.write(header.getBytes());
            outputStream.write(body);

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
        String ip = storage.getIp();
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        String selectedItem = adminListField.getSelectionModel().getSelectedItem();
        String arr[] = selectedItem.split(" ");
        int pk = Integer.parseInt(arr[0]);

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);
        dos.writeInt(pk);
        System.out.println("이미지가져올 pk : " + pk);
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_MANAGER,
                Header.CODE_MANAGER_VIEW_NOTAPPROVE_ADMIN_IMAGE,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);
        // Alert 다이얼로그 생성
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        int size = inputStream.readInt();
        byte[] resBody = new byte[size];
        inputStream.readFully(resBody);
        if (size > 0) {
            try {
                byte[] byteArray = resBody;
                ByteArrayInputStream inputStream2 = new ByteArrayInputStream(byteArray);
                Image image = new Image(inputStream2);
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