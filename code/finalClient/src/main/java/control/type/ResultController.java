package control.type;

import protocol.Header;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ResultController {
    public void handleRead(Header header, DataInputStream inputStream, DataOutputStream outputStream) {
        switch (header.code) {
            case Header.CODE_RESULT_SUCCESS://성공
                resultSuccess(outputStream);
                break;

            case Header.CODE_RESULT_FAIL://실패
                resultFail(outputStream);
                break;

            case Header.CODE_RESULT_HOLD://등록신청보류
                resultHold(outputStream);
                break;
        }
    }

    public void resultSuccess(DataOutputStream outputStream) {

    }

    public void resultFail(DataOutputStream outputStream) {

    }

    public void resultHold(DataOutputStream outputStream) {

    }
}