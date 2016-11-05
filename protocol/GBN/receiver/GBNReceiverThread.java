package protocol.GBN.receiver;

import protocol.ByteConvertor;
import protocol.GBN.GBNPkt;
import protocol.InetLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by AlienX
 */
public class GBNReceiverThread extends Thread{
    private final int top;
    private final int base = 0;
    private final int dataLength;
    private final int maxPktSize;
    private int exp_pkt_num;

    private GBNPktBuilder builder;
    private String outputPath;//should also include a filename
    private DatagramSocket socket;
    private InetLogger logger;
    private InetAddress senderAddress;
    private int sender_port;

    public GBNReceiverThread(int dataLength,
                             int winSize, int maxPktSize,
                             DatagramSocket socket, InetAddress senderAddress, int sender_port,
                             InetLogger logger,
                             String outputPath){
        this.senderAddress = senderAddress;
        this.sender_port = sender_port;
        exp_pkt_num = base;
        this.logger = logger;
        this.maxPktSize = maxPktSize;
        this.socket = socket;
        this.outputPath = outputPath;
        this.dataLength = dataLength;
        builder = new GBNPktBuilder(dataLength);
        top = (int) Math.ceil(winSize / maxPktSize) - 1;
    }

    private DatagramPacket mk_ack_pkt(int ack_num){
        byte[] dummy_data = {0};
        GBNPkt pkt = new GBNPkt(0, ack_num, dummy_data);
        byte[] data = pkt.toBytes();
        DatagramPacket datagramPacket = new DatagramPacket(data, 0, data.length, senderAddress, sender_port);
        return datagramPacket;
    }

    @Override
    public void run(){
        byte[] buffer = new byte[maxPktSize + 20];
        DatagramPacket pkt = new DatagramPacket(buffer, buffer.length);
        DatagramPacket ack_pkt = null;
        GBNPkt gbnPkt;
        while(!builder.isFinish()){
            try {
                socket.receive(pkt);
            } catch (IOException e) {
                logger.log(Level.WARNING, "IOException: " + e);
            }

            gbnPkt = GBNPkt.buildFromBytes(ByteConvertor.bytesCopy(pkt.getData(), 0, pkt.getLength() - 1));
            if(GBNPkt.isCorrupted(gbnPkt)){
                logger.log(Level.WARNING, "Corrupted pkt received");
            }
            else if(gbnPkt.get_seq_number() == exp_pkt_num){
                /**
                 * not corrupted and has the desired ack number
                 */

                builder.appendData(gbnPkt.get_data());
                //send ack pkt
                ack_pkt = mk_ack_pkt(gbnPkt.get_seq_number());
                try {
                    socket.send(ack_pkt);
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Error in sending ack packet: " + gbnPkt.get_seq_number());
                }
                exp_pkt_num = (exp_pkt_num + 1)%(top + 1);
            }
        }

        logger.log(Level.INFO, "Done receiving");
        try {
            File file = new File(outputPath);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fop = null;
            fop = new FileOutputStream(file);

            fop.write(builder.get_data());
            fop.flush();
            fop.close();
            System.out.println("Done");
        }catch (FileNotFoundException e){
            logger.log(Level.SEVERE, "File Not Found: Discard all received data");
        }catch (IOException ex){
            logger.log(Level.SEVERE, "IOException: Discard all received data");
        }
    }
}
