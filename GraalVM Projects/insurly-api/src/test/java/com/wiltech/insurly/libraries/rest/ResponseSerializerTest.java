package com.wiltech.insurly.libraries.rest;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ResponseSerializerTest {

    @Test
    void shouldSerializePayloadDataWithRootName() throws Exception {
        BaseResponse response = new BaseResponse();
        response.setData("user", new TestUser("John", "Doe"));

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(response);

        assertTrue(json.contains("\"_data\":{\"user\":{\"firstName\":\"John\",\"lastName\":\"Doe\"}}"));
    }

    static class TestUser {
        private final String firstName;
        private final String lastName;

        public TestUser(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }
}
