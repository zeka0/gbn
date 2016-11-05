package protocol.GBN;

import protocol.GBN.receiver.InitReceiver;
import protocol.GBN.sender.InitSender;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by AlienX
 */
public class Main {
    public static void main(String[] args){
        String dataPath = "E:\\JetbrainsProjects\\Java\\JavaNet\\src\\protocol\\input\\test.txt";
        String outPath = "E:\\JetbrainsProjects\\Java\\JavaNet\\src\\protocol\\output\\output.txt";
        int winSize = 2000;
        int maxPktSize = 200;
        int sender_port = 7878;
        int receiver_port = 8989;

        byte[] data = null;
        try {
            data = Files.readAllBytes(Paths.get(dataPath));
        } catch (IOException e) {
            return;
        }
        try {
            InitSender.init_sender(winSize, maxPktSize, sender_port, receiver_port, data);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            InitReceiver.init_receiver(winSize, maxPktSize, sender_port, receiver_port, data.length, outPath);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }
    }
}
