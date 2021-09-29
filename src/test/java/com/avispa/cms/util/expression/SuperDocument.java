package com.avispa.cms.util.expression;

import com.avispa.cms.model.document.Document;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.time.LocalDateTime;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
@Entity
public class SuperDocument extends Document {
    private String extraField;
    private LocalDateTime extraDate;
    private int extraInt;
}
