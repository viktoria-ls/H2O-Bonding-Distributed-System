import java.util.Scanner;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;

class HydrogenClient {
    static int N = 0;

    //Server
    private static int port = 1337;
    private static String address = "127.0.0.1";
    private static Socket mainServer = null;

    static DataOutputStream out;
    static DataInputStream input;

    static Scanner sc = new Scanner(System.in);
    
    public static void main(String[] args) {
        try {
            mainServer = new Socket(address, port);
            out = new DataOutputStream(mainServer.getOutputStream());
            input = new DataInputStream(mainServer.getInputStream());

            out.writeUTF("Hydrogen");

            HydrogenListenerThread listener = new HydrogenListenerThread();
            listener.start();

            System.out.println("[HYDROGEN CLIENT] Enter N:");

            int currStart = 1;

            while (true) {
                N = sc.nextInt();

                for (int i = currStart; i < currStart + N; i++) {
                    LocalDateTime currTime = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String currTimeStr = currTime.format(formatter);
                    System.out.println("H" + i + ", request, " + currTimeStr);
                }

                out.writeInt(N);

                currStart += N;
            }
            
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sc.close();
        }
        
    }
}

class HydrogenListenerThread extends Thread {
    public void run() {
        while(true) {
            try {
                System.out.println(HydrogenClient.input.readUTF());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}