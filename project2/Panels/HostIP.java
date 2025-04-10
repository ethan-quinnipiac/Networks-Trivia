package project2.Panels;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class HostIP {
    private String hostIP;

    public HostIP() {
        File file = new File(".\\project2\\Panels\\HostIP.txt");
        try {
            Scanner scan = new Scanner(file);
            this.hostIP = scan.nextLine();
            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getHostIP() {
        return this.hostIP;
    }
}
