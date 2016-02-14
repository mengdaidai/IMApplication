package com.daidai.im.util;

/**
 * Created by songs on 2015/12/30.
 */
public class Util {

    public static byte[] grow(byte[] src, int size) {
        byte[] tmp = new byte[src.length + size];
        System.arraycopy(src, 0, tmp, 0, src.length);
        return tmp;
    }

    public static int getLength(byte[] data){
        int length = data[3] & 0xFF;
        // System.out.println("getLength:"+length);
        length|=((data[2] << 8) & 0xFF00);
        //System.out.println("getLength:"+length);
        length|=((data[1] << 16) & 0xFF0000);
        // System.out.println("getLength:"+length);
        length|=((data[0] << 24) & 0xFF000000);
        //System.out.println("getLength:"+length);
        return length;

    }

    public static byte[] longToBytes( long value )
    {
        byte[] src = new byte[8];
        src[0] =  (byte) ((value>>56) & 0xFF);
        src[1] =  (byte) ((value>>48) & 0xFF);
        src[2] =  (byte) ((value>>40) & 0xFF);
        src[3] =  (byte) ((value>>32) & 0xFF);
        src[4] =  (byte) ((value>>24) & 0xFF);
        src[5] =  (byte) ((value>>16) & 0xFF);
        src[6] =  (byte) ((value>>8) & 0xFF);
        src[7] =  (byte) (value & 0xFF);
        return src;
    }

    public static int bytesToLong(byte[] bytes) {
        int number = bytes[7] & 0xFF;
        number |= ((bytes[6] << 8) & 0xFF00);
        number |= ((bytes[5] << 16) & 0xFF0000);
        number |= ((bytes[4] << 24) & 0xFF000000);
        number |= ((bytes[3] << 32) & 0xFF00000000l);
        number |= ((bytes[2] << 40) & 0xFF0000000000l);
        number |= ((bytes[1] << 48) & 0xFF000000000000l);
        number |= ((bytes[0] << 56) & 0xFF00000000000000l);
        return number;
    }

    public static byte[] intToByte(int data){
        byte[] b = new byte[1];
        b[0] =  (byte)((data<<24)>>24);
        System.out.println("byte:"+b[0]);
        return b;
    }

    public static byte[] intToBytes(int data){
        System.out.println("data:"+data);
        byte[] b = new byte[4];

        b[3] = (byte)((data<<24)>>24);
        System.out.println("intToByte:"+b[0]);
        b[2] = (byte)((data <<16)>> 24);
        System.out.println("intToByte:"+b[1]);
        b[1] = (byte)((data << 8)>>24);
        System.out.println("intToByte:"+b[2]);
        b[0] = (byte)(data >> 24);
        System.out.println("intToByte:"+b[3]);
        return b;
    }
}
