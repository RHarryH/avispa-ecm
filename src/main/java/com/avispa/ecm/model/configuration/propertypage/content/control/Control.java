package com.avispa.ecm.model.configuration.propertypage.content.control;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        //include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Columns.class, name = "columns"),
        @JsonSubTypes.Type(value = ComboRadio.class, name = "combo"),
        @JsonSubTypes.Type(value = Date.class, name = "date"),
        @JsonSubTypes.Type(value = Date.class, name = "datetime"),
        @JsonSubTypes.Type(value = Text.class, name = "email"),
        @JsonSubTypes.Type(value = Label.class, name = "label"),
        @JsonSubTypes.Type(value = Money.class, name = "money"),
        @JsonSubTypes.Type(value = Number.class, name = "number"),
        @JsonSubTypes.Type(value = Text.class, name = "password"),
        @JsonSubTypes.Type(value = ComboRadio.class, name = "radio"),
        @JsonSubTypes.Type(value = Separator.class, name = "separator"),
        @JsonSubTypes.Type(value = Table.class, name = "table"),
        @JsonSubTypes.Type(value = Tabs.class, name = "tabs"),
        @JsonSubTypes.Type(value = Text.class, name = "text"),
        @JsonSubTypes.Type(value = Textarea.class, name = "textarea"),
})
public abstract class Control {
    private String type;
}
