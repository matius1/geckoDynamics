package skocz.mateusz.geckoDynamics.controller;

import org.junit.jupiter.api.Test;
import skocz.mateusz.geckoDynamics.model.ClientData;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClientDataParserTest {

    @Test
    public void shouldThrowExceptionWhenInputNull() {
        assertThrows(IllegalArgumentException.class, () -> ClientDataParser.parse(null));
    }

    @Test
    public void shouldThrowExceptionWhenEmptyInput() {
        assertThrows(IllegalArgumentException.class, () -> ClientDataParser.parse(""));
    }

    @Test
    public void shouldParseOneRecord() {
        // Given
        String input = "PRIMARY_KEY,NAME,DESCRIPTION,UPDATED_TIMESTAMP\n" +
                "val1;val2;val3;val4;\n";
        // When
        List<ClientData> result = ClientDataParser.parse(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    public void shouldPassValidationHeaders() {
        // Given
        String input = "PRIMARY_KEY,NAME,DESCRIPTION,UPDATED_TIMESTAMP";

        // When
        boolean isValid = input.toUpperCase().contains(ClientDataParser.HEADERS);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    public void shouldPassValidationHeadersCaseInsensitive() {
        // Given
        String input = "primary_key,name,description,updated_timestamp";

        // When
        boolean isValid = input.toUpperCase().contains(ClientDataParser.HEADERS);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    public void shouldFailsValidationHeaders() {
        // Given
        String input = "PRIMARY_KEY,NAME,DESCRIPTION";

        // When
        boolean isValid = input.toUpperCase().contains(ClientDataParser.HEADERS);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    public void shouldFailsWhenFieldsToMany() {
        // Given
        String input = "val1;val2;val3;val4;val5";

        // When
        boolean isValid = ClientDataParser.containsAllFields(input);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    public void shouldFailsWhenFieldsNotEnough() {
        // Given
        String input = "val1;val2;val3";

        // When
        boolean isValid = ClientDataParser.containsAllFields(input);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    public void shouldPassAllWhenFields() {
        // Given
        String input = "val1;val2;val3;val4";

        // When
        boolean isValid = ClientDataParser.containsAllFields(input);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    public void shouldParseToClientData() {
        // Given
        String input = "val1;val2;val3;val4";

        ClientData expected = ClientData.builder()
                .primary_key("val1")
                .name("val2")
                .description("val3")
                .updated_timestamp("val4")
                .build();

        // When
        ClientData actual = ClientDataParser.toClientData(input);

        // Then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

}
