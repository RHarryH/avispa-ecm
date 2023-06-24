/*
 * Avispa ECM - a small framework for implementing basic ECM solution
 * Copyright (C) 2023 Rafał Hiszpański
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
