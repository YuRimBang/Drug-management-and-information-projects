package controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.InfirmaryDAO;
import persistence.dao.InfirmaryNoticeDAO;
import persistence.dto.InfirmaryNoticeDTO;
import storage.PkStorage;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminRegisterNotice implements Initializable {
    private PkStorage storage;

    InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());
    InfirmaryNoticeDAO infirmaryNoticeDAO = new InfirmaryNoticeDAO(MyBatisConnectionFactory.getSqlSessionFactory());

    @FXML private TextField titleField;
    @FXML private TextArea contentField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
    }

    public void registerNotice(ActionEvent event) {
        try {
            int infirmaryPk = infirmaryDAO.getInfirmaryPk(storage.getCurPk());
            InfirmaryNoticeDTO infirmaryNoticeDTO = new InfirmaryNoticeDTO();
            System.out.println("infirmaryPk: " + infirmaryPk);
            infirmaryNoticeDTO.setInfirmary_pk(infirmaryPk);
            infirmaryNoticeDTO.setTitle(titleField.getText());
            infirmaryNoticeDTO.setContent(contentField.getText());

            infirmaryNoticeDAO.insertNotice(infirmaryNoticeDTO);

            titleField.clear();
            contentField.clear();
        } catch (Exception e) {

        }
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