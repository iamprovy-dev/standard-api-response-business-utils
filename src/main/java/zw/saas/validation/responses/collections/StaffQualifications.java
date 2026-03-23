package zw.saas.validation.responses.collections;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import zw.saas.validation.responses.enums.Qualifications;

import java.io.Serializable;
import java.time.LocalDate;

@Setter
@Getter
@Embeddable
public class StaffQualifications implements Serializable {
    private Qualifications qualifications;
    private String qualificationName;
    private LocalDate yearObtained;
    private String subjectSpecialised;
    private String institution;
}
