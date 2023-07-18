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
import persistence.dao.UserDAO;
import persistence.dto.AdminDTO;
import persistence.dto.UserDTO;
import protocol.Header;
import storage.PkStorage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerViewUserList implements Initializable {
    @FXML
    private ListView<String> userListField;
    private ObservableList<String> userList;
    private PkStorage storage;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        userList = FXCollections.observableArrayList();
        userListField.setItems(userList);

        try {
            viewUserList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void viewUserList() throws IOException { // 추가됨(5/23)
        String ip = storage.getIp();
        System.out.println(ip);
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_MANAGER,
                Header.CODE_MANAGER_VIEW_USER_LIST,
                0
        );
        outputStream.write(header.getBytes());

        viewUserList(inputStream);
    }
    public void viewUserList(DataInputStream inputStream) throws IOException {
        Header header = Header.readHeader(inputStream);
        int size = inputStream.readInt();
        if(size > 0){
            for(int i=0; i<size; i++){
                UserDTO user = UserDTO.readUser(inputStream);
                String userInfo = user.getPk() + " 아이디: " + user.getId() + " 이름: " + user.getName()
                        + " | 전화번호: " + user.getPhone_num();
                userList.add(userInfo);
            }
        }
    }
    public void goToPreviousScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/manager/managerManageUserUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }
}