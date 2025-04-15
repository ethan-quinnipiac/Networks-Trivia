package project2.Panels;

import java.util.Scanner;

public class HostIP {
    private String hostIP;

    public HostIP() {
        try {
            Scanner scan = new Scanner(HostIP.class.getClassLoader().getResourceAsStream("project2/Panels/HostIP.txt"));
            this.hostIP = scan.nextLine();
            scan.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public String getHostIP() {
        return this.hostIP;
    }
}
