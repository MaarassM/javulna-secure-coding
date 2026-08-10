package com.kalavit.javulna.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class RefreshToken extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String token;

    @ManyToOne
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
