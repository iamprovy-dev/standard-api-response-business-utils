package zw.saas.validation.responses.enums;


import lombok.Getter;

@Getter
public enum Qualifications {
    DIPLOMA("Diploma"),
    DEGREE("Bachelor's Degree"),
    MASTERS("Master's Degree"),
    DOCTORATE("Doctorate Degree"),
    PHD("Professional Degree"),
    CERTIFICATE("Certificate"),;

    private final String description;

    Qualifications(String description) {
        this.description = description;
    }

}
