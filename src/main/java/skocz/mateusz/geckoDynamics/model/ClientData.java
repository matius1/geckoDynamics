package skocz.mateusz.geckoDynamics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
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
//    @Temporal(TemporalType.TIMESTAMP)
    private Instant updated;

}