package Thread;

import control.action.ActionController;
import protocol.Header;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Server extends Thread
{
    private Socket socket;

    public Server(Socket socket) {
        this.socket = socket;
    }

    @Override
     public void run() {
        try {
            boolean stop = false;

            while (true)
            {
                DataInputStream is = new DataInputStream(socket.getInputStream());
                DataOutputStream os = new DataOutputStream(socket.getOutputStream());

                Header header = Header.readHeader(is);

                    byte[] body = new byte[header.bodySize];
                    is.read(body);
                    DataInputStream bodyReader = new DataInputStream(new ByteArrayInputStream(body));
                    ActionController controller = new ActionController();

                    controller.handleRequest(header, bodyReader, os);

            }
        } catch (Exception e)
        {
            System.out.println(e);
        }
    }



}
