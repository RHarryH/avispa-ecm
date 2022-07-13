package com.avispa.ecm.util;

import com.avispa.ecm.model.configuration.dictionary.annotation.Dictionary;
import com.avispa.ecm.model.configuration.display.annotation.DisplayName;
import com.avispa.ecm.model.document.Document;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
@Entity
public class SuperDocument extends Document {
    @Dictionary(name = "TestDict")
    private String extraField;
    private LocalDateTime extraDateTime;
    private LocalDate extraDate;

    @DisplayName("Some extra integer")
    private int extraInt;

    @OneToMany
    private List<Document> table;

    @OneToMany
    private Set<Document> nonTable;
}
