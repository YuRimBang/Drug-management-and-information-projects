package control.action;

import control.type.RequestController;
import control.type.ResponseController;
import control.type.ResultController;
import protocol.Header;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class ReadController
{
    private RequestController requestController;
    private ResponseController responseController;
    private ResultController resultController;
    public ReadController(RequestController requestController, ResponseController responseController, ResultController resultController)
    {
        this.requestController = requestController;
        this.responseController = responseController;
        this.resultController = resultController;
    }

    public void handleRead(Header header, DataInputStream inputStream, DataOutputStream outputStream) throws IOException, EOFException
    {
        switch (header.type)
        {
            case Header.TYPE_REQUEST:
                requestController.handleRead(header, inputStream, outputStream);
                break;
            case Header.TYPE_RESPONSE:
                responseController.handleRead(header, inputStream, outputStream);
                break;
            case Header.TYPE_RESULT:
                resultController.handleRead(header, inputStream, outputStream);
                break;
        }
    }
}
