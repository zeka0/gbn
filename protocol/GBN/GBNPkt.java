package protocol.GBN;

import protocol.ByteConvertor;

/**
 * Created by AlienX
 */
public class GBNPkt {
    private int seq_number;//4 bytes 0-3
    private int ack_number;//4 bytes 4-7
    private short inet_checksum;//2 bytes of checksum 8-9
    private byte[] data;
    private static final int HEAD_LENGTH = 10;//10 bytes

    private byte[] bytes = null;//pre-buffered bytes

    public GBNPkt(int seq_number, int ack_number,
                  byte[] data){
        this(seq_number, ack_number,
                compute_checksum(seq_number, ack_number, data), data);
    }

    private GBNPkt(int seq_number, int ack_number,
                   short inet_checksum,
                   byte[] data){
        this.seq_number = seq_number;
        this.ack_number = ack_number;
        this.inet_checksum = inet_checksum;
        this.data = data;
    }

    private void clear_bytes(){
        //force toBytes to calculate bytes
        bytes = null;
    }

    public byte[] toBytes(){
        /*
        * Write in the sequence of
        * seq_number, ack_number, checksum., data
        * */
        if(bytes != null){
            //Use pre-computed bytes to save time
            return bytes;
        }
        bytes = new byte[HEAD_LENGTH + data.length];
        byte[] seq_bytes = ByteConvertor.int2bytes(seq_number);
        byte[] ack_bytes = ByteConvertor.int2bytes(ack_number);
        byte[] cm_bytes = ByteConvertor.short2bytes(inet_checksum);
        int offset = 0;

        for(int i = offset; i < seq_bytes.length + offset; ++ i)
            bytes[i] = seq_bytes[i - offset];
        offset += seq_bytes.length;

        for(int i = offset; i < ack_bytes.length + offset; ++ i)
            bytes[i] = ack_bytes[i - offset];
        offset += ack_bytes.length;

        for(int i = offset; i < cm_bytes.length + offset; ++i)
            bytes[i] = cm_bytes[i - offset];
        offset += cm_bytes.length;

        for(int i = offset; i < data.length + offset; ++i)
            bytes[i] = data[i - offset];
        return bytes;
    }

    public int get_seq_number(){
        return seq_number;
    }

    public int get_ack_number(){
        return ack_number;
    }

    public byte[] get_data(){
        return data;
    }

    private static short compute_checksum(int seq_number, int ack_number,
                                           byte[] data){
        GBNPkt pkt = new GBNPkt(seq_number, ack_number, (short)0, data);//create a dummy packet containing 0 checksum
        return compute_checksum(pkt);
    }

   private static short compute_checksum(byte[] bytes){
        //for debug purpose
        int offset = 0;
        short sum = 0;
        for(int i = 0; i < bytes.length/2; ++i){
            sum += ByteConvertor.bytes2short(bytes, offset);
            offset += 2;
        }
        sum = (short)~sum;
        return sum;
    }

    public static short compute_checksum(GBNPkt pkt) {
        //Used to determine whether a packet is corrupted or not
        /*
        * First divide pkt into 2 bytes chunks
        * Convert each chunk to a short
        * Sum up this short
        * Negate this short and return it
        * */
        byte[] bytes = pkt.toBytes();
        //clear the original checksum
        bytes[8] = 0;
        bytes[9] = 0;
        short cm = compute_checksum(bytes);
        pkt.clear_bytes();
        return cm;
    }

    public static GBNPkt buildFromBytes(byte[] pkt){
        /*
        * Caution!
        * This builds the original packet, regardless of the correctness of checksum
        * */
        int offset = 0;

        int seq_number = ByteConvertor.bytes2int(pkt, offset);
        offset += 4;

        int ack_number = ByteConvertor.bytes2int(pkt, offset);
        offset += 4;

        short cm = ByteConvertor.bytes2short(pkt, offset);
        offset += 2;

        byte[] data = ByteConvertor.bytesCopy(pkt, offset, pkt.length - 1);

        return new GBNPkt(seq_number, ack_number,
                cm, data);
    }

    public static boolean isCorrupted(GBNPkt pkt){
        /*
        * true if corrupted
        * */
        short cm = compute_checksum(pkt);
        return cm != pkt.inet_checksum;
    }

    public static void main(String[] args){
        System.out.println("Test check sum computation");
        byte[] bytes = {(byte)136, (byte)136, (byte)136, (byte)136};
        System.out.println(compute_checksum(bytes));
    }
}
