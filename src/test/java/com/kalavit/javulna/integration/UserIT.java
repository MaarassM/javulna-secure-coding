package com.kalavit.javulna.integration;

import com.kalavit.javulna.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


import static com.kalavit.javulna.integration.TestFixtures.loginAndGetToken;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class UserIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getUsersWithTokenReturnsList() throws Exception {
        String token = loginAndGetToken(mockMvc);

        mockMvc.perform(get("/rest/user")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    public void createUserWithTokenSucceeds() throws Exception {
        String token = loginAndGetToken(mockMvc);

        mockMvc.perform(put("/rest/user")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"NewUser\",\"password\":\"pass12345\","
                                + "\"emailAddress\":\"new@example.com\",\"sex\":\"m\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void createUserWithoutTokenIsRejected() throws Exception {
        mockMvc.perform(put("/rest/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Attacker\",\"password\":\"pass12345\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void changePasswordWithWrongOldPasswordFails() throws Exception {
        String token = loginAndGetToken(mockMvc);

        mockMvc.perform(post("/rest/user/password")
                        .header("Authorization", "Bearer " + token)
                        .param("user", "Yoda")
                        .param("oldPassword", "WrongPassword")
                        .param("newPassword", "NewPass123"))
                .andExpect(status().isOk());
    }


    @Test
    public void changePasswordWithCorrectOldPasswordSucceeds() throws Exception {
        String token = loginAndGetToken(mockMvc);

        mockMvc.perform(post("/rest/user/password")
                        .header("Authorization", "Bearer " + token)
                        .param("user", "Yoda")
                        .param("oldPassword", "NoSecretsATrueJediHas")
                        .param("newPassword", "TempPass123")
                        .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/rest/user/password")
                        .header("Authorization", "Bearer " + token)
                        .param("user", "Yoda")
                        .param("oldPassword", "TempPass123")
                        .param("newPassword", "NoSecretsATrueJediHas")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    public void modifyUserWithToken() throws Exception {
        String token = loginAndGetToken(mockMvc);

        mockMvc.perform(post("/rest/user")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"3\",\"name\":\"Princess Leia\","
                                + "\"password\":\"IwishIhaveChoosenTheWookieInstead\","
                                + "\"emailAddress\":\"lea@lucasarts.com\",\"sex\":\"f\","
                                + "\"motto\":\"Updated from test\"}")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

}