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
import persistence.dto.AdminDTO;
import persistence.dto.InfirmaryDTO;
import protocol.Header;
import storage.PkStorage;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerManageInfirmary implements Initializable {
    private PkStorage storage;
    @FXML private TextField nameField;
    @FXML private ListView<String> infirmaryListField;
    private ObservableList<String> infirmaryList;
    @FXML private Button deleteInfirmaryButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        deleteInfirmaryButton.setOnAction(event -> deleteSelectedInfirmary(event));
    }

    public void searchInfirmary(ActionEvent event) throws IOException {
        String ip = storage.getIp();

        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        try {
            String name = nameField.getText();
            infirmaryList = FXCollections.observableArrayList();
            infirmaryListField.setItems(infirmaryList);

            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buf);

            dos.writeUTF(name);
            byte[] body = buf.toByteArray();
            Header header = new Header(
                    Header.TYPE_REQUEST,
                    Header.ACTOR_MANAGER,
                    Header.CODE_MANAGER_SEARCH_INFIRMARY,
                    body.length
            );
            outputStream.write(header.getBytes());
            outputStream.write(body);
            System.out.println("보낸거 : " + name);

            viewInfirmaryList(inputStream);
        } catch (Exception e) {

        }
    }
    public void viewInfirmaryList(DataInputStream inputStream) throws IOException {
        Header header = Header.readHeader(inputStream);
        int size = inputStream.readInt();
        if(size > 0){
            for(int i=0; i<size; i++){
                InfirmaryDTO infirmary = InfirmaryDTO.readInfirmary(inputStream);
                String list = infirmary.getPk() + " | 학교: " + infirmary.getSchool() + " | 위치: " + infirmary.getLocation()
                        + "\n운영자 이름 : " + infirmary.getAdmin_name() + " | 운영자 전화번호 : " + infirmary.getInfirmary_phone_num()
                        + "\n보건실 전화번호 : " + infirmary.getInfirmary_phone_num()
                        + " | 운영 시간 : " + infirmary.getOpen_time() + " ~ " + infirmary.getClose_time();
                infirmaryList.add(list);
            }
        }
    }
    public void deleteInfirmary(int pk,DataOutputStream outputStream) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);
        dos.writeInt(pk);
        System.out.println("삭제할 pk : " + pk);
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_MANAGER,
                Header.CODE_MANAGER_DELETE_INFIRMARY,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);
    }
    public void deleteSelectedInfirmary(ActionEvent event) {
        System.out.println("보건실 삭제 들어감");
        try {
            String ip = storage.getIp();
            Socket socket = new Socket(ip, 4000);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            String selectedInfirmary = infirmaryListField.getSelectionModel().getSelectedItem();
            String arr[] = selectedInfirmary.split(" ");
            int pk = Integer.parseInt(arr[0]); //보건실 PK
            deleteInfirmary(pk,outputStream);
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