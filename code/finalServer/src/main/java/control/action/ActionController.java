package control.action;

import control.type.RequestController;
import control.type.ResponseController;
import control.type.ResultController;
import protocol.Header;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class ActionController
{
    ReadController readController;
    RequestController requestController;
    ResponseController responseController;
    ResultController resultController;

    public ActionController()
    {
        requestController = new RequestController();
        responseController = new ResponseController();
        resultController = new ResultController();

        readController = new ReadController(requestController, responseController, resultController);
    }

    public void handleRequest(Header header, DataInputStream inputStream, DataOutputStream outputStream) throws IOException
    {
        System.out.println("받았수~");
        readController.handleRead(header, inputStream, outputStream);
    }
}
