package protocol;

/**
 * Created by AlienX
 */
public class ByteConvertor {
    private static final int mask = 0xff;

    public static byte[] int2bytes(int integer){
        //convert an integer to byte[4]
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) (integer >>> (24 - i * 8));
        }
        return bytes;
    }

    public static byte[] short2bytes(short s){
        //convert an short to byte[2]
        byte[] bytes = new byte[2];
        for (int i = 0; i < 2; i++) {
            bytes[i] = (byte) (s >>> (8 - i * 8));
        }
        return bytes;
    }

    public static int bytes2int(byte[] bytes){
        if(bytes.length != 4)
            throw new IllegalArgumentException("Wrong number of bytes to convert to int");
        return bytes2int(bytes, 0);
    }

    public static short bytes2short(byte[] bytes){
        if(bytes.length != 2)
            throw new IllegalArgumentException("Wrong number of bytes to convert to short");
        return bytes2short(bytes, 0);
    }

    public static int bytes2int(byte[] bytes, int offset){
        //convert bytes to int
        if(bytes.length < offset + 4)
            throw new IllegalArgumentException("Not enough number of bytes to convert to int");

        int tmp = 0;
        int n = 0;
        for(int i = offset; i < 4 + offset; ++ i){
            n <<= 8;//left shift n
            tmp = bytes[i] & mask;//convert bytes[i] to int
            n |= tmp;//binary add tmp and n
        }
        return n;
    }

    public static short bytes2short(byte[] bytes, int offset){
        //convert bytes to short
        if(bytes.length < offset + 2)
            throw new IllegalArgumentException("Not enough number of bytes to convert to int");

        return (short) (bytes[1 + offset] & mask | (bytes[offset] & mask) << 8);
    }

    public static byte[] bytesCopy(byte[] bytes, int start, int end){
        byte[] subBytes = new byte[end - start + 1];
        for(int i = start; i <= end; ++i)
            subBytes[i - start] = bytes[i];

        return subBytes;
    }

    public static void main(String[] args){
        System.out.println("Test int, short, bytes conversion");
        byte[] intbytes = {1,1,1,1};
        byte[] shortbytes = {1,1};
        int i = bytes2int(intbytes);
        short s = bytes2short(shortbytes);
        byte[] intbytes2 = int2bytes(i);
        byte[] shortbytes2 = short2bytes(s);

        System.out.println("Integer: " + i);
        System.out.println("Short: " + s);
        System.out.println("Use debug mode to observe the data of intbytes2 and shortbytes2");
    }
}
