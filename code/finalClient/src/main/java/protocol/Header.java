package protocol;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.PublicKey;

public class Header implements MySerializableClass
{
    public final static String ip = "172.30.125.220";

    public final static byte TYPE_REQUEST = 1;
    public final static byte TYPE_RESPONSE = 2;
    public final static byte TYPE_RESULT = 3;

    public final static byte ACTOR_SERVER = 0;
    public final static byte ACTOR_MANAGER = 1;
    public final static byte ACTOR_ADMIN = 2;
    public final static byte ACTOR_USER = 3;
    //관리자 REQUEST
    public final static byte CODE_MANAGER_VIEW_ADMIN_LIST = 1;//운영자 목록 조회
    public final static byte CODE_MANAGER_SEARCH_ADMIN = 2;//운영자 검색
    public final static byte CODE_MANAGER_DELETE_ADMIN = 3;//운영자 삭제
    public final static byte CODE_MANAGER_VIEW_USER_LIST = 4;//회원 목록 조회
    public final static byte CODE_MANAGER_SEARCH_USER = 5;//회원검색
    public final static byte CODE_MANAGER_DELETE_USER = 6;//회원삭제
    public final static byte CODE_MANAGER_VIEW_INFIRMARY_LIST = 7;//보건실 목록 조회
    public final static byte CODE_MANAGER_SEARCH_INFIRMARY = 8;//보건실 검색
    public final static byte CODE_MANAGER_DELETE_INFIRMARY = 9;//보건실 삭제
    public final static byte CODE_MANAGER_VIEW_NOTAPPROVE_ADMIN = 10;//승인되지않은 운영자
    public final static byte CODE_MANAGER_VIEW_NOTAPPROVE_ADMIN_IMAGE = 11;//승인되지않은 운영자 재직증명서 조회
    public final static byte CODE_MANAGER_VIEW_NOTAPPROVE_INFIRMARY_LIST = 12;//승인되지않은 보건실 조회
    public final static byte CODE_MANAGER_VIEW_NOTAPPROVE_INFIRMARY_IMAGE = 13;//승인되지않은 보건실 증명서 조회

    //관리자 RESPONSE
    public final static byte CODE_MANAGER_AMDIN_APPROVE= 1; //운영자 승인
    public final static byte CODE_MANAGER_INFIRMARY_APPROVE = 2; //보건실 승인

    //운영자 REQUEST
    public final static byte CODE_ADMIN_REGIST_INFIRMARY_NOTICE = 1;//보건실 공지사항 등록요청
    public final static byte CODE_ADMIN_CHANGE_INFIRMARY_NOTICE = 2;//보건실 공지사항 수정요청
    public final static byte CODE_ADMIN_DELETE_INFIRMARY_NOTICE = 3;//보건실 공지사항 삭제요청
    public final static byte CODE_ADMIN_ADD_MEDICINE = 4;//약품추가
    public final static byte CODE_ADMIN_CHANGE_MEDICINE_QUANTITY = 5;//약품수량수정
    public final static byte CODE_ADMIN_DELETE_MEDICINE = 6;//약품삭제
    public final static byte CODE_ADMIN_NOTIFICATION_SETTING = 7;//알림설정
    public final static byte CODE_ADMIN_SEARCH_ALL_MEDICINE = 8;//모든 약 검색
    public final static byte CODE_ADMIN_REGIST_INFIRMARY = 9;//대학교 보건실 등록
    public final static byte CODE_ADMIN_CHANGE_INFIRMARY_INFO = 10;//대학교 보건실 정보수정
    public final static byte CODE_ADMIN_VIEW_MY_INFO = 11;//개인정보조회
    public final static byte CODE_ADMIN_CHANGE_MY_INFO = 12;//개인정보수정
    public final static byte CODE_ADMIN_CHANGE_PW = 13;//비밀번호수정
    public final static byte CODE_ADMIN_CHECK_PW = 14;//비밀번호수정
    public final static byte CODE_ADMIN_VIEW_INFIRMARY_INFO = 15;//대학교 보건실 정보수정
    public final static byte CODE_ADMIN_VIEW_INFIRMARY_MEDICINE = 16;//대학교 보건실 정보수정
    public final static byte CODE_ADMIN_SHOW_ALARM = 17;//알림설정
    public final static byte CODE_ADMIN_VIEW_INFIRMARY_NOTICE = 18;//보건실 공지사항 조회
    public final static byte CODE_ADMIN_REGIST = 19;//보건실 공지사항 조회



