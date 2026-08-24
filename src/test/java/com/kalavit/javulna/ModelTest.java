package com.kalavit.javulna;

import com.kalavit.javulna.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;


public class ModelTest {

    @Test
    public void movieAccessors(){
        Movie movie  = new Movie();
        movie.setTitle("The Odyssey");
        movie.setDescription("Odyssey return from war");
        movie.setGenre("Fantasy");
        movie.setId("42");

        assertEquals("The Odyssey", movie.getTitle());
        assertEquals("Odyssey return from war", movie.getDescription());
        assertEquals("Fantasy", movie.getGenre());
        assertEquals("42", movie.getId());
    }

    @Test
    public void userAccessors() {
        User user = new User();
        user.setName("Test User");
        user.setPassword("password");
        user.setEmailAddress("test@gmail.com");
        user.setSex("Male");
        user.setMotto("motto");
        user.setWebPageUrl("webpageurl");

        assertEquals("Test User", user.getName());
        assertEquals("password", user.getPassword());
        assertEquals("test@gmail.com", user.getEmailAddress());
        assertEquals("Male", user.getSex());
        assertEquals("motto", user.getMotto());
        assertEquals("webpageurl", user.getWebPageUrl());

        // UserDetails metode
        assertEquals("Test User", user.getUsername());
        assertNull(user.getAuthorities());
        assertTrue(user.isEnabled());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isCredentialsNonExpired());

    }

    @Test
    public void refreshTokenAccessors() {
        User user = new User();
        user.setName("testuser");

        Date expiry = new Date();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("test-token-123");
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(expiry);

        assertEquals("test-token-123", refreshToken.getToken());
        assertEquals(user, refreshToken.getUser());
        assertEquals(expiry, refreshToken.getExpiryDate());
    }

    @Test
    public void messageAccessors() {
        User user = new User();
        user.setName("testuser");

        Message message = new Message();

        message.setMessage("Hello World");
        message.setAuthor(user);
        assertEquals("Hello World", message.getMessage());
        assertEquals("testuser", message.getAuthor().getName());

    }

    @Test
    public void movieObjectAccessors() {
        MovieObject object = new MovieObject();
        object.setName("Test Movie");
        object.setDescription("Odyssey return from war");

        assertEquals("Test Movie", object.getName());
        assertEquals("Odyssey return from war", object.getDescription());

    }

    @Test
    public void baseEntityAccessors() {
        Date date = new Date();
        BaseEntity entity = new BaseEntity();
        entity.setId("55");
        entity.setCreatedAt(date);
        entity.setLastUpdatedAt(date);

        assertEquals("55", entity.getId());
        assertEquals(date, entity.getCreatedAt());
        assertEquals(date, entity.getLastUpdatedAt());



    }

    @Test
    public void messageTypeValues() {
        assertEquals(2, MessageType.values().length);
        assertEquals(MessageType.mail, MessageType.valueOf("mail"));
        assertEquals(MessageType.chat, MessageType.valueOf("chat"));
    }

    @Test
    public void userEqualsAndHashCode() {
        User a = new User();
        a.setName("Test User");
        a.setSex("Male");
        a.setPassword("password");
        a.setEmailAddress("test@gmail.com");
        a.setMotto("motto");
        a.setWebPageUrl("webpageurl");

        User b = new User();
        b.setName("Test User");
        b.setSex("Male");
        b.setPassword("password");
        b.setEmailAddress("test@gmail.com");
        b.setMotto("motto");
        b.setWebPageUrl("webpageurl");

        assertTrue(a.equals(a));
        assertTrue(a.equals(b));
        assertFalse(a.equals(null));
        assertFalse(a.equals("neki string"));

        User c = new User();
        c.setName("Drugi");
        assertFalse(a.equals(c));

        User d = new User();
        d.setName("Test User");
        d.setSex("Female");
        d.setPassword("password");
        d.setEmailAddress("test@gmail.com");
        d.setMotto("motto");
        d.setWebPageUrl("webpageurl");
        assertFalse(a.equals(d));

        User e = new User();
        e.setName("Test User");
        e.setSex("Male");
        e.setPassword("drugaLozinka");
        e.setEmailAddress("test@gmail.com");
        e.setMotto("motto");
        e.setWebPageUrl("webpageurl");
        assertFalse(a.equals(e));

        User f = new User();
        f.setName("Test User");
        f.setSex("Male");
        f.setPassword("password");
        f.setEmailAddress("drugi@gmail.com");
        f.setMotto("motto");
        f.setWebPageUrl("webpageurl");
        assertFalse(a.equals(f));

        User g = new User();
        g.setName("Test User");
        g.setSex("Male");
        g.setPassword("password");
        g.setEmailAddress("test@gmail.com");
        g.setMotto("drugi motto");
        g.setWebPageUrl("webpageurl");
        assertFalse(a.equals(g));

        User h = new User();
        h.setName("Test User");
        h.setSex("Male");
        h.setPassword("password");
        h.setEmailAddress("test@gmail.com");
        h.setMotto("motto");
        h.setWebPageUrl("drugi-url");
        assertFalse(a.equals(h));

        assertEquals(a.hashCode(), b.hashCode());
    }


}
