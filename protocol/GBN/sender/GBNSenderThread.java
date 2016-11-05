package protocol.GBN.sender;

import protocol.InetLogger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;

/**
 * Created by AlienX
 */
public class GBNSenderThread extends Thread{
    private volatile boolean isTimeout;//required by timer thread
    private final Object timer_lock;
    private volatile boolean sdStop;

    private final InetLogger logger;
    private final DatagramSocket socket;
    private final int receiver_port;
    private final InetAddress rcvAddress;
    private final GBNPktFactory factory;
    /**
     * Typically window size is negotiated between sender and receiver
     * We ignore this convention here
     * port represents the receiver side port
     * */
    public GBNSenderThread(byte[] data,
                           int winSize, int maxPktSize,
                           DatagramSocket socket, InetLogger logger,
                           InetAddress rcvAddress, int receiver_port,
                           Object timer_lock){
        factory = new GBNPktFactory(data, winSize, maxPktSize);
        this.socket = socket;
        this.timer_lock = timer_lock;
        this.rcvAddress = rcvAddress;
        this.receiver_port = receiver_port;
        this.logger = logger;
    }

    public void set_sdStop(){
        sdStop = true;
    }

    public GBNPktFactory getFactory(){
        return factory;
    }

    public void set_isTimeout(boolean isTimeout){
        /**
         * sender thread checks isTimeout only when it's waiting is interrupted
         * */
        this.isTimeout = isTimeout;
    }

    private void resend_all(){
        synchronized (factory) {
            DatagramPacket pkt;
            byte[] bytes;
            for (int i = factory.get_base(); i <= factory.get_top(); ++i) {
                bytes = factory.get_pktAt(i).toBytes();
                pkt = new DatagramPacket(bytes, 0, bytes.length, rcvAddress, receiver_port);
                try {
                    socket.send(pkt);
                } catch (IOException e) {
                    logger.log(Level.WARNING, "IOException in sending packet:\n" + e);
                }
            }
        }
    }

    private void send_all(){
        synchronized (factory) {
            /**
             * send all packages ranked through base to top
             * */
            factory.slide_window();
            resend_all();
        }
    }

    @Override
    public void run(){
        do {
            synchronized (factory) {
                if (!isTimeout || factory.get_base() >= factory.get_top()) {
                    logger.log(Level.INFO, "window acknowledged, slide window");
                    send_all();
                } else {
                    logger.log(Level.WARNING, "Time out");
                    resend_all();
                }
            }
            ThreadManager.start_timer();
            isTimeout = false;
            synchronized (timer_lock) {
                ThreadManager.start_timer();
                try {
                    timer_lock.wait();
                } catch (InterruptedException e) {
                    //not handled here
                }
            }
        }while(!sdStop);
    }
}
