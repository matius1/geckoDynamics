package skocz.mateusz.geckoDynamics.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientDataRepository extends CrudRepository<ClientData, String> {
}
