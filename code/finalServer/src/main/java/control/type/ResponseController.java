package control.type;

import control.entityControl.AdminController;
import control.entityControl.ManagerController;
import control.entityControl.UserController;
import protocol.Header;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ResponseController
{
    UserController userController = new UserController();
    ManagerController managerController = new ManagerController();
    AdminController adminController = new AdminController();

    public void handleRead(Header header, DataInputStream inputStream, DataOutputStream outputStream) throws IOException
    {
        switch (header.actor)
        {
            case Header.ACTOR_SERVER:
                switch (header.code)
                {
                    case Header.CODE_SERVER_RES_ADMIN_LIST:
                        //운영자 목록
                    case Header.CODE_SERVER_RES_USER_LIST:
                        //회원 목록
                    case Header.CODE_SERVER_RES_USER_INFO:
                        //회원 개인정보
                    case Header.CODE_SERVER_RES_INFIRMARY_LIST:
                        //보건실 목록
                    case Header.CODE_SERVER_RES_INFIRMARY_INFO:
                        //보건실 정보
                    case Header.CODE_SERVER_RES_LOST_ID:
                        //잃어버린 ID
                    case Header.CODE_SERVER_RES_LOST_PW:
                        //잃어버린 PW
                    case Header.CODE_SERVER_RES_ALL_MADICINE:
                        //모든 약
                    case Header.CODE_SERVER_RES_ADMIN_INFO:
                        //운영자 개인정보
                    case Header.CODE_SERVER_RES_BEWARE_OLDERLY_MEDICIN_INFO:
                        //노약자 주의 약
                    case Header.CODE_SERVER_RES_BEWARE_COMBINE_MEDICINE_INFO:
                        //병용금기약
                    case Header.CODE_SERVER_RES_SHAPE_MEDICINE_INFO:
                        //약 모양
                    case Header.CODE_SERVER_RES_USER_UNIVERCITY_INFIRMARY_MEDICINE:
                        //회원의 대학교 보건실 약
                    case Header.CODE_SERVER_RES_USER_UNIVERCITY_INFIRMARY_MEDICINE_QUANTITY:
                        //회원의 대학교 보건실 약 재고
                    case Header.CODE_SERVER_RES_USER_UNIVERCITY_INFIRMARY_MEDICINE_NOTICE:
                        //회원의 대학교 보건실 공지사항
                    case Header.CODE_SERVER_RES_CATEGORY_OLDERLY_MEDICINE:
                        //노약자 주의 카테고리별 검색
                    case Header.CODE_SERVER_RES_USER_SAVE_MEDICINE:
                        //유저가 저장한 약 정보
                }
                break;
            case Header.ACTOR_MANAGER:
                switch (header.code)
                {
                    case Header.CODE_MANAGER_AMDIN_APPROVE://운영자 승인
                        System.out.println("받아볼게~");
                        managerController.acceptAdmin(inputStream);
                        break;
                    case Header.CODE_MANAGER_INFIRMARY_APPROVE://보건실 승인
                        System.out.println("받아볼게~");
                        managerController.acceptInfirmary(inputStream);
                        break;
                }
                break;

        }
    }
}
