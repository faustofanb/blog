package com.minetty.buffer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Minimal byte buffer abstraction with reader/writer indices.
 */
public interface ByteBuf {
    int readableBytes();
    int writableBytes();

    int readerIndex();
    int writerIndex();
    ByteBuf readerIndex(int newIndex);
    ByteBuf writerIndex(int newIndex);

    ByteBuf writeBytes(byte[] src);
    ByteBuf writeByte(int value);
    ByteBuf writeInt(int value);
    ByteBuf writeString(String value, Charset charset);

    byte readByte();
    int readInt();
    ByteBuf readBytes(byte[] dst);
    String readString(int length, Charset charset);

    ByteBuffer nioBuffer();
    byte[] array();

    static ByteBuf allocate(int capacity) {
        return new SimpleByteBuf(capacity);
    }
}

