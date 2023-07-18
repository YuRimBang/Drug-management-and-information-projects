package controller.user;

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
import persistence.dao.InfirmaryNoticeDAO;
import persistence.dao.UserDAO;
import persistence.dto.InfirmaryNoticeDTO;
import storage.PkStorage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserViewInfirmaryNotice implements Initializable {
    private PkStorage storage;
    @FXML private ListView<String> noticeListField;
    private ObservableList<String> noticeList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();

        noticeList = FXCollections.observableArrayList();
        noticeListField.setItems(noticeList);

        viewInfirmaryNotice();
    }

    public void viewInfirmaryNotice() {
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        int infirmary_pk = userDAO.getInfirmary_pk(storage.getCurPk());
        InfirmaryNoticeDAO infirmaryNoticeDAO = new InfirmaryNoticeDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        List<InfirmaryNoticeDTO> noticeListResult = infirmaryNoticeDAO.inqueryNotice(infirmary_pk);

        for(InfirmaryNoticeDTO notice : noticeListResult) {
            String list = "제목 : " + notice.getTitle() + "\n내용 : " + notice.getContent();
            noticeList.add(list);
        }
        noticeListField.refresh();
    }

    public void goToPreviousScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/user/userSearchInfimaryMedicineUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }
}
