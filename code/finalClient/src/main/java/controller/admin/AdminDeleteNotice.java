package controller.admin;

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
import persistence.dao.InfirmaryDAO;
import persistence.dao.InfirmaryNoticeDAO;
import persistence.dto.InfirmaryNoticeDTO;
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

public class AdminDeleteNotice implements Initializable {
    private PkStorage storage;
    @FXML
    private ListView<Object> noticeListField;
    private ObservableList<Object> noticeList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        noticeList = FXCollections.observableArrayList();
        noticeListField.setItems(noticeList);

        try {
            viewInfirmaryNotice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void viewInfirmaryNotice() throws IOException {
        String ip = storage.getIp();

        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeInt(storage.getCurPk());
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_ADMIN,
                Header.CODE_ADMIN_VIEW_INFIRMARY_NOTICE,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);


        int size = inputStream.readInt();
        for(int i = 0; i < size; i++)
        {
            InfirmaryNoticeDTO noticeListResult = InfirmaryNoticeDTO.readInfirmaryNotice(inputStream);
            noticeList.add(noticeListResult);
        }
        noticeListField.refresh();
    }

    public void deleteNotice() throws IOException {
        String ip = storage.getIp();
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);
        InfirmaryNoticeDTO infirmaryNoticeDTO = (InfirmaryNoticeDTO) noticeListField.getSelectionModel().getSelectedItem();
        dos.writeInt(storage.getCurPk());
        dos.write(infirmaryNoticeDTO.getBytes());
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_ADMIN,
                Header.CODE_ADMIN_DELETE_INFIRMARY_NOTICE,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);

        noticeList.clear();

        int size = inputStream.readInt();
        for(int i = 0; i < size; i++)
        {
            InfirmaryNoticeDTO updatedNoticeListResult = InfirmaryNoticeDTO.readInfirmaryNotice(inputStream);
            noticeList.add(updatedNoticeListResult);
        }
        noticeListField.setItems(noticeList);

        noticeListField.getSelectionModel().clearSelection();
    }

    public void goToPreviousScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/admin/adminMainUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }
}