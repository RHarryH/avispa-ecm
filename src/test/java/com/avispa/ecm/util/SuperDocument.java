package com.avispa.ecm.util;

import com.avispa.ecm.model.document.Document;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
@Entity
public class SuperDocument extends Document {
    private String extraField;
    private LocalDateTime extraDateTime;
    private LocalDate extraDate;
    private int extraInt;
}
