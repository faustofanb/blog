package com.minetty;

import com.minetty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        ByteBuf buf = ByteBuf.allocate(32);
        buf.writeString("hello", StandardCharsets.UTF_8);
        String s = buf.readString(5, StandardCharsets.UTF_8);
        System.out.println("ByteBuf OK: " + s);
    }
}

