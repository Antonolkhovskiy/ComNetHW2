import java.net.InetAddress;

/**
 * Created by Anton on 31.03.2018.
 */
public class Host {
    private String message;
    private InetAddress inetAddress;

    public Host(){}

    public Host(String message, InetAddress inetAddress) {
        this.message = message;
        this.inetAddress = inetAddress;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }
}
