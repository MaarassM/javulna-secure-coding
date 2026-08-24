package com.kalavit.javulna.integration;

import com.kalavit.javulna.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.kalavit.javulna.integration.TestFixtures.loginAndGetToken;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class FileIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void uploadWithoutTokenIsRejected() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "test content".getBytes());

        mockMvc.perform(multipart("/uploadFile").file(file).with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void uploadAndDownloadFile() throws Exception {
        String token = loginAndGetToken(mockMvc);

        MockMultipartFile file = new MockMultipartFile(
                "file", "testfile.txt", "text/plain", "hello from test".getBytes());

        mockMvc.perform(multipart("/uploadFile")
                        .file(file)
                        .header("Authorization", "Bearer " + token)
                        .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/downloadFile")
                        .param("fileName", "testfile.txt")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}