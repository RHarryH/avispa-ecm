/*
 * Avispa ECM - a small framework for implementing basic ECM solution
 * Copyright (C) 2024 Rafał Hiszpański
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

package com.avispa.ecm.model;

import com.avispa.ecm.model.type.TypeService;
import com.avispa.ecm.util.TestDocument;
import com.avispa.ecm.util.exception.EcmException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Rafał Hiszpański
 */
@ExtendWith(MockitoExtension.class)
class EcmObjectServiceTest {
    @Mock
    private EcmObjectRepository<EcmObject> ecmObjectRepository;

    @Mock
    private TypeService typeService;

    @InjectMocks
    private EcmObjectService ecmObjectService;

    @Test
    void givenIdAndTypeName_whenGetObject_thenReturnObject() {
        var testDocument = new TestDocument();
        when(ecmObjectRepository.findById(any(UUID.class))).thenReturn(Optional.of(testDocument));
        when(typeService.getTypeName(TestDocument.class)).thenReturn("Test document");

        assertEquals(testDocument, ecmObjectService.getEcmObjectFrom(UUID.randomUUID(), "Test document"));
    }

    @Test
    void givenIdAndTypeName_whenGetObjectWithLowerCaseTypeName_thenReturnObject() {
        var testDocument = new TestDocument();
        when(ecmObjectRepository.findById(any(UUID.class))).thenReturn(Optional.of(testDocument));
        when(typeService.getTypeName(TestDocument.class)).thenReturn("test document");

        assertEquals(testDocument, ecmObjectService.getEcmObjectFrom(UUID.randomUUID(), "Test document"));
    }

    @Test
    void givenIdAndTypeName_whenTypeDoesNotMatch_thenThrowException() {
        var testDocument = new TestDocument();
        when(ecmObjectRepository.findById(any(UUID.class))).thenReturn(Optional.of(testDocument));
        when(typeService.getTypeName(TestDocument.class)).thenReturn("Document");

        var id = UUID.randomUUID();
        assertThrows(EcmException.class, () -> ecmObjectService.getEcmObjectFrom(id, "Test document"));
    }
}