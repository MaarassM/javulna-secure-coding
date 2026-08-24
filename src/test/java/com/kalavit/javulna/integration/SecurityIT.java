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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import javax.servlet.http.Cookie;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class SecurityIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void unauthenticatedAccessToRestUserReturns401() throws Exception {
        mockMvc.perform(get("/rest/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void publicMovieEndpointIsAccessible() throws Exception {
        mockMvc.perform(get("/rest/movie"))
                .andExpect(status().isOk());
    }

    @Test
    public void movieSearchByTitleReturnsResults() throws Exception {
        mockMvc.perform(get("/rest/movie").param("title", "Star"))
                .andExpect(status().isOk());
    }

    @Test
    public void loginReturnsTokens() throws Exception {
        mockMvc.perform(post("/rest/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Yoda\",\"password\":\"NoSecretsATrueJediHas\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void helloEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void logoutInvalidatesRefreshToken() throws Exception {
        String response = mockMvc.perform(post("/rest/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Yoda\",\"password\":\"NoSecretsATrueJediHas\"}"))
                .andReturn().getResponse().getContentAsString();

        String refreshToken = response.split("\"refreshToken\":\"")[1].split("\"")[0];

        mockMvc.perform(post("/rest/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/rest/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void invalidSerializedCookieIsRejected() throws Exception {
        try {
            mockMvc.perform(get("/rest/movie")
                    .cookie(new Cookie("USER_AUTHENTICATION_EXTRA_SECURITY", "YWJj")));
            fail("expected SecurityException for invalid magic bytes");
        } catch (Exception expected) {
            assertTrue(expected.getMessage().contains("Not a serialized"));
        }
    }

    @Test
    public void serializedArrayListCookieIsRejected() throws Exception {
        try {
            mockMvc.perform(get("/rest/movie")
                    .cookie(new Cookie("USER_AUTHENTICATION_EXTRA_SECURITY",
                            "rO0ABXNyABRqYXZhLnV0aWwuTGlua2VkTGlzdAwpU11KYIgiAwAAeHB3BAAAAAB4")));
            fail("expected SecurityException for non-whitelisted class");
        } catch (Exception expected) {
            assertTrue(expected.getMessage().contains("Blocked deserialization"));
        }
    }

    @Test
    public void formLoginSucceeds() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "Yoda")
                        .param("password", "NoSecretsATrueJediHas")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    public void formLoginWithWrongPasswordReturns401() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "Yoda")
                        .param("password", "WrongPassword")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }


}