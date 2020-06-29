package skocz.mateusz.geckoDynamics.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface ClientDataRepository extends JpaRepository<ClientData, String> {

    Page<ClientData> findAllByUpdatedBefore(Instant before, Pageable pageable);

    Page<ClientData> findAllByUpdatedAfter(Instant before, Pageable pageable);

    Page<ClientData> findAllByUpdatedBeforeAndUpdatedAfter(Instant before, Instant after, Pageable pageable);
}
