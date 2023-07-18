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
import persistence.dao.InfirmaryDAO;
import persistence.dao.ManagerDAO;
import persistence.dao.MedicineDAO;
import persistence.dto.AdminDTO;
import persistence.dto.InfirmaryDTO;
import protocol.Header;
import storage.PkStorage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerApprovalInfirmary implements Initializable {

    @FXML
    private ListView<String> infirmaryListField;
    private ObservableList<String> infirmaryList;
    @FXML private Button certificateBtn;
    private PkStorage storage;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        infirmaryList = FXCollections.observableArrayList();
        infirmaryListField.setItems(infirmaryList);
        storage = PkStorage.getInstance();
        storage.setIp(Header.ip);
        try {
            viewInfirmaryList();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void viewInfirmaryList() throws IOException {
        String ip = storage.getIp();
        System.out.println(ip);
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        infirmaryList.clear();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_MANAGER,
                Header.CODE_MANAGER_VIEW_NOTAPPROVE_INFIRMARY_LIST,
                0
        );
        outputStream.write(header.getBytes());

        viewNotApproveInfirmaryList(inputStream);
    }

    public void viewNotApproveInfirmaryList(DataInputStream inputStream) throws IOException {
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
            infirmaryListField.refresh();
        }
    }
    public void acceptInfirmary() throws IOException {
        String ip = storage.getIp();
        Socket socket = new Socket(ip, 4000);
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        String selectedInfirmary = infirmaryListField.getSelectionModel().getSelectedItem();
        String arr[] = selectedInfirmary.split(" ");
        int pk = Integer.parseInt(arr[0]);

        System.out.println("pk : " + pk);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);
        dos.writeInt(pk);
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_RESPONSE,
                Header.ACTOR_MANAGER,
                Header.CODE_MANAGER_INFIRMARY_APPROVE,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);

        infirmaryListField.getSelectionModel().clearSelection();
        viewInfirmaryList();
    }

    public void deleteSelectedInfirmary() {
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
            infirmaryListField.getSelectionModel().clearSelection();
        }
        catch(Exception e)
        {
            System.out.println(e);
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
    public void showImage() throws IOException {
        String ip = storage.getIp();
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        String selectedItem = infirmaryListField.getSelectionModel().getSelectedItem();
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
                Header.CODE_MANAGER_VIEW_NOTAPPROVE_INFIRMARY_IMAGE,
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