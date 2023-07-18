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
import persistence.dto.InfirmaryDTO;
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

public class ManagerViewInfirmaryList implements Initializable {
    @FXML
    private ListView<String> infirmaryListField;
    private ObservableList<String> infirmaryList;
    private PkStorage storage;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        infirmaryList = FXCollections.observableArrayList();
        infirmaryListField.setItems(infirmaryList);
        storage = PkStorage.getInstance();
        try {
            viewInfirmaryList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void viewInfirmaryList() throws IOException { // 추가됨(5/23)
        String ip = Header.ip;
        System.out.println(ip);
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_MANAGER,
                Header.CODE_MANAGER_VIEW_INFIRMARY_LIST,
                0
        );
        outputStream.write(header.getBytes());

        infirmaryListField.refresh();
        viewInfirmaryList(inputStream);
    }
    public void viewInfirmaryList(DataInputStream inputStream) throws IOException {
        int size = inputStream.readInt();
        System.out.println("사이즈: " +size);
        if(size > 0){
            for(int i=0; i<size; i++){
                InfirmaryDTO infirmary = InfirmaryDTO.readInfirmary(inputStream);
                System.out.println(infirmary.toString());
                String list = "학교: " + infirmary.getSchool() + " | 위치: " + infirmary.getLocation()
                        + "\n운영자 이름 : " + infirmary.getAdmin_name() + " | 운영자 전화번호 : " + infirmary.getInfirmary_phone_num()
                        + "\n보건실 전화번호 : " + infirmary.getInfirmary_phone_num()
                        + " | 운영 시간 : " + infirmary.getOpen_time() + " ~ " + infirmary.getClose_time();
                infirmaryList.add(list);
            }
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