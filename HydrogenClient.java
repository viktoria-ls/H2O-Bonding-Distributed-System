import java.util.HashMap;
import java.util.Scanner;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;

class HydrogenClient {
    static int N = 0;
    static long startTime;

    //Server
    private static int port = 1337;
    private static String address = "127.0.0.1";
    private static Socket mainServer = null;

    // Stores request ID and request Timestamp
    static HashMap<String, String> requests = new HashMap<String, String>();

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
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS");
                    String currTimeStr = currTime.format(formatter);
                    System.out.println("H" + i + ", request, " + currTimeStr);

                    requests.put("H" + i, "requested");
                }

                out.writeInt(N);
                startTime = System.currentTimeMillis();

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
    int errors = 0;

    public void run() {
        while(true) {
            try {
                String read = HydrogenClient.input.readUTF();
                String requestKey = read.substring(0, read.indexOf(","));
                sanityCheck(requestKey);
                System.out.println(read);

                if(Integer.parseInt(requestKey.substring(1)) == HydrogenClient.N) {
                    long endTime = System.currentTimeMillis();
                    System.out.println("TOTAL TIME FOR HYDROGEN (milliseconds): " + (endTime - HydrogenClient.startTime));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Checks if bonded Hydrogen was even requested
    public void sanityCheck(String key) {
        if(HydrogenClient.requests.get(key) == null || HydrogenClient.requests.get(key).equals("completed")) {
            errors++;
        } else {
            HydrogenClient.requests.put(key, "completed");
        }
        
        System.out.println("[Sanity Check] Errors found: " + errors);
    }
}