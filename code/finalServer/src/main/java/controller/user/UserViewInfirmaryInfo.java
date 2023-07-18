package controller.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.InfirmaryDAO;
import persistence.dao.UserDAO;
import persistence.dto.InfirmaryDTO;
import storage.PkStorage;

import java.net.URL;
import java.util.ResourceBundle;

public class UserViewInfirmaryInfo implements Initializable {
    private PkStorage storage;

    @FXML private Text schoolText;
    @FXML private Text locationText;
    @FXML private Text infirmaryPhoneNum;
    @FXML private Text operatingTimeText;
    @FXML private Text nameText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        String school = userDAO.getSchool(storage.getCurPk());

        InfirmaryDAO infirmaryDAO = new InfirmaryDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        InfirmaryDTO infirmaryDTO = new InfirmaryDTO();
        InfirmaryDTO resultInfirmaryDTO = new InfirmaryDTO();

        infirmaryDTO.setSchool(school);
        resultInfirmaryDTO = infirmaryDAO.inquiryInfirmary(infirmaryDTO);

        schoolText.setText(resultInfirmaryDTO.getSchool());
        locationText.setText(resultInfirmaryDTO.getLocation());
        infirmaryPhoneNum.setText(resultInfirmaryDTO.getInfirmary_phone_num());
        operatingTimeText.setText(String.valueOf(resultInfirmaryDTO.getOpen_time()) + " ~ "
                + String.valueOf(resultInfirmaryDTO.getClose_time()));
        nameText.setText(resultInfirmaryDTO.getAdmin_name());
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
