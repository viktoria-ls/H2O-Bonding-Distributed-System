import java.util.HashMap;
import java.util.Scanner;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;

class OxygenClient {
    static int N = 0;
    static long startTime;

    //Server
    private static int port = 1337;
    private static String address = "127.0.0.1";
    //   10.50.190.111
    private static Socket mainServer = null;
    
    static DataOutputStream out;
    static DataInputStream input;

    static HashMap<String, String> requests = new HashMap<String, String>();

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            mainServer = new Socket(address, port);
            out = new DataOutputStream(mainServer.getOutputStream());
            input = new DataInputStream(mainServer.getInputStream());

            out.writeUTF("Oxygen");

            OxygenListenerThread listener = new OxygenListenerThread();
            listener.start();

            System.out.println("[OXYGEN CLIENT] Enter M:");

            int currStart = 1;
            
            while (true) {
                N = sc.nextInt();

                for (int i = currStart; i < currStart + N; i++) {
                    LocalDateTime currTime = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS");
                    String currTimeStr = currTime.format(formatter);
                    System.out.println("O" + i + ", request, " + currTimeStr);

                    requests.put("O" + i, "requested");
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

class OxygenListenerThread extends Thread {
    int errors = 0;

    public void run() {
        while(true) {
            try {
                String read = OxygenClient.input.readUTF();
                String requestKey = read.substring(0, read.indexOf(","));
                sanityCheck(requestKey);
                System.out.println(read);

                if(Integer.parseInt(requestKey.substring(1)) == OxygenClient.N) {
                    long endTime = System.currentTimeMillis();
                    System.out.println("TOTAL TIME FOR OXYGEN (milliseconds): " + (endTime - OxygenClient.startTime));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sanityCheck(String key) {
        if(OxygenClient.requests.get(key) == null || OxygenClient.requests.get(key).equals("completed")) {
            errors++;
        } else {
            OxygenClient.requests.put(key, "completed");
        }
        
        System.out.println("[Sanity Check] Errors Found: " + errors);
    }
}