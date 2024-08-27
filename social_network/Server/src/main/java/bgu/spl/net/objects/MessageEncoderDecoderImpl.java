package bgu.spl.net.objects;

import bgu.spl.net.api.MessageEncoderDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessageEncoderDecoderImpl<T> implements MessageEncoderDecoder {
    private byte[] bytes = new byte[1 << 10];
    private int len = 0;
    private short opcode = -1;

    @Override
    public String decodeNextByte(byte nextByte) {
        if (nextByte == '\0' & (len != 0 | opcode != -1)) {
            pushByte((byte) ' '); //push space between arguments
        } else if (nextByte == ';') {
            String toSend = popString();
            opcode = -1;
            return toSend;
        } else pushByte(nextByte); //push 0 for opcode
        if (len == 2 & opcode == -1) {
            opcode = bytesToShort(bytes);
            len = 0;
        }
        return null;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    private String popString() {
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        bytes = new byte[1 << 10];
        return opcode + " " + result;
    }

    public short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    @Override
    public byte[] encode(Object message) {
        String[] args = message.toString().split(" ");
        String msg = ";";
        if(args.length != 2)
            msg = message.toString().substring(args[0].length() + args[1].length() + 2) + msg;
        byte[] msgBytes = (msg).getBytes();
        byte[] bytesArr = new byte[msgBytes.length + 4];
        shortToBytes(Short.parseShort(args[0]), bytesArr, 0);
        shortToBytes(Short.parseShort(args[1]), bytesArr, 2);
        for (int i = 0; i < msgBytes.length; i++) {
            bytesArr[i+4] = msgBytes[i];
        }
        return bytesArr;
    }

    public void shortToBytes(short num, byte[] bytesArr, int index) {
        bytesArr[index] = (byte) ((num >> 8) & 0xFF);
        bytesArr[index + 1] = (byte) (num & 0xFF);
    }
}
