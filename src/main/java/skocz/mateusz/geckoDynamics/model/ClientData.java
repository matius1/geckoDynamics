package skocz.mateusz.geckoDynamics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ClientData {

    @Id
    private String primary_key;
    private String name;
    private String description;
    private String updated_timestamp;

}