package skocz.mateusz.geckoDynamics.controller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import skocz.mateusz.geckoDynamics.model.ClientData;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.*;

@Slf4j
@Service
public class ClientDataParser {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String HEADERS = "PRIMARY_KEY,NAME,DESCRIPTION,UPDATED_TIMESTAMP";
    public static final String COMMA_SEPARATOR = ";";
    public static final int NUMBER_OF_FIELDS = 4;
    public static final List<DateTimeFormatter> FORMATTERS = ImmutableList.of(ISO_DATE_TIME, ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()));

    private List<String> incorrectInputs = Lists.newArrayList();


    public List<String> getIncorrectInputs() {
        return incorrectInputs;
    }

    public int getIncorrectInputsSize() {
        return incorrectInputs.size();
    }

    public void emptyIncorrectInputs() {
        incorrectInputs.clear();
    }


    public List<ClientData> parse(String input) {
        if (input == null) {
            throw new IllegalArgumentException("ClientData is null");

        }
        List<String> lines = Arrays.asList(input.split(LINE_SEPARATOR));

        boolean validHeaders = validateHeader(lines.get(0));
        if (!validHeaders) {
            throw new IllegalArgumentException("ClientData do not contains correct headers");
        }

        List<ClientData> clientData = lines.stream()
                .filter(line -> isNotHeaders(line))
                .filter(line -> !line.isEmpty())
                .map(line -> toClientData(line))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return clientData;
    }

    public boolean validateHeader(String input) {
        return input.toUpperCase().contains(HEADERS);
    }

    boolean containsAllFields(String input) {
        if (input.split(COMMA_SEPARATOR).length != NUMBER_OF_FIELDS) {
            log.info("ClientData do not contains all 4 fields. Input: {}", input);
//            throw new IllegalArgumentException("ClientData do not contains all required fields. Input: " + input);
            //todo: store it!
            return false;
        }
        return true;
    }

    private boolean isNotHeaders(String line) {
        return !validateHeader(line);
    }

    ClientData toClientData(String line) {
        try {
            List<String> fields = Arrays.asList(line.split(COMMA_SEPARATOR));
            return ClientData.builder()
                    .primary_key(fields.get(0))
                    .name(fields.get(1))
                    .description(fields.get(2))
                    .updated(tryParseTimestamp(fields.get(3)))
                    .build();
        } catch (Exception e) {
            log.warn("Parsing input failed [{}]", line, e);
            incorrectInputs.add(line);
            return null;
        }

    }

    public Instant tryParseTimestamp(String input) {

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                TemporalAccessor parse = formatter.parse(input);
                return Instant.from(parse);
            } catch (Exception e) {
            }
        }
        throw new IllegalArgumentException("Parsing timestamp failed: " + input);
    }

    public Instant parseTimestampOrNull(String input){
        try {
            return tryParseTimestamp(input);
        }catch (Exception e){
            return null;
        }
    }
}
