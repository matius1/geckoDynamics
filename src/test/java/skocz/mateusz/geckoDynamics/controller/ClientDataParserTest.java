package skocz.mateusz.geckoDynamics.controller;

import org.junit.jupiter.api.Test;
import skocz.mateusz.geckoDynamics.model.ClientData;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.format.DateTimeFormatter.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClientDataParserTest {

    public static final ClientDataParser PARSER = new ClientDataParser();

    @Test
    public void shouldThrowExceptionWhenInputNull() {
        assertThrows(IllegalArgumentException.class, () -> PARSER.parse(null));
    }

    @Test
    public void shouldThrowExceptionWhenEmptyInput() {
        assertThrows(IllegalArgumentException.class, () -> PARSER.parse(""));
    }

    @Test
    public void shouldParseOneRecord() {
        // Given
        String input = "PRIMARY_KEY,NAME,DESCRIPTION,UPDATED_TIMESTAMP\n" +
                "val1;val2;val3;2020-07-01T19:34:50.63Z;\n";
        // When
        List<ClientData> result = PARSER.parse(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    public void shouldPassValidationHeaders() {
        // Given
        String input = "PRIMARY_KEY,NAME,DESCRIPTION,UPDATED_TIMESTAMP";

        // When
        boolean isValid = PARSER.validateHeader(input);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    public void shouldPassValidationHeadersCaseInsensitive() {
        // Given
        String input = "primary_key,name,description,updated_timestamp";

        // When
        boolean isValid = PARSER.validateHeader(input);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    public void shouldFailsValidationHeaders() {
        // Given
        String input = "PRIMARY_KEY,NAME,DESCRIPTION";

        // When
        boolean isValid = PARSER.validateHeader(input);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    public void shouldFailsWhenFieldsToMany() {
        // Given
        String input = "val1;val2;val3;2020-07-01T19:34:50.63Z;val5";

        // When
        boolean isValid = PARSER.containsAllFields(input);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    public void shouldFailsWhenFieldsNotEnough() {
        // Given
        String input = "val1;val2;val3";

        // When
        boolean isValid = PARSER.containsAllFields(input);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    public void shouldPassAllWhenFields() {
        // Given
        String input = "val1;val2;val3;2020-07-01T19:34:50.63Z";

        // When
        boolean isValid = PARSER.containsAllFields(input);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    public void shouldParseToClientData() {
        // Given
        String input = "val1;val2;val3;2020-07-01T19:34:50.63Z";

        ClientData expected = ClientData.builder()
                .primary_key("val1")
                .name("val2")
                .description("val3")
                .updated(Instant.parse("2020-07-01T19:34:50.63Z"))
                .build();

        // When
        ClientData actual = PARSER.toClientData(input);

        // Then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void shouldParseValidRecordAndAddToListInvalid() {
        // Given
        String headers = "PRIMARY_KEY,NAME,DESCRIPTION,UPDATED_TIMESTAMP";
        String validInput = "val1;val2;val3;2020-07-01T19:34:50.63Z";
        String invalidInput = "!@#$%";

        ClientData expected = ClientData.builder()
                .primary_key("val1")
                .name("val2")
                .description("val3")
                .updated(Instant.parse("2020-07-01T19:34:50.63Z"))
                .build();

        // When
        PARSER.emptyIncorrectInputs();
        List<ClientData> clientData = PARSER.parse(headers + "\n" + validInput + "\n" + invalidInput);

        // Then
        assertThat(clientData).isNotNull();
        assertThat(clientData).hasSize(1);
        assertThat(clientData.get(0)).isEqualToComparingFieldByField(expected);
        assertThat(PARSER.getIncorrectInputsSize()).isEqualTo(1);
        assertThat(PARSER.getIncorrectInputs().get(0)).isEqualTo(invalidInput);
    }

    @Test
    public void shouldNotParseIncorrectInputAndAddToList() {
        // Given
        String input = "val1;val2;val3;2020-07";

        // When
        PARSER.emptyIncorrectInputs();
        ClientData actual = PARSER.toClientData(input);

        // Then
        assertThat(actual).isNull();
        assertThat(PARSER.getIncorrectInputsSize()).isEqualTo(1);
        assertThat(PARSER.getIncorrectInputs().get(0)).isEqualTo(input);
    }

    @Test
    public void shouldParseTimestampFormatISO_DATE_TIME() {
        // Given
        String input = "2020-07-01T19:34:50.63Z";
        Instant expected = Instant.from(ISO_DATE_TIME.parse("2020-07-01T19:34:50.63Z"));

        // When
        Instant actual = PARSER.tryParseTimestamp(input);

        // Then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldParseTimestampFormatISO_LOCAL_DATE_TIME() {
        // Given
        String input = "2020-07-01 19:34:50";

        DateTimeFormatter dateTimeFormatter = ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        Instant expected = Instant.from(dateTimeFormatter.parse("2020-07-01 19:34:50"));

        // When
        Instant actual = PARSER.tryParseTimestamp(input);

        // Then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }


}
