package protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;

/**
 * Created by AlienX
 */
public class FlawedSocket extends DatagramSocket{
    private Random random_generator;
    private final int lost_freq;
    private final int corrupt_freq;

    /**
     * lost_freq & corrupt_freq should be smaller than 10
     * [0 - lost_freq) is considered lost
     * [lost_freq - lost_freq+corrupt_freq) is considered corrupt
     * [lost+corrupt_freq - 10) is considered normal
     * */

    //default constructor
    public FlawedSocket(int port) throws SocketException {
        this(port, 2, 2);
    }

    public FlawedSocket(int port, int lost_freq, int corrupt_freq) throws SocketException {
        super(port);
        if(lost_freq >= 10 || corrupt_freq >= 10 || lost_freq < 0 || corrupt_freq < 0)
            throw new SocketException("Illegal frequency range");
        if(lost_freq + corrupt_freq >= 10)
            throw new SocketException("Illegal frequency range");
        this.lost_freq = lost_freq;
        this.corrupt_freq = corrupt_freq;
        random_generator = new Random();
    }

    private int nextInt(){
        return random_generator.nextInt(10);
    }

    private boolean sd_corrupt(){
        return nextInt() < lost_freq + corrupt_freq && nextInt() >= lost_freq;
    }

    private boolean sd_lost(){
        return nextInt() < lost_freq;
    }

    private boolean sd_normal(){
        return nextInt() >= lost_freq + corrupt_freq;
    }

    private DatagramPacket corrupt_pkt(DatagramPacket pkt){
        byte[] data = ByteConvertor.bytesCopy(pkt.getData(), 0, pkt.getLength() - 1);
        for(int i = 0; i < data.length && i < 3; ++i)
            data[i] = (byte)~data[i];
        return new DatagramPacket(data, 0, data.length, pkt.getAddress(), pkt.getPort());
    }

    public void send(DatagramPacket pkt) throws IOException {
        if(sd_lost()) return;
        if(sd_corrupt()){
            pkt = corrupt_pkt(pkt);
        }
        super.send(pkt);
    }

    public void receive(DatagramPacket packet) throws IOException {
        super.receive(packet);
    }
}
