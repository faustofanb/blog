package com.minetty.buffer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Simple heap-based ByteBuf implementation for educational purposes.
 */
final class SimpleByteBuf implements ByteBuf {
    private final byte[] buffer;
    private final ByteBuffer byteBufferView;
    private int readerIndex;
    private int writerIndex;

    SimpleByteBuf(int capacity) {
        this.buffer = new byte[capacity];
        this.byteBufferView = ByteBuffer.wrap(buffer);
        this.readerIndex = 0;
        this.writerIndex = 0;
    }

    @Override
    public int readableBytes() {
        return writerIndex - readerIndex;
    }

    @Override
    public int writableBytes() {
        return buffer.length - writerIndex;
    }

    @Override
    public int readerIndex() {
        return readerIndex;
    }

    @Override
    public int writerIndex() {
        return writerIndex;
    }

    @Override
    public ByteBuf readerIndex(int newIndex) {
        if (newIndex < 0 || newIndex > writerIndex) {
            throw new IndexOutOfBoundsException("readerIndex out of bounds");
        }
        this.readerIndex = newIndex;
        this.byteBufferView.position(readerIndex);
        return this;
    }

    @Override
    public ByteBuf writerIndex(int newIndex) {
        if (newIndex < readerIndex || newIndex > buffer.length) {
            throw new IndexOutOfBoundsException("writerIndex out of bounds");
        }
        this.writerIndex = newIndex;
        this.byteBufferView.limit(writerIndex);
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        Objects.requireNonNull(src, "src");
        ensureWritable(src.length);
        System.arraycopy(src, 0, buffer, writerIndex, src.length);
        writerIndex += src.length;
        byteBufferView.limit(writerIndex);
        return this;
    }

    @Override
    public ByteBuf writeByte(int value) {
        ensureWritable(1);
        buffer[writerIndex++] = (byte) value;
        byteBufferView.limit(writerIndex);
        return this;
    }

    @Override
    public ByteBuf writeInt(int value) {
        ensureWritable(4);
        buffer[writerIndex++] = (byte) (value >>> 24);
        buffer[writerIndex++] = (byte) (value >>> 16);
        buffer[writerIndex++] = (byte) (value >>> 8);
        buffer[writerIndex++] = (byte) (value);
        byteBufferView.limit(writerIndex);
        return this;
    }

    @Override
    public ByteBuf writeString(String value, Charset charset) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(charset, "charset");
        return writeBytes(value.getBytes(charset));
    }

    @Override
    public byte readByte() {
        ensureReadable(1);
        byte b = buffer[readerIndex++];
        byteBufferView.position(readerIndex);
        return b;
    }

    @Override
    public int readInt() {
        ensureReadable(4);
        int v = ((buffer[readerIndex++] & 0xFF) << 24)
              | ((buffer[readerIndex++] & 0xFF) << 16)
              | ((buffer[readerIndex++] & 0xFF) << 8)
              | ((buffer[readerIndex++] & 0xFF));
        byteBufferView.position(readerIndex);
        return v;
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        Objects.requireNonNull(dst, "dst");
        ensureReadable(dst.length);
        System.arraycopy(buffer, readerIndex, dst, 0, dst.length);
        readerIndex += dst.length;
        byteBufferView.position(readerIndex);
        return this;
    }

    @Override
    public String readString(int length, Charset charset) {
        Objects.requireNonNull(charset, "charset");
        ensureReadable(length);
        String s = new String(buffer, readerIndex, length, charset);
        readerIndex += length;
        byteBufferView.position(readerIndex);
        return s;
    }

    @Override
    public ByteBuffer nioBuffer() {
        byteBufferView.position(readerIndex).limit(writerIndex);
        return byteBufferView;
    }

    @Override
    public byte[] array() {
        return buffer;
    }

    private void ensureWritable(int minWritableBytes) {
        if (writableBytes() < minWritableBytes) {
            throw new IndexOutOfBoundsException("Not enough writable bytes: required=" + minWritableBytes);
        }
    }

    private void ensureReadable(int minReadableBytes) {
        if (readableBytes() < minReadableBytes) {
            throw new IndexOutOfBoundsException("Not enough readable bytes: required=" + minReadableBytes);
        }
    }
}

