package skocz.mateusz.geckoDynamics.model;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ClientDataDao {

    private ClientDataRepository clientDataRepository;

    @Autowired
    public ClientDataDao(ClientDataRepository clientDataRepository) {
        this.clientDataRepository = clientDataRepository;
    }

    public Optional<ClientData> findByKey(String key) {
        log.info("Searching record by key {}", key);
        return clientDataRepository.findById(key);
    }

    public Iterable<ClientData> findAll() {
        return clientDataRepository.findAll();
    }

    public ClientData save(ClientData clientData) {
        log.info("Saving {}", clientData);
        return clientDataRepository.save(clientData);
    }

    public List<ClientData> saveAll(List<ClientData> clientData) {
        log.info("Saving {}", clientData);
        Iterable<ClientData> result = clientDataRepository.saveAll(clientData);
        return Lists.newArrayList(result);
    }

    public void deleteByKey(String key) {
        clientDataRepository.deleteById(key);
    }

    //todo: remove it!!
    @EventListener(ApplicationReadyEvent.class)
    public void fillDB() {
        save(new ClientData("key1", "someName1", "desc1", " "));

    }
}