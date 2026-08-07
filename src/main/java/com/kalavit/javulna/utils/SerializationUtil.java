/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kalavit.javulna.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peti
 */
public class SerializationUtil {

    private static final Logger LOG = LoggerFactory.getLogger(SerializationUtil.class);

    public static byte[] serialize(Object o) {

        ObjectOutputStream out = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(baos);
            out.writeObject(o);
            out.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException ex) {
                LOG.error("Error during serialize", ex);
            }
        }
    }

    public static Object readUserFromFile(byte[] byteArray) {
        if (byteArray==null || byteArray.length < 4){
            throw new SecurityException("Not a serialized object, input too short");
        }
        if (byteArray[0] != (byte) 0xAC || byteArray[1] != (byte) 0xED
                || byteArray[2] != 0x00 || byteArray[3] != 0x05) {
            throw new SecurityException("Not a serialized Java object: invalid magic bytes");
        }
        ObjectInputStream ist;
        try {
            ist = new WhitelistObjectInputStream(new ByteArrayInputStream(byteArray));
            Object obj = ist.readObject();
            return obj;
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }

    }

}
