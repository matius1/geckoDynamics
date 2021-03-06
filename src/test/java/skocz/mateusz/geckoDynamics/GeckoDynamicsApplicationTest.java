package skocz.mateusz.geckoDynamics;

import com.google.common.collect.ImmutableList;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static skocz.mateusz.geckoDynamics.controller.Endpoints.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class GeckoDynamicsApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void readCorrectFileAndSendPostToSaveSingleRecord() throws Exception {
        // Given
        final String input = readFileToString("src/test/resources/data/dataCorrect1.txt");

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(BASE_URL + ADD).content(input);

        // When
        final MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        // Then
        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response).isNotNull();
        assertThat(response.getContentAsString()).isEqualTo("Added 1 records");
    }

    @Test
    public void readCorrectFileAndSendPostToSaveMultipleRecords() throws Exception {
        // Given
        final String input = readFileToString("src/test/resources/data/dataCorrect2.txt");

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(BASE_URL + ADD).content(input);

        // When
        final MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        // Then
        final MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response).isNotNull();
        assertThat(response.getContentAsString()).isEqualTo("Added 2 records");
    }

    @Test
    public void readIncorrectFileAndSendPostToSaveBadHeaders() throws Exception {
        // Given
        final String input = readFileToString("src/test/resources/data/dataIncorrect_badHeaders.txt");

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(BASE_URL + ADD).content(input);

        // When
        final MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().is(UNPROCESSABLE_ENTITY.value()))
                .andReturn();

        // Then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response).isNotNull();
        assertThat(response.getContentAsString()).isEqualTo("Input body is not correct");
    }

    @Test
    public void readIncorrectFileAndSendPostToSaveBadRecord() throws Exception {
        // Given
        final String input = readFileToString("src/test/resources/data/dataIncorrect_badRecord.txt");

        final MockHttpServletRequestBuilder addRequest = MockMvcRequestBuilders.post(BASE_URL + ADD).content(input);
        final MockHttpServletRequestBuilder getIncorrectRequest = MockMvcRequestBuilders.get(BASE_URL + GET_INCORRECT);

        // When
        final MvcResult addResult = mockMvc.perform(addRequest).andExpect(status().isOk()).andReturn();

        MockHttpServletResponse addResponse = addResult.getResponse();
        assertThat(addResponse).isNotNull();
        assertThat(addResponse.getContentAsString()).isEqualTo("Added 0 records");

        final MvcResult getIncorrectResult = mockMvc.perform(getIncorrectRequest)
                .andExpect(status().isOk())
                .andReturn();

        // Then
        MockHttpServletResponse getResponse = getIncorrectResult.getResponse();
        assertThat(getResponse).isNotNull();
        assertThat(getResponse.getContentAsString()).contains("val1;val2;val3;val4;val5;");
    }

    @Test
    public void getNotExistingRecord() throws Exception {
        // Given
        final MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders.get(BASE_URL + GET + "?key=val12345");

        // When
        final MvcResult mvcResult = mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andReturn();

        // Then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response).isNotNull();
        assertThat(response.getContentAsString()).isEqualTo("ClientData not found");
    }

    @Test
    public void addAndGetRecord() throws Exception {
        // Given
        final String input = readFileToString("src/test/resources/data/dataCorrect1.txt");
        final String expected_record = "{\"primary_key\":\"val1\",\"name\":\"val2\",\"description\":\"val3\",\"updated\":\"2020-07-01T19:34:50.630Z\"}";

        final MockHttpServletRequestBuilder addRequest = MockMvcRequestBuilders.post(BASE_URL + ADD).content(input);
        final MockHttpServletRequestBuilder readRequest = MockMvcRequestBuilders.get(BASE_URL + GET + "?key=val1");

        // When
        final MvcResult addResult = mockMvc.perform(addRequest)
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse addResponse = addResult.getResponse();
        assertThat(addResponse).isNotNull();
        assertThat(addResponse.getContentAsString()).isEqualTo("Added 1 records");


        final MvcResult readResult = mockMvc.perform(readRequest)
                .andExpect(status().isOk())
                .andReturn();

        // Then
        MockHttpServletResponse readResponse = readResult.getResponse();
        assertThat(readResponse).isNotNull();
        assertThat(readResponse.getContentAsString()).isEqualTo(expected_record);
    }

    @Test
    public void addAndGetRecordAfterUpdated() throws Exception {
        // Given
        final String input = readFileToString("src/test/resources/data/dataCorrect3.txt");
        final String expected_record1 = "{\"primary_key\":\"key2\",\"name\":\"name2\",\"description\":\"desc3\",\"updated\":\"2020-07-02T19:34:50.630Z\"}";
        final String expected_record2 = "{\"primary_key\":\"key3\",\"name\":\"name3\",\"description\":\"desc4\",\"updated\":\"2020-07-03T19:34:50.630Z\"}";

        MockHttpServletRequestBuilder addRequest = MockMvcRequestBuilders.post(BASE_URL + ADD).content(input);
        MockHttpServletRequestBuilder readBeforeRequest = MockMvcRequestBuilders.get(BASE_URL + GET_BY_DATE + "?after=2020-07-02T00:00:00.000Z");

        // When
        mockMvc.perform(addRequest)
                .andExpect(status().isOk());

        final MvcResult readBeforeResult = mockMvc.perform(readBeforeRequest)
                .andExpect(status().isOk())
                .andReturn();

        // Then
        MockHttpServletResponse readResponse = readBeforeResult.getResponse();
        assertThat(readResponse).isNotNull();
        assertThat(readResponse.getContentAsString()).isEqualTo("[" + expected_record1 + "," + expected_record2 + "]");
    }

    @Test
    public void addAndGetRecordBeforeUpdated() throws Exception {
        // Given
        final String input = readFileToString("src/test/resources/data/dataCorrect3.txt");
        final String expected_record = "{\"primary_key\":\"val1\",\"name\":\"val2\",\"description\":\"val3\",\"updated\":\"2020-07-01T19:34:50.630Z\"}";

        final MockHttpServletRequestBuilder addRequest = MockMvcRequestBuilders.post(BASE_URL + ADD).content(input);
        final MockHttpServletRequestBuilder readBeforeRequest = MockMvcRequestBuilders.get(BASE_URL + GET_BY_DATE + "?before=2020-07-02T00:00:00.000Z");

        // When
        mockMvc.perform(addRequest)
                .andExpect(status().isOk());

        final MvcResult readBeforeResult = mockMvc.perform(readBeforeRequest)
                .andExpect(status().isOk())
                .andReturn();

        // Then
        MockHttpServletResponse readResponse = readBeforeResult.getResponse();
        assertThat(readResponse).isNotNull();
        assertThat(readResponse.getContentAsString()).isEqualTo("[" + expected_record + "]");
    }

    @Test
    public void addAndGetRecordBeforeAndAfterUpdated() throws Exception {
        // Given
        final String input = readFileToString("src/test/resources/data/dataCorrect3.txt");
        final String expected_record1 = "{\"primary_key\":\"key2\",\"name\":\"name2\",\"description\":\"desc3\",\"updated\":\"2020-07-02T19:34:50.630Z\"}";

        final MockHttpServletRequestBuilder addRequest = MockMvcRequestBuilders.post(BASE_URL + ADD).content(input);
        final MockHttpServletRequestBuilder readBeforeRequest = MockMvcRequestBuilders.get(BASE_URL + GET_BY_DATE + "?after=2020-07-02T00:00:00.000Z&before=2020-07-03T00:00:00.000Z");

        // When
        mockMvc.perform(addRequest)
                .andExpect(status().isOk());

        final MvcResult readBeforeResult = mockMvc.perform(readBeforeRequest)
                .andExpect(status().isOk())
                .andReturn();

        // Then
        MockHttpServletResponse readResponse = readBeforeResult.getResponse();
        assertThat(readResponse).isNotNull();
        assertThat(readResponse.getContentAsString()).isEqualTo("[" + expected_record1 + "]");
    }

    @Test
    public void addAndDeletedRecord() throws Exception {
        // Given
        final String input = readFileToString("src/test/resources/data/dataCorrect1.txt");
        final String expected_record = "{\"primary_key\":\"val1\",\"name\":\"val2\",\"description\":\"val3\",\"updated\":\"2020-07-01T19:34:50.630Z\"}";

        final MockHttpServletRequestBuilder addRequest = MockMvcRequestBuilders.post(BASE_URL + ADD).content(input);
        final MockHttpServletRequestBuilder readRequest = MockMvcRequestBuilders.get(BASE_URL + GET + "?key=val1");
        final MockHttpServletRequestBuilder deleteRequest = MockMvcRequestBuilders.get(BASE_URL + DELETE + "?key=val1");

        // When
        final MvcResult addResult = mockMvc.perform(addRequest)
                .andExpect(status().isOk())
                .andReturn();

        final MockHttpServletResponse addResponse = addResult.getResponse();
        assertThat(addResponse).isNotNull();
        assertThat(addResponse.getContentAsString()).isEqualTo("Added 1 records");


        final MvcResult readResult = mockMvc.perform(readRequest)
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse readResponse = readResult.getResponse();
        assertThat(readResponse).isNotNull();
        assertThat(readResponse.getContentAsString()).isEqualTo(expected_record);


        final MvcResult deleteResponse = mockMvc.perform(deleteRequest)
                .andExpect(status().isOk())
                .andReturn();

        // Then
        MockHttpServletResponse deleteResult = deleteResponse.getResponse();
        assertThat(deleteResult).isNotNull();
        assertThat(deleteResult.getContentAsString()).isEqualTo("Record removed");
    }

    private String readFileToString(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        BufferedReader reader = Files.newBufferedReader(path);
        return reader.lines().collect(Collectors.joining("\n"));
    }

}
