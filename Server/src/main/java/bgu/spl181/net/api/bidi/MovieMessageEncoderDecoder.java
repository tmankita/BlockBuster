package bgu.spl181.net.api.bidi;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import bgu.spl181.net.api.MessageEncoderDecoder;

public class MovieMessageEncoderDecoder implements MessageEncoderDecoder<String> {
	// local variables
	 private byte[] bytes = new byte[1 << 10];
	    private int len = 0;

	/**
	 * this method decode the given array of bytes into a string in case which we got the full message
	 * otherwise, it collects the bytes until it full,
	 * @return - the decoded String if the message is full. Null, otherwise. 
	 */
	@Override
	public String decodeNextByte(byte nextByte) {
		// in case which we received the all message
        if (nextByte == '\n') {
            return popString();
        }
        // otherwise, push it into array of bytes
        pushByte(nextByte);
        return null;
	}

	/**
	 * decodes the given array of bytes into String
	 * @return - the decoded String
	 */
	private String popString() {
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
	}

	/**
	 * push byte into the array. If the array is full, creates an array doubled in size.
	 * @param nextByte
	 */
	private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;	
	}

	/**
	 * Encodes the String into array of bytes
	 * @return the array of bytes
	 */
	@Override
	public byte[] encode(String message) {
		return (message+"\n").getBytes();
	}
}
