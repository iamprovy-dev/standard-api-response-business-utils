package zw.saas.validation.responses.collections;

import jakarta.persistence.Embeddable;
import lombok.Data;
import zw.saas.validation.responses.enums.Gender;
import zw.saas.validation.responses.enums.Relationship;

import java.io.Serializable;

@Data
@Embeddable
public class Relatives implements Serializable {
    private String nokName;
    private String nokSurname;
    private String nokPhone;
    private String nokAddress;
    private Gender nokGender;
    private Relationship relationship;
}