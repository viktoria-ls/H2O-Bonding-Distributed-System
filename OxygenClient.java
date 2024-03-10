import java.util.Scanner;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;

class OxygenClient {
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

            out.writeUTF("Oxygen");

            OxygenListenerThread listener = new OxygenListenerThread();
            listener.start();

            System.out.println("[OXYGEN CLIENT] Enter N:");

            int currStart = 1;
            
            while (true) {
                N = sc.nextInt();

                for (int i = currStart; i < currStart + N; i++) {
                    LocalDateTime currTime = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String currTimeStr = currTime.format(formatter);
                    System.out.println("O" + i + ", request, " + currTimeStr);
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

class OxygenListenerThread extends Thread {
    public void run() {
        while(true) {
            try {
                System.out.println(OxygenClient.input.readUTF());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}