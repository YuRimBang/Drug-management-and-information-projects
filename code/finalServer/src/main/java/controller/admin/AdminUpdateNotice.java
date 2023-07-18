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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.InfirmaryDAO;
import persistence.dao.InfirmaryNoticeDAO;
import persistence.dao.ManagerDAO;
import persistence.dto.InfirmaryNoticeDTO;
import persistence.dto.UserDTO;
import storage.PkStorage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminUpdateNotice implements Initializable {
    private PkStorage storage;

    InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());
    InfirmaryNoticeDAO infirmaryNoticeDAO = new InfirmaryNoticeDAO(MyBatisConnectionFactory.getSqlSessionFactory());

    @FXML private ListView<Object> noticeListField;
    private ObservableList<Object> noticeList;
    @FXML private TextField titleField;
    @FXML private TextArea contentField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();

        noticeList = FXCollections.observableArrayList();
        noticeListField.setItems(noticeList);

        viewInfirmaryNotice();
    }

    public void viewInfirmaryNotice() {
        int infirmary_pk = infirmaryDAO.getInfirmaryPk(storage.getCurPk());
        InfirmaryNoticeDAO infirmaryNoticeDAO = new InfirmaryNoticeDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<InfirmaryNoticeDTO> noticeListResult = infirmaryNoticeDAO.inqueryNotice(infirmary_pk);

        for(InfirmaryNoticeDTO notice : noticeListResult) {
//            String list = notice.getPk() + " ) 제목 : " + notice.getTitle() + "\n내용 : " + notice.getContent();
            noticeList.add(notice);
        }
        noticeListField.refresh();
    }

    public void updateNotice() {
        int infirmary_pk = infirmaryDAO.getInfirmaryPk(storage.getCurPk());
        InfirmaryNoticeDAO infirmaryNoticeDAO = new InfirmaryNoticeDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryNoticeDTO infirmaryNoticeDTO = new InfirmaryNoticeDTO();

      /*  String selectedItem = noticeListField.getSelectionModel().getSelectedItem();
        String pk[] = selectedItem.split(" ");*/
        InfirmaryNoticeDTO infirmaryNoticeDTO1 = (InfirmaryNoticeDTO) noticeListField.getSelectionModel().getSelectedItem();

//        infirmaryNoticeDTO.setPk(Integer.parseInt(pk[0]));
        infirmaryNoticeDTO.setPk(infirmaryNoticeDTO1.getPk());
        infirmaryNoticeDTO.setTitle(titleField.getText());
        infirmaryNoticeDTO.setContent(contentField.getText());
        System.out.println(infirmaryNoticeDTO);
        infirmaryNoticeDAO.updateNotice(infirmaryNoticeDTO);

        List<InfirmaryNoticeDTO> updatedNoticeListResult = infirmaryNoticeDAO.inqueryNotice(infirmary_pk);
        noticeList.clear();
        for (InfirmaryNoticeDTO notice : updatedNoticeListResult) {
//            String list = "제목 : " + notice.getTitle() + "\n내용 : " + notice.getContent();
            noticeList.add(notice);
        }
        noticeListField.setItems(noticeList);

        noticeListField.getSelectionModel().clearSelection();
        titleField.clear();
        contentField.clear();
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