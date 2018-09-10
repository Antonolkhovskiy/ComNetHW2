import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPClient {
    private Socket clientSocket;

    public boolean tryCreatClientSocket(int port) {
        try {
            clientSocket = new Socket("localhost", port);//127.0.0.1
        } catch (IOException e) {
            //e.printStackTrace();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return false;
        }
        return true;
    }

    public void sendMessage(String message) throws Exception{
        DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
        dataOutputStream.writeBytes(message);
        clientSocket.close();
    }

    public void closeClientSocket() throws IOException {
        clientSocket.close();
    }
}
