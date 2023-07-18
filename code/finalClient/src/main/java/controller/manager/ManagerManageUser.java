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
import persistence.dto.UserDTO;
import protocol.Header;
import storage.PkStorage;

import javax.swing.plaf.IconUIResource;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerManageUser implements Initializable {
    @FXML private TextField idField;
    @FXML private TextField phoneNumField;
    @FXML private ListView<String> userListField;
    private ObservableList<String> userList;
    @FXML private Button deleteUserButton;
    private PkStorage storage;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        deleteUserButton.setOnAction(event -> deleteSelectedUser(event));
    }

    public void searchUser(ActionEvent event) throws IOException {
        String ip = storage.getIp();

        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        try {
            String id = idField.getText();
            String phoneNum = phoneNumField.getText();

            userList = FXCollections.observableArrayList();
            userListField.setItems(userList);

            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buf);

            dos.writeUTF(id);
            dos.writeUTF(phoneNum);
            byte[] body = buf.toByteArray();
            Header header = new Header(
                    Header.TYPE_REQUEST,
                    Header.ACTOR_MANAGER,
                    Header.CODE_MANAGER_SEARCH_USER,
                    body.length
            );
            outputStream.write(header.getBytes());
            outputStream.write(body);
            System.out.println("보낸거 : " + id+", "+phoneNum);

            userList.clear();
            viewUserList(inputStream);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
    public void viewUserList(DataInputStream inputStream) throws IOException {
        Header header = Header.readHeader(inputStream);
        int size = inputStream.readInt();
        if(size > 0){
            for(int i=0; i<size; i++){
                UserDTO user = UserDTO.readUser(inputStream);
                System.out.println(user);
                String list = user.getPk() + " 아이디: " + user.getId() + " 이름: " + user.getName()
                        + " | 전화번호: " + user.getPhone_num()
                        + " | 학교: " + user.getSchool();
                userList.add(list);
                System.out.println(list);
            }
        }
        System.out.println("이거함");
    }
    public void deleteSelectedUser(ActionEvent event) {
        try {
            String ip = storage.getIp();
            Socket socket = new Socket(ip, 4000);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            String selectedUser = userListField.getSelectionModel().getSelectedItem();
            String arr[] = selectedUser.split(" ");
//            ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());
            System.out.println(arr[0]);
            int pk = Integer.parseInt(arr[0]);
//            int result = managerDAO.deleteUser(pk);
            deleteUser(pk, outputStream);
            userList.clear();
            idField.clear();
            phoneNumField.clear();
            System.out.println("삭제함 : " + pk);
        } catch(Exception e) {

        }
    }
    public void deleteUser(int pk, DataOutputStream outputStream) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);
        dos.writeInt(pk);
        System.out.println("삭제할 pk : " + pk);
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_MANAGER,
                Header.CODE_MANAGER_DELETE_USER,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);
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