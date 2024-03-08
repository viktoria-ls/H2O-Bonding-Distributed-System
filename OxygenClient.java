import java.util.Scanner;
import java.net.*;
import java.io.*;

class OxygenClient {
    static int N = 0;

    //Server
    private static int port = 1337;
    private static String address = "127.0.0.1";
    private static Socket mainServer = null;
    
    static DataOutputStream out;
    static DataInputStream input;
    public static void main(String[] args) {
        try {
            mainServer = new Socket(address, port);
            out = new DataOutputStream(mainServer.getOutputStream());
            input = new DataInputStream(mainServer.getInputStream());

            out.writeUTF("Oxygen");
            Scanner sc = new Scanner(System.in);
            OxygenListenerThread listener = new OxygenListenerThread();
            listener.start();

            System.out.println("Oxygen CLIENT");
            System.out.print("Enter N:  ");
            
            while (true) {
                
                N = sc.nextInt();

                out.writeInt(N);

            }
            
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}

class OxygenListenerThread extends Thread {
    public void run() {
        while(true) {
            try {
                System.out.println(OxygenClient.input.readUTF());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}