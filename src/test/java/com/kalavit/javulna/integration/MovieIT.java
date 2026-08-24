package com.kalavit.javulna.integration;

import com.kalavit.javulna.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static com.kalavit.javulna.integration.TestFixtures.loginAndGetToken;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class MovieIT {

    @Autowired
    private MockMvc mockMvc;
    @Test
    public void movieSearchByDescription() throws Exception {
        mockMvc.perform(get("/rest/movie").param("description", "Luke"))
                .andExpect(status().isOk());
    }

    @Test
    public void movieSearchByGenre() throws Exception {
        mockMvc.perform(get("/rest/movie").param("genre", "Action"))
                .andExpect(status().isOk());
    }

    @Test
    public void movieSearchById() throws Exception {
        mockMvc.perform(get("/rest/movie").param("id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void movieSearchByAllParams() throws Exception {
        mockMvc.perform(get("/rest/movie")
                        .param("title", "Star")
                        .param("description", "Luke")
                        .param("genre", "Action")
                        .param("id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void sqlInjectionAttemptReturnsEmptyResult() throws Exception {
        mockMvc.perform(get("/rest/movie").param("title", "zzzzz' OR 1=1 --"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void moviesPageIsAccessible() throws Exception {
        mockMvc.perform(get("/movies")).andExpect(status().isOk());
    }

    @Test
    public void addMoviePageIsAccessible() throws Exception {
        mockMvc.perform(get("/addMovie")).andExpect(status().isOk());
    }



    @Test
    public void movieXmlWithDoctypeIsRejected() throws Exception {
        String token = loginAndGetToken(mockMvc);

        String payload = "<?xml version=\"1.0\"?>"
                + "<!DOCTYPE movie [<!ENTITY xxe SYSTEM \"http://127.0.0.1:9999/x\">]>"
                + "<movie><title>&xxe;</title><description>d</description><genre>g</genre></movie>";

        try {
            mockMvc.perform(post("/rest/moviexml")
                    .header("Authorization", "Bearer " + token)
                    .param("inputxml", payload)
                    .with(csrf()));
            fail("expected DOCTYPE to be rejected");
        } catch (Exception expected) {
            assertTrue(expected.getMessage().contains("DOCTYPE"));
        }
    }
    @Test
    public void movieXmlWithoutDoctypeIsAccepted() throws Exception {
        String token = loginAndGetToken(mockMvc);

        String payload = "<?xml version=\"1.0\"?>"
                + "<movie><title>Test XML Movie</title>"
                + "<description>from test</description><genre>SF</genre></movie>";

        mockMvc.perform(post("/rest/moviexml")
                        .header("Authorization", "Bearer " + token)
                        .param("inputxml", payload)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

}
