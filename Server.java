import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;

class Server {
    static ServerSocket serverSocket = null;
    static int PORT = 1337;

    static Socket hydrogenSocket = null;
    static Socket oxygenSocket = null;

    static DataOutputStream hydrogenOutputStream = null;
    static DataInputStream hydrogenInputStream = null;

    static DataOutputStream oxygenOutputStream = null;
    static DataInputStream oxygenInputStream = null;

    static IonThread oxygenThread = null;
    static IonThread hydrogenThread = null;

    static Semaphore hydrogenSemaphore = new Semaphore(0);
    static Semaphore oxygenSemaphore = new Semaphore(0);

    static Integer requestedOxygenCount = 0;
    static Integer requestedHydrogenCount = 0;
    
    static Integer bondedOxygenCount = 0;
    static Integer bondedHydrogenCount = 0;

    static HashMap<String, String> requests = new HashMap<String, String>();
    static int errors = 0;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(PORT);
            ClientAcceptThread accept = new ClientAcceptThread();
            
            // Ensures that both clients have connected
            accept.start();
            try {
                accept.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            oxygenThread.start();
            hydrogenThread.start();

            // Bonding
            while (true) {
                try {
                    hydrogenSemaphore.acquire(2);
                    oxygenSemaphore.acquire(1);

                    for(int i = 0; i < 2; i++) {
                        bondedHydrogenCount++;

                        String hBondLog = "H" + bondedHydrogenCount.toString() + ", bonded, " + getTimestamp();
                        sanityCheck(hBondLog.substring(0, 2));
                        System.out.println(hBondLog);
                        hydrogenOutputStream.writeUTF(hBondLog);
                    }

                    bondedOxygenCount++;

                    String oBondLog = "O" + bondedOxygenCount.toString() + ", bonded, " + getTimestamp();
                    sanityCheck(oBondLog.substring(0, 2));
                    System.out.println(oBondLog);
                    oxygenOutputStream.writeUTF(oBondLog);
                    
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getTimestamp() {
        LocalDateTime currTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS");
        String currTimeStr = currTime.format(formatter);

        return currTimeStr;
    }

    public static void sanityCheck(String key) {
        System.out.println(key);
        if(Server.requests.get(key) == null) {
            Server.errors++;
        }
        
        System.out.println("[Sanity Check] Errors found: " + errors);
    }
}

class ClientAcceptThread extends Thread {
    public void run() {
        System.out.println("Started accepting Sockets.");

        // While hydrogen socket and oxygen socket haven't connected yet
        while(Server.hydrogenSocket == null || Server.oxygenSocket == null) {
            try {
                Socket temp = Server.serverSocket.accept();

                DataInputStream tempStream = new DataInputStream(temp.getInputStream());

                String tempType = tempStream.readUTF();

                switch(tempType) {
                    case "Hydrogen":
                        Server.hydrogenSocket = temp;
                        Server.hydrogenInputStream = tempStream;
                        Server.hydrogenOutputStream = new DataOutputStream(temp.getOutputStream());
                        Server.hydrogenThread = new IonThread("Hydrogen");
                        
                        System.out.println("Accepted Hydrogen Client.");
                        break;
                    case "Oxygen":
                        Server.oxygenSocket = temp;
                        Server.oxygenInputStream = tempStream;
                        Server.oxygenOutputStream = new DataOutputStream(temp.getOutputStream());
                        Server.oxygenThread = new IonThread("Oxygen");
                        
                        System.out.println("Accepted Oxygen Client.");
                        break;
                    default:
                        System.out.print("Unidentified Socket rejected.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}