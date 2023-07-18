package control.type;

import protocol.Header;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RequestController
{
    public void handleRead(Header header, DataInputStream inputStream, DataOutputStream outputStream) throws IOException
    {
        switch (header.actor)
        {
            case Header.ACTOR_MANAGER:
                switch (header.code)
                {
                    case Header.CODE_MANAGER_VIEW_ADMIN_LIST:
                        //운영자 목록 조회
                    case Header.CODE_MANAGER_SEARCH_ADMIN:
                        //운영자 검색
                    case Header.CODE_MANAGER_DELETE_ADMIN:
                        //운영자 삭제
                    case Header.CODE_MANAGER_VIEW_USER_LIST:
                        //회원 목록 조회
                    case Header.CODE_MANAGER_SEARCH_USER:
                        //회원 검색
                    case Header.CODE_MANAGER_DELETE_USER:
                        //회원 삭제
                    case Header.CODE_MANAGER_VIEW_INFIRMARY_LIST:
                        //보건실 목록 조회
                    case Header.CODE_MANAGER_SEARCH_INFIRMARY:
                        //보건실 검색
                    case Header.CODE_MANAGER_DELETE_INFIRMARY:
                        //보건실 승인
                }
                break;
            case Header.ACTOR_ADMIN:
                switch (header.code)
                {
                    case Header.CODE_ADMIN_REGIST_INFIRMARY_NOTICE:
                        //보건실 공지사항 등록
                    case Header.CODE_ADMIN_CHANGE_INFIRMARY_NOTICE:
                        //보건실 공지사항 수정
                    case Header.CODE_ADMIN_DELETE_INFIRMARY_NOTICE:
                        //보건실 공지사항 삭제
                    case Header.CODE_ADMIN_ADD_MEDICINE:
                        //약품추가
                    case Header.CODE_ADMIN_CHANGE_MEDICINE_QUANTITY:
                        //약품수량수정
                    case Header.CODE_ADMIN_DELETE_MEDICINE:
                        //약품삭제
                    case Header.CODE_ADMIN_NOTIFICATION_SETTING:
                        //알림설정
                    case Header.CODE_ADMIN_SEARCH_ALL_MEDICINE:
                        //모든 약 검색
                    case Header.CODE_ADMIN_REGIST_INFIRMARY:
                        //대학교 보건실 등록
                    case Header.CODE_ADMIN_CHANGE_INFIRMARY_INFO:
                        //대학교 보건실 정보수정
                    case Header.CODE_ADMIN_VIEW_MY_INFO:
                        //개인정보조회
                    case Header.CODE_ADMIN_CHANGE_MY_INFO:
                        //개인정보수정
                }
                break;
            case Header.ACTOR_USER:
                switch (header.code)
                {
                    case Header.CODE_USER_VIEW_MY_INFO:
                        //개인정보조회
                    case Header.CODE_USER_CHANGE_MY_INFO:
                        //개인정보수정
                    case Header.CODE_USER_SEARCH_BEWARE_OLDERLY_MEDICINE:
                        //노약자주의
                    case Header.CODE_USER_SEARCH_BEWARE_COMBINED_MEDICINE:
                        //병용금기약 검색
                    case Header.CODE_USER_SEARCH_SHAPE_MEDICINE:
                        //약모양 검색
                    case Header.CODE_USER_SEARCH_USER_UNIVERCITY_INFIRMARY_MEDICINE:
                        //회원의 대학교 보건실 약 검색
                    case Header.CODE_USER_SEARCH_CATEGORY_OLDERLY_MEDICINE:
                        //노약자 주의약 카테고리 별 검색
                    case Header.CODE_USER_SEARCH_ALL_MEDICINE:
                        //모든 약 검색
                    case Header.CODE_USER_VIEW_USER_UNIVERCITY_INFIRMARY_MEDICINE_QUANTITY:
                        //회원의 대학교 보건실 약품 재고 조회
                    case Header.CODE_USER_VIEW_USER_UNIVERCITY_INFIRMARY_MEDICINE_NOTICE:
                        //회원의 대학교 보건실 보건실 공지사항 조회
                    case Header.CODE_USER_SAVE_MEDICINE:
                        //약 저장(즐겨찾기)
                    case Header.CODE_USER_VIEW_SAVE_MEDICINE:
                        //저장한 약 정보조회
                    case Header.CODE_USER_SETTING_DOSE_MEDICINE:
                        //복용설정
                    case Header.CODE_USER_LOGIN:
                        //로그인
                    case Header.CODE_USER_REGIST:
                        //회원가입
                    case Header.CODE_USER_FIND_ID:
                        //ID찾기
                    case Header.CODE_USER_FIND_PW:
                        //PW찾기
                }
                break;
        }

    }
}
