package protocol.GBN;

/**
 * Created by AlienX
 */
public class GBNProtocol {
    public byte[] getData(byte[] pkg){
        return null;
    }

    public int getAckNum(byte[] pkg){
        return 0;
    }

    public boolean isCorrupt(byte[] pkg){
        return false;
    }

    public byte[] mkPkg(byte[] pkg_data){
        return null;
    }

    public byte[][] mkPkgs(byte[] data,
                           int windowSize, int pkgDataSize){
        return null;
    }

    public static void main(String[] args){
        //for test purpose
    }
}
