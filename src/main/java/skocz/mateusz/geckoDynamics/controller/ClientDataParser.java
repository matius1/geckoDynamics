package skocz.mateusz.geckoDynamics.controller;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import skocz.mateusz.geckoDynamics.model.ClientData;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ClientDataParser {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String HEADERS = "PRIMARY_KEY,NAME,DESCRIPTION,UPDATED_TIMESTAMP";
    public static final String COMMA_SEPARATOR = ";";
    public static final int NUMBER_OF_FIELDS = 4;

    public static List<ClientData> parse(String input) {
        if (input == null) {
            throw new IllegalArgumentException("ClientData is null");

        }
        List<String> lines = Arrays.asList(input.split(LINE_SEPARATOR));

        boolean validHeaders = lines.get(0).toUpperCase().contains(HEADERS);
        if (!validHeaders) {
            throw new IllegalArgumentException("ClientData do not contains correct headers");
        }

        List<ClientData> clientData = lines.stream()
                .filter(ClientDataParser::isNotHeaders)
                .filter(line -> !line.isEmpty())
                .filter(ClientDataParser::containsAllFields)
                .map(ClientDataParser::toClientData)
                .collect(Collectors.toList());

        return clientData;
    }

    static boolean containsAllFields(String input) {
        if (input.split(COMMA_SEPARATOR).length != NUMBER_OF_FIELDS) {
            log.info("ClientData do not contains all 4 fields. Input: {}", input);
//            throw new IllegalArgumentException("ClientData do not contains all required fields. Input: " + input);
            //todo: store it!
            return false;
        }
        return true;
    }

    static ClientData toClientData(String line) {
        List<String> fields = Arrays.asList(line.split(COMMA_SEPARATOR));

        return ClientData.builder()
                .primary_key(fields.get(0))
                .name(fields.get(1))
                .description(fields.get(2))
                .updated_timestamp(fields.get(3))
                .build();
    }

    private static boolean isNotHeaders(String line) {
        return !line.toUpperCase().contains(HEADERS);
    }


}
