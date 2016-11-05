package protocol.GBN.sender;

import protocol.GBN.GBNPkt;
import protocol.InetLogger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;

/**
 * Created by AlienX
 */
public class GBNSenderReceiverThread extends Thread{
    private final Object stop_timer_lock;
    private final InetLogger logger;
    private final GBNPktFactory factory;
    private final DatagramSocket socket;
    private volatile boolean sdStop;


    public GBNSenderReceiverThread(GBNPktFactory factory, DatagramSocket socket,
                               InetLogger logger, Object stop_timer_lock){
        this.factory = factory;
        this.socket = socket;
        this.logger = logger;
        this.stop_timer_lock = stop_timer_lock;
        sdStop = false;
    }

    public void set_sdStop(){
        sdStop = true;
    }

    @Override
    public void run(){
        byte[] buffer = new byte[factory.get_maxPktSize() + 20];
        DatagramPacket pkt = new DatagramPacket(buffer, buffer.length);
        GBNPkt gbnPkt;

        while(!sdStop){
            try {
                socket.receive(pkt);
            } catch (IOException e) {
                logger.log(Level.WARNING, "IO exception in receiver thread");
            }
            gbnPkt = GBNPkt.buildFromBytes(pkt.getData());
            if(!GBNPkt.isCorrupted(gbnPkt)){
                logger.log(Level.INFO, "Received ack number: " + gbnPkt.get_ack_number());

                synchronized (factory){
                    factory.ack_pkt_num(gbnPkt.get_ack_number());
                    if(factory.get_base() >= factory.get_top()){
                        //done in a window size
                        synchronized (stop_timer_lock){
                            stop_timer_lock.notifyAll();
                        }
                    }
                    if(factory.isSlideToEnd() && factory.get_top() == gbnPkt.get_ack_number()){
                        //stop all, has finished
                        System.out.println("Stopping all threads");
                        ThreadManager.stop_sender_thread();
                        ThreadManager.stop_receiver_thread();
                    }
                }
            }
        }
    }
}
