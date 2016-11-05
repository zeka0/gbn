package protocol.GBN.receiver;

/**
 * Created by AlienX
 */
/**
 * rebuild the data from packets received
 * */
public class GBNPktBuilder {
    private byte[] data;
    private int index;
    public GBNPktBuilder(int dataLength){
        data = new byte[dataLength];
        index = 0;
    }

    public void appendData(byte[] buffer){
        for(int i = 0; i < buffer.length; ++i) {
            if(index + i >= data.length) break;
            data[index + i] = buffer[i];
        }
        index += buffer.length;
    }

    public byte[] get_data(){
        return data;
    }

    public boolean isFinish(){
        return index >= data.length;
    }
}
