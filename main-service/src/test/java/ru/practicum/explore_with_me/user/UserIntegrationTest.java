package ru.practicum.explore_with_me.user;

import org.json.JSONException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.user.dto.UserRequestDto;
import ru.practicum.explore_with_me.user.dto.UserResponseDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class UserIntegrationTest {
    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();
    @LocalServerPort
    private int port;

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    @Test
    @DisplayName("Full user integration test")
    public void testCreateGetAndDeleteUser() throws JSONException {
        UserRequestDto userRequestDtoTest1 = UserRequestDto.builder()
                .email("test1@email.com")
                .name("Test User1")
                .build();

        // Create a user and validate response
        HttpEntity<UserRequestDto> entity1 = new HttpEntity<>(userRequestDtoTest1, headers);
        ResponseEntity<UserResponseDto> response1 = restTemplate.exchange(
                createURLWithPort("/admin/users"),
                HttpMethod.POST, entity1, UserResponseDto.class);

        UserResponseDto userResponseDto1 = response1.getBody();
        assertNotNull(userResponseDto1);
        assertEquals(userRequestDtoTest1.getEmail(), userResponseDto1.getEmail());
        assertEquals(userRequestDtoTest1.getName(), userResponseDto1.getName());

        // Get users and validate response
        HttpEntity<String> entity2 = new HttpEntity<>(null, headers);
        ResponseEntity<String> response2 = restTemplate.exchange(
                createURLWithPort("/admin/users"),
                HttpMethod.GET, entity2, String.class);

        String expectedJson = String.format("[{\"email\":\"test1@email.com\",\"id\":%d,\"name\":\"Test User1\"}]",
                userResponseDto1.getId());
        JSONAssert.assertEquals(expectedJson, response2.getBody(), false);

        // Delete user and validate response
        restTemplate.delete(createURLWithPort("/admin/users/" + userResponseDto1.getId()));

        // Get users after deletion and validate response
        ResponseEntity<String> response3 = restTemplate.exchange(
                createURLWithPort("/admin/users"),
                HttpMethod.GET, entity2, String.class);

        JSONAssert.assertEquals("[]", response3.getBody(), false);
    }
}