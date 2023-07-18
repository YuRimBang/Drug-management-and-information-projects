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
import protocol.Header;
import storage.PkStorage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerViewAdminList implements Initializable {

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

        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_MANAGER,
                Header.CODE_MANAGER_VIEW_ADMIN_LIST,
                0
        );
        outputStream.write(header.getBytes());

        viewAdminList(inputStream);
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