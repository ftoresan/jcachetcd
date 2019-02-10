package com.toresan.jcachetcd.util;

import com.google.protobuf.ByteString;

import java.io.*;

public class TransportConverter {

    private TransportConverter() {
    }

    public static ByteString toByteString(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            return ByteString.copyFrom(bos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Could not convert object " + obj, e);
        }
    }

    public static <T> T toObject(ByteString bs) {
        ObjectInput in = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bs.toByteArray())) {
            in = new ObjectInputStream(bis);
            return (T) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Could not convert bytes " + bs, e);
        }
    }
}
