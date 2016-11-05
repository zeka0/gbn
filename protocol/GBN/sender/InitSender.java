package protocol.GBN.sender;

import protocol.FlawedSocket;
import protocol.InetLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by AlienX
 */
public class InitSender {
    public static byte[] read_all(String dataPath) throws IOException {
        byte[] data = Files.readAllBytes(Paths.get(dataPath));
        return data;
    }

    public static void init_sender(int winSize, int maxPktSize,
                                   int sender_port, int receiver_port,
                                   byte[] data) throws IOException {

        FlawedSocket socket = new FlawedSocket(sender_port);

        InetLogger logger = new InetLogger("InetLog_sender", "sender");
        InetAddress rcvAddress = null;
        try {
            rcvAddress = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }
        GBNSenderThread senderThread = new GBNSenderThread(data, winSize, maxPktSize,
                socket, logger, rcvAddress, receiver_port, ThreadManager.get_timer_clock());
        GBNSenderReceiverThread receiverThread = new GBNSenderReceiverThread(senderThread.getFactory(),
                socket, logger, ThreadManager.get_stop_timer_lock());
        ThreadManager.register(senderThread, receiverThread, 1000);
        senderThread.start();
        receiverThread.start();
    }
}
