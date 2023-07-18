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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import persistence.MyBatisConnectionFactory;
import persistence.dao.InfirmaryDAO;
import persistence.dao.InfirmaryMedicineDAO;
import persistence.dao.MedicineDAO;
import persistence.dto.InfirmaryDTO;
import persistence.dto.InfirmaryMedicineDTO;
import persistence.dto.RefineData;
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

public class AdminAddMedicine implements Initializable {
    private PkStorage storage;
    @FXML private TextField searchField;
    @FXML private Button searchBtn;
    @FXML private TextField stockField;
    @FXML private ListView<String> medicineListField;
    private ObservableList<String> medicineList;
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storage = PkStorage.getInstance();
        medicineList = FXCollections.observableArrayList();
        medicineListField.setItems(medicineList);

        searchBtn.setOnAction(event -> viewMedicineList(event));
    }

    public void addMedicine(ActionEvent event) throws IOException {
        String ip = storage.getIp();

        Socket socket = new Socket(ip, 4000);
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());

        try {
            int pk = storage.getCurPk();
            System.out.println(pk);
            String selectedMedicine = medicineListField.getSelectionModel().getSelectedItem();
            String arr[] = selectedMedicine.split("\\)");
            System.out.println(arr[0]);
            int medicine_pk = Integer.parseInt(arr[0]);

            int stock = Integer.parseInt(stockField.getText());
            System.out.println(stock);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buf);

            dos.writeInt(pk);
            dos.writeInt(medicine_pk);
            dos.writeInt(stock);
            byte[] body = buf.toByteArray();
            Header header = new Header(
                    Header.TYPE_REQUEST,
                    Header.ACTOR_ADMIN,
                    Header.CODE_ADMIN_ADD_MEDICINE,
                    body.length
            );
            outputStream.write(header.getBytes());
            outputStream.write(body);
            System.out.println("보냄~");

            getResult(inputStream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public void getResult(DataInputStream inputStream) throws IOException {
        Header header = Header.readHeader(inputStream);
        int result = inputStream.readInt();
        System.out.println(result);
        if(result == 1) {
            stockField.clear();
            alert.setTitle("약 등록");
            alert.setHeaderText("약 등록 성공");
            alert.setContentText("약 등록이 되었습니다.");
            alert.showAndWait();
        } else {
            alert.setTitle("약 등록");
            alert.setHeaderText("약 등록 실패");
            alert.setContentText("약 등록에 실패하였습니다.");
            alert.showAndWait();
        }
    }
    public void viewMedicineList(ActionEvent event) {
        try {
            String medicineName = searchField.getText();
            medicineList.clear();

            String ip = storage.getIp();
            System.out.println(ip);
            Socket socket = new Socket(ip, 4000);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buf);

            dos.writeUTF(medicineName);
            byte[] body = buf.toByteArray();
            Header header = new Header(
                    Header.TYPE_REQUEST,
                    Header.ACTOR_ADMIN,
                    Header.CODE_ADMIN_SEARCH_ALL_MEDICINE,
                    body.length
            );
            outputStream.write(header.getBytes());
            outputStream.write(body);

            showInfo(inputStream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public void showInfo(DataInputStream inputStream) throws IOException {
        Header header = Header.readHeader(inputStream);
        int size = inputStream.readInt();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                RefineData data = RefineData.readRefineData(inputStream);
                String list = data.getSeqNum() + ")\n품목명: |" + data.getItemName() + "|\n"
                        + "업체명: " + data.getEntpName() + "\n"
                        + "주성분: " + splitString(data.getMainIngr())
                        + "분류: " + data.getClassName() + "\n"
                        + "전문/일반: " + data.getEtcOtcName() + "\n"
                        + "효능효과: " + splitString(data.getEfficacy())
                        + "사용법: " + splitString(data.getUseMethod())
                        + "주의사항: " + splitString(data.getCaution())
                        + "상호작용: " + splitString(data.getIntrc()) + "\n"
                        + "부작용: " + splitString(data.getSideEffect()) + "\n"
                        + "--------------------------------------------------------------------------------------------";
                list = list.replace("null", "");
                medicineList.add(list);
            }
        }
    }
    public String splitString(String content) {
        StringBuilder result = new StringBuilder();
        int startIndex = 0;
        int endIndex = 40;

        while (startIndex < content.length()) {
            if (endIndex > content.length()) {
                endIndex = content.length();
            }

            String subString = content.substring(startIndex, endIndex);
            result.append(subString).append("\n");

            startIndex += 40;
            endIndex += 40;
        }

        return result.toString();
    }

    public void showImage() throws IOException {
        String selectedAdmin = medicineListField.getSelectionModel().getSelectedItem();
        String arr[] = selectedAdmin.split("\\|");
        String name = arr[1];

        String ip = storage.getIp();
        System.out.println(ip);
        Socket socket = new Socket(ip, 4000);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeUTF(name);
        byte[] body = buf.toByteArray();
        Header header = new Header(
                Header.TYPE_REQUEST,
                Header.ACTOR_USER,
                Header.CODE_USER_SHOW_IMAGE,
                body.length
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);

        MedicineDAO medicineDAO = new MedicineDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        String ImgUrl = null;
        ImgUrl = medicineDAO.inquiryMedicineImageByName(name);

        getImage(inputStream);

    }
    public void getImage(DataInputStream inputStream) throws IOException {
        String ImgUrl = inputStream.readUTF();
        // Alert 다이얼로그 생성
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        if(ImgUrl != null) {
            ImageView imageView = new ImageView();
            imageView.setPreserveRatio(true); // 원본 비율 유지 (Aspect Ratio)

            // 원본 이미지 크기 가져오기
            URL imageUrl = new URL(ImgUrl);
            Image originalImage = new Image(imageUrl.openStream());
            int originalWidth = (int) originalImage.getWidth();
            int originalHeight = (int) originalImage.getHeight();

            // 원하는 크기 계산
            int targetWidth = 500; // 원하는 폭
            int targetHeight = 500 * originalHeight / originalWidth; // Aspect Ratio에 따라 높이 계산

            // 이미지 생성
            Image resizedImage = new Image(imageUrl.openStream(), targetWidth, targetHeight, false, true); // 오류 발생 시, 예외 처리 필요

            // ImageView에 이미지 설정
            imageView.setImage(resizedImage);

            // 이미지 보기
            imageView.setImage(resizedImage);
            alert.setGraphic(imageView);
        } else {
            alert.setContentText("이미지가 존재하지 않습니다.");
        }

        // Alert 다이얼로그 표시
        alert.showAndWait();
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