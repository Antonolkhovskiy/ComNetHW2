import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private ServerSocket serverSocket;

    public boolean tryCreateServerSocket(int port){
        try {
            serverSocket = new ServerSocket(port);
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

    public String getMessage()throws Exception{
            Socket socket = serverSocket.accept();
            BufferedReader message = new BufferedReader(new InputStreamReader((socket.getInputStream())));
            String soobweniye = message.readLine();
            return soobweniye;
    }

    public void closeServerSocket() throws IOException {
        serverSocket.close();
    }



}
