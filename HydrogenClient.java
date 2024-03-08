import java.util.Scanner;
import java.io.IOException;
import java.net.*;
import java.io.*;

class HydrogenClient {
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

            out.writeUTF("Hydrogen");
            Scanner sc = new Scanner(System.in);
            HydrogenListenerThread listener = new HydrogenListenerThread();

            listener.start();
            System.out.println("HYDROGEN CLIENT");
            System.out.print("Enter N: ");
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

class HydrogenListenerThread extends Thread {
    public void run() {
        while(true) {
            try {
                System.out.println(HydrogenClient.input.readUTF());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}