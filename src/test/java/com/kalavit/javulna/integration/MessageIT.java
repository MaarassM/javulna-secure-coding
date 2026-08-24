package com.kalavit.javulna.integration;


import com.kalavit.javulna.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class MessageIT {

    @Autowired
    private MockMvc mockMvc;

    private String loginAndGetToken() throws Exception {
        String response = mockMvc.perform(post("/rest/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Yoda\",\"password\":\"NoSecretsATrueJediHas\"}"))
                .andReturn().getResponse().getContentAsString();

        return response.split("\"accessToken\":\"")[1].split("\"")[0];
    }

    @Test
    public void chatAllWithoutTokenIsRejected() throws Exception {
        mockMvc.perform(get("/rest/messages/chatAll"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void chatAllWithTokenReturnsOk() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(get("/rest/messages/chatAll")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    public void chatWithOtherUserReturnsOk() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(get("/rest/messages/chat")
                        .header("Authorization", "Bearer " + token)
                        .param("otherUser", "Darth Vader"))
                .andExpect(status().isOk());
    }

    @Test
    public void sendChatMessageWithToken() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(put("/rest/messages/chat")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Hello there\",\"toUser\":\"Darth Vader\"}"))
                .andExpect(status().isOk());
    }

}
