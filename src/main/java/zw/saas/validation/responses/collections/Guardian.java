package zw.saas.validation.responses.collections;

import jakarta.persistence.Embeddable;
import lombok.Data;
import zw.saas.validation.responses.enums.Relationship;

import java.io.Serializable;

@Data
@Embeddable
public class Guardian implements Serializable {
    private String name;
    private String surname;
    private String mobile;
    private String address;
    private Relationship relationship;
}
