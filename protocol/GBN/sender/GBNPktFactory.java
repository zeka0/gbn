package protocol.GBN.sender;

import protocol.GBN.GBNPkt;

import java.util.ArrayDeque;

/**
 * Created by AlienX
 */

/**
 * In GBN, no need for ack number when sending pkt from sender to receiver
 * Pkt sequence number start from 0
 * Window size should divide max package size
 * */
public class GBNPktFactory {
    public static class NoAvailableDataException extends Exception{
        public NoAvailableDataException(String message){
            super(message);
        }
    }

    private byte[] orign_data;

    /**
     * ws_pkts may have a size smaller than window_size
     * */
    private GBNPkt[] ws_pkts;
    private int index;//the index of present ws_pkts[0] index

    private int base;
    private int top;//window size/maxPktSize(may smaller)

    private final int winSize;
    private final int maxPktSize;

    public GBNPktFactory(byte[] orign_data, int winSize, int maxPktSize){
        base = 0;
        top = 0;
        index = 0 - winSize;//in order to compatible with slide-window;
        this.orign_data = orign_data;
        this.winSize = winSize;
        this.maxPktSize = maxPktSize;
        int maxLen = (int) Math.ceil(winSize / maxPktSize);
        ws_pkts = new GBNPkt[maxLen];
    }

    public int get_winSize(){
        return winSize;
    }

    public int get_maxPktSize(){
        return maxPktSize;
    }

    private GBNPkt mk_pkt(int s, int e, int seq){
        byte[] bytes = new byte[e - s + 1];
        for(int i = 0; i < bytes.length; ++i)
            bytes[i] = orign_data[i + s];
        return new GBNPkt(seq, 0, bytes);
    }

    public synchronized boolean isSlideToEnd(){
        return index + winSize > orign_data.length;
    }

    public void slide_window(){
        if (index + winSize > orign_data.length)
            return;
        index = index + winSize;
        base = 0;
        //top is the top sequence number in this window
        //top = ... - 1 is to compensate that the base is 0 not 1
        if (index + winSize > orign_data.length) {
            top = (int) Math.ceil((orign_data.length - index) / (double)maxPktSize) - 1;
        } else {
            top = (int) Math.ceil(winSize / (double)maxPktSize) - 1;
        }

        for (int i = base; i <= top; ++i) {
            if((i + 1) * maxPktSize + index - 1 > orign_data.length)
                ws_pkts[i] = mk_pkt(i * maxPktSize + index, orign_data.length - 1, i);
            else ws_pkts[i] = mk_pkt(i * maxPktSize + index, (i + 1) * maxPktSize + index - 1, i);
        }
    }

    public void ack_pkt_num(int ack_num){
        base = ack_num + 1;
    }

    public int get_base(){
        return base;
    }

    public int get_top(){
        return top;
    }

    public GBNPkt get_pktAt(int index){
        if(index < base || index > top)
            throw new ArrayIndexOutOfBoundsException("Out of legal package bound");
        return ws_pkts[index];
    }
}
