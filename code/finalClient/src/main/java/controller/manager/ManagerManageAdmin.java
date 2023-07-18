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
import protocol.Header;
import storage.PkStorage;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerManageAdmin implements Initializable {
    private PkStorage storage;
    @FXML private TextField idField;
    @FXML private TextField phoneNumField;
    @FXML private ListView<String> adminListField;
    private ObservableList<String> adminList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
    }

    public void searchAdmin(ActionEvent event) throws IOException {
        String ip = storage.getIp();

        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        try {
            String id = idField.getText();
            String phoneNum = phoneNumField.getText();
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buf);

            dos.writeUTF(id);
            dos.writeUTF(phoneNum);
            byte[] body = buf.toByteArray();
            Header header = new Header(
                    Header.TYPE_REQUEST,
                    Header.ACTOR_MANAGER,
                    Header.CODE_MANAGER_SEARCH_ADMIN,
                    body.length
            );
            outputStream.write(header.getBytes());
            outputStream.write(body);
            System.out.println("보낸거 : " + id+", "+phoneNum);
            adminList = FXCollections.observableArrayList();
            adminListField.setItems(adminList);
            adminList.clear();

            viewAdminList(inputStream);
        }
        catch (Exception e){

        }
    }
    public void viewAdminList(DataInputStream inputStream) throws IOException {
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
    public void deleteAdmin(int pk,DataOutputStream outputStream) throws IOException {
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
    }
    public void deleteSelectedAdmin() throws IOException {
        String ip = storage.getIp();
        Socket socket = new Socket(ip, 4000);
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        String selectedAdmin = adminListField.getSelectionModel().getSelectedItem();
        String arr[] = selectedAdmin.split(" ");
        int pk = Integer.parseInt(arr[0]);
        deleteAdmin(pk,outputStream);
        adminList.clear();
        idField.clear();
        phoneNumField.clear();

        adminListField.getSelectionModel().clearSelection();
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