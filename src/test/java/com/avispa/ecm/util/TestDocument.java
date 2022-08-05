package com.avispa.ecm.util;

import com.avispa.ecm.model.configuration.dictionary.annotation.Dictionary;
import com.avispa.ecm.model.configuration.display.annotation.DisplayName;
import com.avispa.ecm.model.document.Document;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
public class TestDocument extends Document {
    @Dictionary(name = "TestDict")
    private String testString;
    private LocalDateTime testDateTime;
    private LocalDate testDate;
    private boolean testBoolean;

    @DisplayName("Some test integer")
    private int testInt;

    @OneToMany
    private List<Document> table;

    @OneToMany
    private Set<Document> nonTable;

    @OneToOne(cascade = CascadeType.ALL)
    private NestedObject nestedObject;
}
