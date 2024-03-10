import java.util.concurrent.Semaphore;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IonThread extends Thread {
    String type;
    DataOutputStream o;
    DataInputStream i;
    Integer requestedCount;
    Integer bondedCount;
    Semaphore typeSemaphore;


    IonThread(String t) {
        this.type = t;

        switch(type) {
            case "Hydrogen":
                this.requestedCount = Server.requestedHydrogenCount;
                this.bondedCount = Server.bondedHydrogenCount;
                this.o = Server.hydrogenOutputStream;
                this.i = Server.hydrogenInputStream;
                this.typeSemaphore = Server.hydrogenSemaphore;
                
                break;
            case "Oxygen":
                this.requestedCount = Server.requestedOxygenCount;
                this.bondedCount = Server.bondedOxygenCount;
                this.o = Server.oxygenOutputStream;
                this.i = Server.oxygenInputStream;
                this.typeSemaphore = Server.oxygenSemaphore;
        }
    }

    public void run() {
        try {
            while (true) {
                int amount = i.readInt();

                for(int i = 0; i < amount; i++) {
                    requestedCount++;
                    typeSemaphore.release(1);

                    LocalDateTime currTime = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String currTimeStr = currTime.format(formatter);

                    String requestLog = type.charAt(0) + requestedCount.toString() + ", request, " + currTimeStr;

                    System.out.println(requestLog);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
