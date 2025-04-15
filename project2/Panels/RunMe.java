package project2.Panels;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class RunMe {

    public static void main(String[] args) {
        try {
            System.out.println("If this machine is hosting, make sure to start games with this IP address in HostIP.txt: " + InetAddress.getLocalHost().toString().split("/")[1]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        @SuppressWarnings("unused")
        GameWindow gameWindow = new GameWindow();
    }
    
}
