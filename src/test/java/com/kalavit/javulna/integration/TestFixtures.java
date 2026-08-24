package com.kalavit.javulna.integration;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TestFixtures {

    public static String loginAndGetToken(MockMvc mockMvc) throws Exception {
        String response = mockMvc.perform(post("/rest/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Yoda\",\"password\":\"NoSecretsATrueJediHas\"}"))
                .andReturn().getResponse().getContentAsString();

        return response.split("\"accessToken\":\"")[1].split("\"")[0];
    }
}