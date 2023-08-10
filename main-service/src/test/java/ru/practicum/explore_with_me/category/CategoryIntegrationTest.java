package ru.practicum.explore_with_me.category;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.practicum.explore_with_me.category.dto.CategoryRequestDto;
import ru.practicum.explore_with_me.category.dto.CategoryResponseDto;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class CategoryIntegrationTest {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    @LocalServerPort
    private int port;

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    @Test
    @DisplayName("Full category integration test")
    public void fullCategoryIntegrationTest() throws JSONException, IOException {
        CategoryRequestDto categoryRequestDtoTest1 = new CategoryRequestDto("Test Category1");
        CategoryRequestDto categoryRequestDtoTest2 = new CategoryRequestDto("Test Category2");

        // Create a category and validate response
        HttpEntity<CategoryRequestDto> entity1 = new HttpEntity<>(categoryRequestDtoTest1, headers);
        ResponseEntity<CategoryResponseDto> response1 = restTemplate.exchange(
            createURLWithPort("/admin/categories"),
            HttpMethod.POST, entity1, CategoryResponseDto.class);

        CategoryResponseDto categoryResponseDto1 = response1.getBody();
        assertNotNull(categoryResponseDto1);
        assertEquals(categoryRequestDtoTest1.getName(), categoryResponseDto1.getName());

        // Create another category for testing
        HttpEntity<CategoryRequestDto> entity2 = new HttpEntity<>(categoryRequestDtoTest2, headers);
        restTemplate.exchange(createURLWithPort("/admin/categories"), HttpMethod.POST, entity2,
            CategoryResponseDto.class);

        // Update category using Apache HttpClient
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPatch httpPatch = new HttpPatch(createURLWithPort("/admin/categories/" + categoryResponseDto1.getId()));
        StringEntity entity = new StringEntity("{\"name\":\"Updated Category\"}");
        httpPatch.setEntity(entity);
        httpPatch.setHeader("Accept", "application/json");
        httpPatch.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(httpPatch);

        String responseJson = EntityUtils.toString(response.getEntity());
        assertNotNull(responseJson);
        String expectedJsonUpdate = "{\"name\":\"Updated Category\",\"id\":" + categoryResponseDto1.getId() + "}";
        JSONAssert.assertEquals(expectedJsonUpdate, responseJson, false);

        // Get categories and validate response
        HttpEntity<String> entity3 = new HttpEntity<>(null, headers);
        ResponseEntity<String> responseGetAll = restTemplate.exchange(createURLWithPort("/categories?from=0&size=10"),
            HttpMethod.GET, entity3, String.class);

        String expectedJson = "[{\"name\":\"Updated Category\"},{\"name\":\"Test Category2\"}]";
        JSONAssert.assertEquals(expectedJson, responseGetAll.getBody(), false);

        // Get category by ID and validate response
        ResponseEntity<String> responseGetById =
            restTemplate.exchange(createURLWithPort("/categories/" + categoryResponseDto1.getId()),
                HttpMethod.GET, entity3, String.class);

        String expectedJsonId =
            String.format("{\"name\":\"Updated Category\",\"id\":%d}", categoryResponseDto1.getId());
        JSONAssert.assertEquals(expectedJsonId, responseGetById.getBody(), false);

        // Delete category and validate response
        restTemplate.delete(createURLWithPort("/admin/categories/" + categoryResponseDto1.getId()));

        client.close();
    }
}