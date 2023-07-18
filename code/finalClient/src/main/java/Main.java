import control.action.ActionController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import medicineAPI.DetailInfo;
import medicineAPI.MedicineShape;
import medicineAPI.ThisMedicine;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.BasicConfigurator;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;
import persistence.MyBatisConnectionFactory;
import persistence.dao.*;
import persistence.dto.*;
import protocol.Header;
import storage.PkStorage;
import view.ManagerView;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static javafx.application.Application.launch;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/JavaFX/mainUI.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String args[]) throws IOException, ParseException, ParserConfigurationException, SAXException {
        String ip = "172.30.125.220";
        PkStorage client = new PkStorage();
        client.setIp(ip);
        Socket socket = new Socket(ip, 4000);
        DataInputStream is = new DataInputStream(socket.getInputStream());
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        boolean isContinue = true;
        System.out.println("connected!");
        while(isContinue) {

            launch(args);
//            Header header = Header.readHeader(is);
//            ActionController controller = new ActionController();
//            controller.handleRequest(header, is, os);
            System.out.println("끝");
            isContinue = false;
        }
        BasicConfigurator.configure(); //뺴지말것
    }
}