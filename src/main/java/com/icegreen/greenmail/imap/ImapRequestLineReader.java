/* -------------------------------------------------------------------
 * Copyright (c) 2006 Wael Chatila / Icegreen Technologies. All Rights Reserved.
 * This software is released under the LGPL which is available at http://www.gnu.org/copyleft/lesser.html
 * This file has been modified by the copyright holder. Original file can be found at http://james.apache.org
 * -------------------------------------------------------------------
 */
package com.icegreen.greenmail.imap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;

/**
 * Wraps the client input reader with a bunch of convenience methods, allowing lookahead=1
 * on the underlying character stream.
 * TODO need to look at encoding, and whether we should be wrapping an InputStream instead.
 *
 * @author Darrell DeBoer <darrell@apache.org>
 * @version $Revision: 109034 $
 */
public class ImapRequestLineReader {
    private OutputStream output;

    private boolean nextSeen = false;
    private char nextChar; // unknown
    private InputStream input;

	private CharsetDecoder utf8Decoder;
	private CharsetDecoder asciiDecoder;

    ImapRequestLineReader(InputStream input, OutputStream output) {
    	utf8Decoder = Charset.forName("UTF-8").newDecoder();
    	asciiDecoder = Charset.forName("ASCII").newDecoder().onUnmappableCharacter(CodingErrorAction.REPORT);
        this.input = input;
        this.output = output;
    }

    /**
     * Reads the next regular, non-space character in the current line. Spaces are skipped
     * over, but end-of-line characters will cause a {@link ProtocolException} to be thrown.
     * This method will continue to return
     * the same character until the {@link #consume()} method is called.
     *
     * @return The next non-space character.
     * @throws ProtocolException If the end-of-line or end-of-stream is reached.
     */
    public char nextWordChar() throws ProtocolException {
        char next = nextChar();
        while (next == ' ') {
            consume();
            next = nextChar();
        }

        if (next == '\r' || next == '\n') {
            throw new ProtocolException("Missing argument.");
        }

        return next;
    }

    /**
     * Reads the next character in the current line. This method will continue to return
     * the same character until the {@link #consume()} method is called.
     *
     * @return The next character.
     * @throws ProtocolException If the end-of-stream is reached.
     */
    public char nextChar() throws ProtocolException {
        if (!nextSeen) {
            int next = -1;

            try {
            	byte[] byteArray = new byte[1];
            	int nbRead = input.read(byteArray);
            	if (nbRead > 0) {
            		try {
            			CharBuffer asciiChar = asciiDecoder.decode(ByteBuffer.wrap(byteArray));
            			next = asciiChar.get();
            		} catch (MalformedInputException e) {
            			next = nextCharUTF8(next, byteArray);
					}
            	} else {
					next = -1;
            	}
            	
            } catch (IOException e) {
//                e.printStackTrace();
                throw new ProtocolException("Error reading from stream.");
            }
            if (next == -1) {
                throw new ProtocolException("Unexpected end of stream.");
            }

            nextSeen = true;
            nextChar = (char) next;
//            System.out.println( "Read '" + nextChar + "'" );
        }
        return nextChar;
    }

	private int nextCharUTF8(int next, byte[] byteArray) throws IOException, CharacterCodingException {
		int nbRead;
		byte[] maxBytes = new byte[4];
		maxBytes[0] = byteArray[0];
		for (int index = 1; index < maxBytes.length; ++index) {
			nbRead = input.read(byteArray);
			if (nbRead > 0) {
				maxBytes[index] = byteArray[0];
				try {
					CharBuffer unicodeChar = utf8Decoder.decode(ByteBuffer.wrap(maxBytes, 0, index + 1));
					next = unicodeChar.get();
					break;
				} catch (UnmappableCharacterException unicodeException) {
					//just loop one more time
				}
			} else {
				next = -1;
			}
		}
		return next;
	}

    /**
     * Moves the request line reader to end of the line, checking that no non-space
     * character are found.
     *
     * @throws ProtocolException If more non-space tokens are found in this line,
     *                           or the end-of-file is reached.
     */
    public void eol() throws ProtocolException {
        char next = nextChar();

        // Ignore trailing spaces.
        while (next == ' ') {
            consume();
            next = nextChar();
        }

        // handle DOS and unix end-of-lines
        if (next == '\r') {
            consume();
            next = nextChar();
        }

        // Check if we found extra characters.
        if (next != '\n') {
            // TODO debug log here and other exceptions
            throw new ProtocolException("Expected end-of-line, found more characters.");
        }
    }

    /**
     * Consumes the current character in the reader, so that subsequent calls to the request will
     * provide a new character. This method does *not* read the new character, or check if
     * such a character exists. If no current character has been seen, the method moves to
     * the next character, consumes it, and moves on to the subsequent one.
     *
     * @throws ProtocolException if a the current character can't be obtained (eg we're at
     *                           end-of-file).
     */
    public char consume() throws ProtocolException {
        char current = nextChar();
        nextSeen = false;
        nextChar = 0;
        return current;
    }


    /**
     * Reads and consumes a number of characters from the underlying reader,
     * filling the char array provided.
     *
     * @param holder A char array which will be filled with chars read from the underlying reader.
     * @throws ProtocolException If a char can't be read into each array element.
     */
    public void read(byte[] holder) throws ProtocolException {
        int readTotal = 0;
        try {
            while (readTotal < holder.length) {
                int count = 0;
                count = input.read(holder, readTotal, holder.length - readTotal);
                if (count == -1) {
                    throw new ProtocolException("Unexpectd end of stream.");
                }
                readTotal += count;
            }
            // Unset the next char.
            nextSeen = false;
            nextChar = 0;
        } catch (IOException e) {
            throw new ProtocolException("Error reading from stream.");
        }

    }

    /**
     * Sends a server command continuation request '+' back to the client,
     * requesting more data to be sent.
     */
    public void commandContinuationRequest()
            throws ProtocolException {
        try {
            output.write('+');
            output.write('\r');
            output.write('\n');
            output.flush();
        } catch (IOException e) {
            throw new ProtocolException("Unexpected exception in sending command continuation request.");
        }
    }

    public void consumeLine()
            throws ProtocolException {
        char next = nextChar();
        while (next != '\n') {
            consume();
            next = nextChar();
        }
        consume();
    }
}
