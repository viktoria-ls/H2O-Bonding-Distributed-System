import java.util.concurrent.Semaphore;
import java.util.*;
import java.io.IOException;
import java.net.*;
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

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(PORT);
            ClientAcceptThread accept = new ClientAcceptThread();
            
            accept.start();
            try {
                accept.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            oxygenThread.start();
            hydrogenThread.start();

            while (true) {
                try {
                    hydrogenSemaphore.acquire(2);
                    oxygenSemaphore.acquire(1);

                    for(int i = 0;i < 2;i++) {
                        bondedHydrogenCount++;

                        String temp = "H" + bondedHydrogenCount.toString() + ", bonded, time";

                        System.out.println(temp);
                        hydrogenOutputStream.writeUTF(temp);
                    }

                    bondedOxygenCount++;
                    String temp = "O" + bondedOxygenCount.toString() + ", bonded, time";
                    System.out.println(temp);
                    oxygenOutputStream.writeUTF(temp);
                    
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        
    }
}

class ClientAcceptThread extends Thread {
    public void run() {
        System.out.println("Started accepting Sockets.");
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
                        System.out.print("Unidentified Socket Rejected.");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
}

class BondThread extends Thread {
    public void run() {

    }
}