    //이용자의 REQUEST
    public final static byte CODE_USER_VIEW_MY_INFO = 1;//개인정보조회
    public final static byte CODE_USER_CHANGE_MY_INFO = 2;//개인정보수정
    public final static byte CODE_USER_SEARCH_BEWARE_OLDERLY_MEDICINE = 3;//노약자주의 약 검색
    public final static byte CODE_USER_SEARCH_BEWARE_COMBINED_MEDICINE = 4;//병용금기 약 검색
    public final static byte CODE_USER_SEARCH_SHAPE_MEDICINE = 5;//약모양 검색
    public final static byte CODE_USER_SEARCH_USER_UNIVERCITY_INFIRMARY_MEDICINE = 6;//회원의 대학교 보건실 약 검색
    public final static byte CODE_USER_SEARCH_CATEGORY_OLDERLY_MEDICINE = 7;//노약자 주의약 카테고리 별 검색
    public final static byte CODE_USER_SEARCH_ALL_MEDICINE = 8;//모든 약 검색
    public final static byte CODE_USER_VIEW_USER_UNIVERCITY_INFIRMARY_MEDICINE_QUANTITY = 9;//회원의 대학교 보건실 약품 재고 조회
    public final static byte CODE_USER_VIEW_USER_UNIVERCITY_INFIRMARY_MEDICINE_NOTICE = 10;//회원의 대학교 보건실 보건실 공지사항 조회
    public final static byte CODE_USER_SAVE_MEDICINE = 11;//약 저장(즐겨찾기)
    public final static byte CODE_USER_VIEW_SAVE_MEDICINE = 12;//저장한 약 정보조회
    public final static byte CODE_USER_SETTING_DOSE_MEDICINE = 13;//복용설정
    public final static byte CODE_USER_LOGIN = 14;//로그인
    public final static byte CODE_USER_REGIST = 15;//회원가입
    public final static byte CODE_USER_FIND_ID = 16;//아이디찾기
    public final static byte CODE_USER_FIND_PW = 17;//비밀번호찾기
    public final static byte CODE_USER_SHOW_IMAGE = 18;//약품이미지보기
    public final static byte CODE_USER_SHOW_SCHOOL_LIST = 19;//학교 리스트보기
    public final static byte CODE_USER_CHANGE_PW = 20; //비밀번호 수정
    public final static byte CODE_USER_CHECK_PW = 21; //비밀번호 확인
    public final static byte CODE_USER_SECESSION = 22;//회원탈퇴
    public final static byte CODE_USER_SEARCH_CATEGORY_PREGNANT_MEDICINE = 23;//임부 주의약 카테고리 별 검색
    public final static byte CODE_USER_SEARCH_CATEGORY_CHILD_MEDICINE = 24;//아동 주의약 카테고리 별 검색
    public final static byte CODE_USER_VIEW_USER_UNIVERCITY_INFIRMARY_INFO = 25;//회원의 대학교 보건실 정보 조회
    public final static byte CODE_USER_VIEW_USER_UNIVERCITY_INFIRMARY_SELECT_MEDICINE = 26;//보건실 선택한 약 검색
    public final static byte CODE_USER_VIEW_USER_UNIVERCITY_BOOKMART_SELECT_MEDICINE = 27;//즐겨찾기 선택한 약 검색
    public final static byte CODE_USER_GET_USER_BOOKMART_PK = 28;//즐겨찾기 pk
    public final static byte CODE_USER_CHECK_END_TAKING_DAY = 29;//끝나는 날짜
    public final static byte CODE_USER_SET_CHOICE_BOX = 30;//


    //이용자의 RESPONSE
//    public final static byte CODE_USER_DOSE_ALARM= 1;//복용알림
//    public final static byte CODE_USER_MY_INFIRMARY_ALARM = 2;//회원의 보건실 알림
    //????필요한가? -> 아니~

    //서버의 RESPONSE
    public final static byte CODE_SERVER_RES_ADMIN_LIST = 1;//운영자 목록
    public final static byte CODE_SERVER_RES_USER_LIST = 2;//회원 목록
    public final static byte CODE_SERVER_RES_USER_INFO = 3;//회원 개인정보
    public final static byte CODE_SERVER_RES_INFIRMARY_LIST = 4;//보건실 목록
    public final static byte CODE_SERVER_RES_INFIRMARY_INFO = 5;//보건실 정보
    public final static byte CODE_SERVER_RES_LOST_ID = 6;//잃어버린 ID
    public final static byte CODE_SERVER_RES_LOST_PW = 7;//잃어버린 PW
    public final static byte CODE_SERVER_RES_ALL_MADICINE = 8;//모든 약
    public final static byte CODE_SERVER_RES_ADMIN_INFO = 9;//운영자 개인정보
    public final static byte CODE_SERVER_RES_BEWARE_OLDERLY_MEDICIN_INFO = 10;//노약자 주의 약
    public final static byte CODE_SERVER_RES_BEWARE_COMBINE_MEDICINE_INFO = 11;//병용금기약
    public final static byte CODE_SERVER_RES_SHAPE_MEDICINE_INFO = 12;//약 모양
    public final static byte CODE_SERVER_RES_USER_UNIVERCITY_INFIRMARY_MEDICINE = 13;//회원의 대학교 보건실 약
    public final static byte CODE_SERVER_RES_USER_UNIVERCITY_INFIRMARY_MEDICINE_QUANTITY = 14;//회원의 대학교 보건실 약 재고
    public final static byte CODE_SERVER_RES_USER_UNIVERCITY_INFIRMARY_MEDICINE_NOTICE = 15;//회원의 대학교 보건실 공지사항
    public final static byte CODE_SERVER_RES_CATEGORY_OLDERLY_MEDICINE = 16; //노약자 주의 카테고리별 검색
    public final static byte CODE_SERVER_RES_USER_SAVE_MEDICINE = 17;//유저가 저장한 약 정보


    public final static byte CODE_RESULT_SUCCESS = 1;//성공
    public final static byte CODE_RESULT_FAIL = 2;//실패
    public final static byte CODE_RESULT_HOLD = 3;//등록신청 보류


    public final static int SIZE_HEADER = 6;

    public byte type;
    public byte actor;
    public byte code;
    public int bodySize;

    public Header(byte type, byte actor, byte code,int bodySize)
    {
        this.type = type;
        this.actor = actor;
        this.code = code;
        this.bodySize = bodySize;
    }
    public static Header readHeader(DataInputStream dis) throws IOException
    {
        byte type = dis.readByte();
        byte actor = dis.readByte();
        byte code = dis.readByte();
        int bodySize = dis.readInt();
        return new Header(type,actor,code, bodySize);
    }

    @Override
    public byte[] getBytes() throws IOException
    {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);
        dos.writeByte(type);
        dos.writeByte(actor);
        dos.writeByte(code);
        dos.writeInt(bodySize);
        return buf.toByteArray();
    }

}
