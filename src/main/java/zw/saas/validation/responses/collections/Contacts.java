package zw.saas.validation.responses.collections;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
@JsonIgnoreProperties(ignoreUnknown = true)
public class Contacts implements Serializable {
    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "contact_number", nullable = false, unique = true)
    private String contactNumber;

    @Column(name = "designation")
    private String designation;

    @Column(name = "email_address")
    private String emailAddress;
}
