package com.kalavit.javulna;

import com.kalavit.javulna.utils.SerializationUtil;
import org.junit.Test;
import static org.junit.Assert.fail;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import com.kalavit.javulna.model.User;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class SerializationUtilTest {
    @Test
    public void tooShortInputIsRejected() {
        try {
            SerializationUtil.readUserFromFile(new byte[]{1, 2});
            fail("expected SecurityException for input shorter than 4 bytes");
        } catch (SecurityException expected) {
            // ocekivano
        }
    }

    @Test
    public void invalidMagicBytesAreRejected() {
        byte[] notSerialized = "hello world".getBytes();
        try {
            SerializationUtil.readUserFromFile(notSerialized);
            fail("expected SecurityException for invalid magic bytes");
        } catch (SecurityException expected) {
            // ocekivano
        }
    }
    @Test
    public void whitelistBlocksUnknownClass() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(new ArrayList<String>());
        out.flush();

        try {
            SerializationUtil.readUserFromFile(baos.toByteArray());
            fail("expected SecurityException for non-whitelisted class");
        } catch (SecurityException expected) {
        }
    }

    @Test
    public void whitelistAllowsUser() throws Exception {
        User user = new User();
        user.setName("testuser");

        byte[] serialized = SerializationUtil.serialize(user);
        Object result = SerializationUtil.readUserFromFile(serialized);

        assertNotNull(result);
        assertEquals("testuser", ((User) result).getName());
    }



}