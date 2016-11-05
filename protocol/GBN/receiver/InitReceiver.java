package protocol.GBN.receiver;

import protocol.InetLogger;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by AlienX
 */
public class InitReceiver {
    public static void init_receiver(int winSize, int maxPktSize,
                                     int sender_port, int receiver_port,
                                     int dataLength,
                                     String outPath) throws SocketException, UnknownHostException {
        DatagramSocket socket = new DatagramSocket(receiver_port);
        InetLogger logger = new InetLogger("InetLog_receiver", "receiver");
        InetAddress senderAddress = InetAddress.getByName("localhost");

        GBNReceiverThread receiverThread = new GBNReceiverThread(dataLength, winSize, maxPktSize,
                socket, senderAddress, sender_port, logger, outPath);
        receiverThread.start();
    }
}
