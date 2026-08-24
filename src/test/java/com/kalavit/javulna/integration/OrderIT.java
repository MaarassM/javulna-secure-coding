package com.kalavit.javulna.integration;
import com.kalavit.javulna.Application;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static com.kalavit.javulna.integration.TestFixtures.loginAndGetToken;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import org.springframework.http.MediaType;
import com.kalavit.javulna.exception.InvalidOrderException;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc

public class OrderIT {

    @Autowired
    private MockMvc mockMvc;
    @Test
    public void movieObjectsWithoutTokenIsRejected() throws Exception {
        mockMvc.perform(get("/rest/movieobject"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void movieObjectsWithTokenReturnsOk() throws Exception {
        String token = loginAndGetToken(mockMvc);

        mockMvc.perform(get("/rest/movieobject")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    public void placeValidOrderSucceeds() throws Exception {
        String token = loginAndGetToken(mockMvc);

        mockMvc.perform(put("/rest/order")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderItems\":["
                                + "{\"movieObjectId\":\"1\",\"nrOfItemsOrdered\":2},"
                                + "{\"movieObjectId\":\"2\",\"nrOfItemsOrdered\":1}]}"))
                .andExpect(status().isOk());
    }

    @Test
    public void placeOrderWithInvalidQuantityThrowsInvalidOrderException() throws Exception {
        String token = loginAndGetToken(mockMvc);

        try {
            mockMvc.perform(put("/rest/order")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"orderItems\":[{\"movieObjectId\":\"1\",\"nrOfItemsOrdered\":0}]}"));
            fail("expected validation to reject quantity 0");
        } catch (Exception expected) {
            assertTrue(expected.getCause() instanceof InvalidOrderException);
        }
    }

    @Test
    public void placeOrderWithBlankIdIsRejected() throws Exception {
        String token = loginAndGetToken(mockMvc);
        try {
            mockMvc.perform(put("/rest/order")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"orderItems\":[{\"movieObjectId\":\"\",\"nrOfItemsOrdered\":5}]}"));
            fail("expected validation to reject quantity 0");
        } catch (Exception expected) {
            assertTrue(expected.getCause() instanceof InvalidOrderException);
        }
        }
    }

