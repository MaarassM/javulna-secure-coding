package com.kalavit.javulna.utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Arrays;
import java.util.List;


public class WhitelistObjectInputStream  extends ObjectInputStream {
    private static final List<String> ALLOWED_CLASSES = Arrays.asList(
            "com.kalavit.javulna.model.User",
            "com.kalavit.javulna.model.BaseEntity"
    );
    public WhitelistObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        if (!ALLOWED_CLASSES.contains(desc.getName())) {
            throw new SecurityException("Blocked deserialization of class: " + desc.getName());
        }
        return super.resolveClass(desc);
    }
}