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
import storage.PkStorage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminDeleteNotice implements Initializable {
    private PkStorage storage;
    @FXML
    private ListView<Object> noticeListField;
    private ObservableList<Object> noticeList;
    InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());
    InfirmaryNoticeDAO infirmaryNoticeDAO = new InfirmaryNoticeDAO(MyBatisConnectionFactory.getSqlSessionFactory());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        noticeList = FXCollections.observableArrayList();
        noticeListField.setItems(noticeList);

        viewInfirmaryNotice();
    }

    public void viewInfirmaryNotice() {
        int infirmary_pk = infirmaryDAO.getInfirmaryPk(storage.getCurPk());
        System.out.println(storage.getCurPk());

        List<InfirmaryNoticeDTO> noticeListResult = infirmaryNoticeDAO.inqueryNotice(infirmary_pk);

        for(InfirmaryNoticeDTO notice : noticeListResult) {
//            String list = notice.getPk() + " ) 제목 : " + notice.getTitle() + "\n내용 : " + notice.getContent();
            noticeList.add(notice);
        }
        noticeListField.refresh();
    }

    public void deleteNotice() {
        int infirmary_pk = infirmaryDAO.getInfirmaryPk(storage.getCurPk());
        InfirmaryNoticeDAO infirmaryNoticeDAO = new InfirmaryNoticeDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        InfirmaryNoticeDTO infirmaryNoticeDTO = (InfirmaryNoticeDTO) noticeListField.getSelectionModel().getSelectedItem();

        infirmaryNoticeDAO.deleteNotice(infirmaryNoticeDTO.getPk());

        List<InfirmaryNoticeDTO> updatedNoticeListResult = infirmaryNoticeDAO.inqueryNotice(infirmary_pk);
        noticeList.clear();
        for (InfirmaryNoticeDTO notice : updatedNoticeListResult) {
//            String list = notice.getPk() + " ) 제목 : " + notice.getTitle() + "\n내용 : " + notice.getContent();
            noticeList.add(notice);
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