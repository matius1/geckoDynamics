package skocz.mateusz.geckoDynamics.controller;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GeneratorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skocz.mateusz.geckoDynamics.model.ClientData;
import skocz.mateusz.geckoDynamics.model.ClientDataDao;

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

    @PostMapping(ADD)
    @ResponseBody
    public ResponseEntity addData(@RequestBody String input) {
        try {
            List<ClientData> clientData = parser.parse(input);
            List<ClientData> result = dao.saveAll(clientData);
            return ResponseEntity.ok("Added " + result.size() + " records");

        } catch (Exception e) {
            log.error("Error during saving clientData", e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("Input body is not correct");
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
