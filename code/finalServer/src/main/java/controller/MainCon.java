package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.ibatis.javassist.NotFoundException;
import persistence.MyBatisConnectionFactory;
import persistence.dao.AdminDAO;
import persistence.dao.ManagerDAO;
import persistence.dao.UserDAO;
import persistence.dto.AdminDTO;
import persistence.dto.ManagerDTO;
import persistence.dto.UserDTO;
import storage.PkStorage;

public class MainCon implements Initializable {

    @FXML
    private TextField idField;
    @FXML
    private TextField pwField;
    private PkStorage storage;

    Alert alert = new Alert(Alert.AlertType.INFORMATION);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
    }

    public void login(ActionEvent event) {
        try {
            String id = idField.getText();
            String pw = pwField.getText();
            FXMLLoader loader = new FXMLLoader();

            UserDAO userDAO = new UserDAO(MyBatisConnectionFactory.getSqlSessionFactory());
            AdminDAO adminDAO = new AdminDAO(MyBatisConnectionFactory.getSqlSessionFactory());
            ManagerDAO managerDAO = new ManagerDAO(MyBatisConnectionFactory.getSqlSessionFactory());

            int pk = 0;
            String status = null;
            UserDTO userDTO = userDAO.login(id, pw);
            AdminDTO adminDTO = adminDAO.login(id, pw);
            ManagerDTO managerDTO = managerDAO.login(id, pw);

            try {
                if (userDTO != null) {
                    pk = userDTO.getPk();
                    status = userDTO.getStatus();
                } else if (adminDTO != null) {
                    pk = adminDTO.getPk();
                    status = adminDTO.getStatus();
                } else if (managerDTO != null) {
                    pk = managerDTO.getPk();
                    status = managerDTO.getStatus();
                } else {
                    alert.setTitle("로그인");
                    alert.setHeaderText("로그인 실패");
                    alert.setContentText("다시 입력하세요");

                    alert.showAndWait();
                    throw new Exception("No matching user found");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }

            if(pk != 0){ // 로그인 성공
                storage.setCurPk(pk); // 현재 사용자의 pk 설정 // 여기서 pk가 userDTO.getPk()가 됨.
            }
            else{
                System.out.println("사용자를 찾을 수 없습니다.");
            }

            try {
                if (status.equals("user")) {
                    loader = new FXMLLoader(getClass().getResource("/JavaFX/user/userMainUI.fxml"));
                } else if (status.equals("admin")) {
                    loader = new FXMLLoader(getClass().getResource("/JavaFX/admin/adminMainUI.fxml"));
                } else if (status.equals("manager")) {
                    loader = new FXMLLoader(getClass().getResource("/JavaFX/manager/managerMainUI.fxml"));
                } else {
                    throw new Exception("Invalid user");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }




            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }

    public void goToSignUpScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/choiceSignUpUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            System.out.println("test"+stage);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToFindIdScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/findIdUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }

    public void goToFindPwScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/findPwUI.fxml"));

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/mainUI.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }
}


