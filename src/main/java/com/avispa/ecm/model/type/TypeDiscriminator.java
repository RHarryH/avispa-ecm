package com.avispa.ecm.model.type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The design of class hierarchy uses Hibernate's JOINED inheritance type. This means each Java subtype is mapped to
 * single table and joined by the id defined in the base class (EcmObject in this case). Hibernate does not allow to change
 * the inheritance strategy in the class hierarchy but there might be the cases when we want to keep all subtypes in single
 * class but later to be allowed to manage them with single property page and some discriminator field.
 *
 * This approach seems to be similar to SINGLE_TABLE inheritance type but underneath the classes still follow JOINED
 * inheritance type. Additionally, two different subtypes are in fact stored in single class.
 *
 * Example:
 * Retail customer and corporate customer can be added via single property page with combo box discriminator defining the
 * customer type. In the backend they are stored in single customer class. But on frontend it is possible to treat them
 * as to distinct types sharing the same configuration.
 *
 * TypeDiscriminator annotation allows to realize such idea. It points, which property in the class will be used as
 * discriminator for property pages.
 * @author Rafał Hiszpański
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TypeDiscriminator {
    String name();
}
