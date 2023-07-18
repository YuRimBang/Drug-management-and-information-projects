//import control.action.ActionController;
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//import medicineAPI.DetailInfo;
//import medicineAPI.MedicineShape;
//import medicineAPI.ThisMedicine;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.apache.log4j.BasicConfigurator;
//import org.json.simple.parser.ParseException;
//import org.xml.sax.SAXException;
//import persistence.MyBatisConnectionFactory;
//import persistence.dao.*;
//import persistence.dto.*;
//import protocol.Header;
//import view.ManagerView;
//
//import javax.xml.parsers.ParserConfigurationException;
//import java.io.*;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.List;
//
//import static javafx.application.Application.launch;
//
//public class Main {
//
//    private static SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
//
//    public static final int PORT = 4000;
//
//    public static void main(String []args) throws IOException
//    {
//        ServerSocket ss = new ServerSocket(PORT);
//        System.out.println("ServerSocket created. \nWaiting for connection ...\n\n");
//        BasicConfigurator.configure(); //뺴지말것
//        while(true)
//        {
//            Socket socket = ss.accept();
//
//            System.out.println("Client connected!\n");
//
//            DataInputStream is = new DataInputStream(socket.getInputStream());
//            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
//            System.out.println("실행");
//            Header header = Header.readHeader(is);
//            ActionController actionController = new ActionController();
//            System.out.println("받을게~");
//            actionController.handleRequest(header, is, os);
//        }
//    }
//}