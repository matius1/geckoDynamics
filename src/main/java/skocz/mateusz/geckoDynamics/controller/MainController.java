package skocz.mateusz.geckoDynamics.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skocz.mateusz.geckoDynamics.model.ClientData;
import skocz.mateusz.geckoDynamics.model.ClientDataDao;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static skocz.mateusz.geckoDynamics.controller.Endpoints.*;

@RestController
@RequestMapping(BASE_URL)
@Slf4j
public class MainController {
    @Autowired
    private ClientDataDao dao;
    @Autowired
    private ClientDataParser parser;

    @GetMapping(GET)
    @ResponseBody
    public ResponseEntity getDataByKey(@RequestParam(required = true) String key) {
        Optional<ClientData> maybeClientData = dao.findByKey(key);
        if (maybeClientData.isPresent()) {
            ClientData clientData = maybeClientData.get();
            log.info("Found record {}", clientData);
            return ResponseEntity.ok(clientData);
        }
        return ResponseEntity.ok("ClientData not found");
    }

    @GetMapping(GET_BY_DATE)
    @ResponseBody
    public ResponseEntity getDataByDate(@RequestParam(required = false) String before,
                                        @RequestParam(required = false) String after,
                                        @RequestParam(required = false, defaultValue = "0") int page,
                                        @RequestParam(required = false, defaultValue = "999") int size) {

        Instant beforeParsed = parser.parseTimestampOrNull(before);
        Instant afterParsed = parser.parseTimestampOrNull(after);
        Page<ClientData> results = Page.empty();

        if (beforeParsed != null && afterParsed == null) {
            results = dao.getBefore(beforeParsed, page, size);
        } else if (beforeParsed == null && afterParsed != null) {
            results = dao.getAfter(afterParsed, page, size);
        } else if (beforeParsed != null && afterParsed != null) {
            results = dao.getBetween(beforeParsed, afterParsed, page, size);
        }

        return ResponseEntity.ok(results.getContent());

    }

    @GetMapping(GET_INCORRECT)
    @ResponseBody
    public ResponseEntity getIncorrect() {
        List<String> incorrectInputs = parser.getIncorrectInputs();
        log.info("Found {} incorrect inputs {}", incorrectInputs.size(), incorrectInputs);
        return ResponseEntity.ok(incorrectInputs);
    }

    @PostMapping(ADD)
    @ResponseBody
    public ResponseEntity addData(@RequestBody String input) {
        try {
            List<ClientData> clientData = parser.parse(input);
            List<ClientData> result = dao.saveAll(clientData);
            return ResponseEntity.ok("Added " + result.size() + " records");

        } catch (Exception e) {
            log.error("Error during saving clientData", e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Input body is not correct");
        }
    }

    @GetMapping(DELETE)
    @ResponseBody
    public ResponseEntity deleteDataByKey(@RequestParam(required = true) String key) {
        dao.deleteByKey(key);
        log.info("Deleted record with key: {}", key);
        return ResponseEntity.ok("Record removed");
    }

}
