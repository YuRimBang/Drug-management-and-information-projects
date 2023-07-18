package control.type;

import com.sun.org.apache.xalan.internal.xsltc.trax.XSLTCSource;
import control.entityControl.AdminController;
import control.entityControl.ManagerController;
import control.entityControl.UserController;
import persistence.MyBatisConnectionFactory;
import persistence.dao.ManagerDAO;
import protocol.Header;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RequestController
{
    UserController userController = new UserController();
    ManagerController managerController = new ManagerController();
    AdminController adminController = new AdminController();

    public void handleRead(Header header,DataInputStream inputStream, DataOutputStream outputStream) throws IOException
    {
        switch (header.actor)
        {
            case Header.ACTOR_MANAGER:
                switch (header.code)
                {
                    case Header.CODE_MANAGER_VIEW_ADMIN_LIST://운영자 목록 조회
                        managerController.viewAdminList(outputStream);
                        break;
                    case Header.CODE_MANAGER_SEARCH_ADMIN://운영자 검색
                        managerController.searchAdmin(inputStream,outputStream);
                        break;
                    case Header.CODE_MANAGER_DELETE_ADMIN://운영자 삭제
                        managerController.deleteAdmin(inputStream);
                        break;
                    case Header.CODE_MANAGER_VIEW_USER_LIST://회원 목록 조회
                        managerController.viewUserList(outputStream);
                        break;
                    case Header.CODE_MANAGER_SEARCH_USER:  //회원 검색
                        managerController.searchUser(inputStream,outputStream);
                        break;
                    case Header.CODE_MANAGER_DELETE_USER://회원 삭제
                        managerController.deleteUser(inputStream);
                        break;
                    case Header.CODE_MANAGER_VIEW_INFIRMARY_LIST://보건실 목록 조회
                        managerController.viewInfirmaryList(outputStream);
                        break;
                    case Header.CODE_MANAGER_SEARCH_INFIRMARY://보건실 검색
                        managerController.searchInfirmary(inputStream, outputStream);
                        break;
                    case Header.CODE_MANAGER_DELETE_INFIRMARY://보건실 삭제
                        managerController.deleteInfirmary(inputStream);
                        break;
                    case Header.CODE_MANAGER_VIEW_NOTAPPROVE_ADMIN://승인되지않은 운영자 조회
                        managerController.viewNotApproveAdminList(outputStream);
                        break;
                    case Header.CODE_MANAGER_VIEW_NOTAPPROVE_ADMIN_IMAGE://승인되지않은 운영자 재직증명서 조회
                        managerController.viewAdminImage(inputStream,outputStream);
                        break;
                    case Header.CODE_MANAGER_VIEW_NOTAPPROVE_INFIRMARY_LIST://승인되지않은 보건실 조회
                        System.out.println("승인되지않은 보건실 조회");
                        managerController.viewNotApprovedInfirmaryList(outputStream);
                        break;
                    case Header.CODE_MANAGER_VIEW_NOTAPPROVE_INFIRMARY_IMAGE://승인되지않은 보건실 조회
                        managerController.viewInfirmaryImage(inputStream,outputStream);
                        break;
                }
                break;
            case Header.ACTOR_ADMIN:
                switch (header.code)
                {
                    case Header.CODE_ADMIN_REGIST_INFIRMARY_NOTICE://보건실 공지사항 등록
                        adminController.registNotice(inputStream);
                        break;
                    case Header.CODE_ADMIN_CHANGE_INFIRMARY_NOTICE://보건실 공지사항 수정
                        adminController.updateNotice(inputStream,outputStream);
                        break;
                    case Header.CODE_ADMIN_DELETE_INFIRMARY_NOTICE://보건실 공지사항 삭제
                        adminController.deleteNotice(inputStream,outputStream);
                        break;
                    case Header.CODE_ADMIN_ADD_MEDICINE://약품추가
                        adminController.addMedicine(inputStream, outputStream);
                        break;
                    case Header.CODE_ADMIN_CHANGE_MEDICINE_QUANTITY: //약품수량수정
                       adminController.updateAmount(inputStream, outputStream);
                       break;
                    case Header.CODE_ADMIN_DELETE_MEDICINE://약품삭제
                        adminController.deleteMedicine(inputStream,outputStream);
                        break;
                    case Header.CODE_ADMIN_NOTIFICATION_SETTING://알림설정**************************
                        adminController.setAlarm(inputStream, outputStream);
                        break;
                    case Header.CODE_ADMIN_SEARCH_ALL_MEDICINE://모든 약 검색
                        userController.searchMedicineList(inputStream,outputStream);
                        break;
                    case Header.CODE_ADMIN_REGIST_INFIRMARY://대학교 보건실 등록
                        adminController.registInfirmary(inputStream,outputStream);
                        break;
                    case Header.CODE_ADMIN_CHANGE_INFIRMARY_INFO://대학교 보건실 정보수정
                        adminController.updateInfirmaryInfo(inputStream,outputStream);
                        break;
                    case Header.CODE_ADMIN_VIEW_MY_INFO://개인정보조회
                        adminController.viewInfo(inputStream, outputStream);
                        break;
                    case Header.CODE_ADMIN_CHANGE_MY_INFO://개인정보수정
                        adminController.updateUnfo(inputStream, outputStream);
                        break;
                    case Header.CODE_ADMIN_CHANGE_PW://비밀번호수정
                        adminController.changePW(inputStream);
                        break;
                    case Header.CODE_ADMIN_CHECK_PW://비밀번호확인
                        adminController.checkPW(inputStream,outputStream);
                        break;
                    case Header.CODE_ADMIN_VIEW_INFIRMARY_INFO://보건실 정보 확인
                        adminController.viewInfirmaryInfo(inputStream,outputStream);
                        break;
                    case Header.CODE_ADMIN_VIEW_INFIRMARY_MEDICINE://보건실 약 확인
                        adminController.viewMedicineList(inputStream,outputStream);
                        break;
                    case Header.CODE_ADMIN_SHOW_ALARM://알람 보여주기*******************************
                        adminController.showAlarm(inputStream,outputStream);
                        break;
                    case Header.CODE_ADMIN_VIEW_INFIRMARY_NOTICE://공지사항 조회
                        adminController.viewNotice(inputStream,outputStream);
                        break;
                    case Header.CODE_ADMIN_REGIST://운영자 회원가입
                        adminController.adminRegist(inputStream,outputStream);
                        break;
                }
                break;
            case Header.ACTOR_USER:
                switch (header.code)
                {
                    case Header.CODE_USER_VIEW_MY_INFO://개인정보조회
                        userController.viewUserInfo(inputStream,outputStream);
                        break;
                    case Header.CODE_USER_CHANGE_MY_INFO://개인정보수정
                        userController.userUpdateInfo(inputStream, outputStream);
                        break;
                    case Header.CODE_USER_CHANGE_PW://비밀번호수정
                        userController.changePW(inputStream);
                        break;
                    case Header.CODE_USER_CHECK_PW: // 비밀번호 확인
                        userController.checkPW(inputStream, outputStream);
                        break;
                    case Header.CODE_USER_SEARCH_BEWARE_OLDERLY_MEDICINE: //노약자주의
                        userController.searchElderlyMedicineList(inputStream, outputStream);
                        break;
                    case Header.CODE_USER_SEARCH_BEWARE_COMBINED_MEDICINE: //병용금기약 검
                        userController.viewBewreConbine(inputStream,outputStream);
                        break;
                    case Header.CODE_USER_SEARCH_SHAPE_MEDICINE://약모양 검색
                        userController.searchMedicineShape(inputStream,outputStream);
                        break;
                    case Header.CODE_USER_SEARCH_USER_UNIVERCITY_INFIRMARY_MEDICINE: //회원의 대학교 보건실 약 검색
                        userController.viewInfirmaryMedicineList(inputStream, outputStream);
                        break;
                    case Header.CODE_USER_SEARCH_CATEGORY_OLDERLY_MEDICINE://노약자 주의약 카테고리 별 검색
                        userController.viewCautionElderly(outputStream);
                        break;
                    case Header.CODE_USER_SEARCH_ALL_MEDICINE: //모든 약 검색
                        userController.searchMedicineList(inputStream,outputStream);
                        break;
                    case Header.CODE_USER_VIEW_USER_UNIVERCITY_INFIRMARY_MEDICINE_QUANTITY://회원의 대학교 보건실 약품 재고 조회
                        userController.searcMedicineNum(inputStream, outputStream);
                        break;
                    case Header.CODE_USER_VIEW_USER_UNIVERCITY_INFIRMARY_MEDICINE_NOTICE: //회원의 대학교 보건실 보건실 공지사항 조회
                        userController.viewNotice(inputStream, outputStream);
                        break;
                    case Header.CODE_USER_SAVE_MEDICINE://약 저장(즐겨찾기)
                        userController.saveBoomark(inputStream,outputStream);
                        break;
                    case Header.CODE_USER_VIEW_SAVE_MEDICINE://저장한 약 정보조회
                        userController.viewBookmarkMedicine(inputStream,outputStream);
                        break;
                    case Header.CODE_USER_SETTING_DOSE_MEDICINE://복용설정
                        userController.registMediAlarm(inputStream,outputStream);
                        break;
                    case Header.CODE_USER_LOGIN://로그인
                        userController.login(inputStream, outputStream);
                        break;
                    case Header.CODE_USER_REGIST://회원가입
                        userController.userRegist(inputStream, outputStream);
                        break;
                    case Header.CODE_USER_FIND_ID://ID찾기
                        userController.findId(inputStream, outputStream);
                        break;
                    case Header.CODE_USER_FIND_PW://PW찾기
                        userController.findPw(inputStream, outputStream);
                        break;
                    case Header.CODE_USER_SHOW_IMAGE://알약 사진보기
                        userController.showImage(inputStream,outputStream);
                        break;
                    case Header.CODE_USER_SHOW_SCHOOL_LIST://학교 리스트보기
                        userController.showSchoolList(outputStream);
                        break;
                    case Header.CODE_USER_SECESSION://회원탈퇴
                        userController.secession(inputStream);
                        break;
                    case Header.CODE_USER_SEARCH_CATEGORY_PREGNANT_MEDICINE://임부 주의약 카테고리 별 검색
                        userController.viewCautionPregnant(outputStream);
                        break;
                    case Header.CODE_USER_SEARCH_CATEGORY_CHILD_MEDICINE://아동 주의약 카테고리 별 검색
                        userController.viewCautionChild(outputStream);
                        break;
                    case Header.CODE_USER_VIEW_USER_UNIVERCITY_INFIRMARY_INFO://아동 주의약 카테고리 별 검색
                        userController.viewInfirmaryInfo(inputStream,outputStream);
                        break;
                    case Header.CODE_USER_VIEW_USER_UNIVERCITY_INFIRMARY_SELECT_MEDICINE://보건실 선택한 약 검색
                        userController.viewInfirmarySelectMedicine(inputStream,outputStream);
                        break;
                    case Header.CODE_USER_VIEW_USER_UNIVERCITY_BOOKMART_SELECT_MEDICINE://즐겨찾기 선택한 약 검색
                        userController.viewBookmarkSelectMedicine(inputStream,outputStream);
                        break;
                    case Header.CODE_USER_GET_USER_BOOKMART_PK://즐켜찾기 pk
                        userController.getBookmarkPk(inputStream,outputStream);
                        break;
                    case Header.CODE_USER_CHECK_END_TAKING_DAY://끝나는 날짜
                        userController.checkEndDay(inputStream);
                        break;
                    case Header.CODE_USER_SET_CHOICE_BOX:
                        userController.setChoiceBox(inputStream,outputStream);
                        break;
                }
                break;
        }

    }
}
