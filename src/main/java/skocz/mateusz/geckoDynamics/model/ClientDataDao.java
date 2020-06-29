package skocz.mateusz.geckoDynamics.model;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ClientDataDao {

    private ClientDataRepository repo;

    @Autowired
    public ClientDataDao(ClientDataRepository clientDataRepository) {
        this.repo = clientDataRepository;
    }

    public Optional<ClientData> findByKey(String key) {
        log.info("Searching record by key {}", key);
        return repo.findById(key);
    }

    public Iterable<ClientData> findAll() {
        return repo.findAll();
    }

    public ClientData save(ClientData clientData) {
        log.info("Saving {}", clientData);
        return repo.save(clientData);
    }

    public List<ClientData> saveAll(List<ClientData> clientData) {
        log.info("Saving {}", clientData);
        Iterable<ClientData> result = repo.saveAll(clientData);
        return Lists.newArrayList(result);
    }

    public void deleteByKey(String key) {
        repo.deleteById(key);
    }

    public Page<ClientData> getBefore(Instant before, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findAllByUpdatedBefore(before, pageable);
    }

    public Page<ClientData> getAfter(Instant after, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findAllByUpdatedAfter(after, pageable);
    }

    public Page<ClientData> getBetween(Instant before, Instant after, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findAllByUpdatedBeforeAndUpdatedAfter(before, after, pageable);
    }

}