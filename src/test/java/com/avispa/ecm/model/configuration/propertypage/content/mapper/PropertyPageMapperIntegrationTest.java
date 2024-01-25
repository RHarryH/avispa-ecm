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

package com.avispa.ecm.model.configuration.propertypage.content.mapper;

import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.dictionary.Dictionary;
import com.avispa.ecm.model.configuration.dictionary.DictionaryNotFoundException;
import com.avispa.ecm.model.configuration.dictionary.DictionaryValue;
import com.avispa.ecm.model.configuration.propertypage.PropertyPage;
import com.avispa.ecm.model.configuration.propertypage.content.PropertyPageContent;
import com.avispa.ecm.model.configuration.propertypage.content.control.Columns;
import com.avispa.ecm.model.configuration.propertypage.content.control.ComboRadio;
import com.avispa.ecm.model.configuration.propertypage.content.control.Control;
import com.avispa.ecm.model.configuration.propertypage.content.control.Hidden;
import com.avispa.ecm.model.configuration.propertypage.content.control.Label;
import com.avispa.ecm.model.configuration.propertypage.content.control.Money;
import com.avispa.ecm.model.configuration.propertypage.content.control.Number;
import com.avispa.ecm.model.configuration.propertypage.content.control.PropertyControl;
import com.avispa.ecm.model.configuration.propertypage.content.control.Tab;
import com.avispa.ecm.model.configuration.propertypage.content.control.Table;
import com.avispa.ecm.model.configuration.propertypage.content.control.Tabs;
import com.avispa.ecm.model.configuration.propertypage.content.control.Text;
import com.avispa.ecm.model.configuration.propertypage.content.control.Textarea;
import com.avispa.ecm.model.configuration.propertypage.content.control.constraints.Constraint;
import com.avispa.ecm.model.configuration.propertypage.content.control.constraints.Constraints;
import com.avispa.ecm.model.configuration.propertypage.content.control.dictionary.DynamicLoad;
import com.avispa.ecm.model.content.Content;
import com.avispa.ecm.model.document.Document;
import com.avispa.ecm.model.format.Format;
import com.avispa.ecm.model.type.Type;
import com.avispa.ecm.model.type.TypeService;
import com.avispa.ecm.util.NestedObject;
import com.avispa.ecm.util.TestDocument;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.avispa.ecm.model.configuration.propertypage.content.PropertyPageContext.EDIT;
import static com.avispa.ecm.model.configuration.propertypage.content.PropertyPageContext.INSERT;
import static com.avispa.ecm.model.configuration.propertypage.content.PropertyPageContext.READONLY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
@SpringBootTest
class PropertyPageMapperIntegrationTest {
    private Document document;

    @MockBean
    private TypeService typeService;

    @MockBean
    private EcmConfigRepository<Dictionary> dictionaryRepository;

    @Autowired
    private PropertyPageMapper propertyPageMapper;

    @BeforeEach
    void init() {
        document = createTestDocument();

        // first dictionary
        {
            Dictionary testDict = new Dictionary();
            testDict.setObjectName("Test Dictionary");

            DictionaryValue dv1 = new DictionaryValue();
            dv1.setKey("1");
            dv1.setLabel("10");

            DictionaryValue dv2 = new DictionaryValue();
            dv2.setKey("2");
            dv2.setLabel("20");

            DictionaryValue dv3 = new DictionaryValue();
            dv3.setKey("3");
            dv3.setLabel("30");

            testDict.addValue(dv1);
            testDict.addValue(dv2);
            testDict.addValue(dv3);

            when(dictionaryRepository.findByObjectName("Test Dictionary")).thenReturn(Optional.of(testDict));
        }

        // second dictionary
        {
            Dictionary testDict = new Dictionary();
            testDict.setObjectName("Test Dictionary 2");

            DictionaryValue dv1 = new DictionaryValue();
            dv1.setKey("a");
            dv1.setLabel("A");

            DictionaryValue dv2 = new DictionaryValue();
            dv2.setKey("b");
            dv2.setLabel("B");

            testDict.addValue(dv1);
            testDict.addValue(dv2);

            when(dictionaryRepository.findByObjectName("Test Dictionary 2")).thenReturn(Optional.of(testDict));
        }

        Type type = new Type();
        type.setObjectName("Test Document");
        type.setEntityClass(TestDocument.class);

        when(typeService.getType("Test Document")).thenReturn(type);
    }

    private Document createTestDocument() {
        TestDocument document = new TestDocument();
        document.setId(UUID.randomUUID());
        document.setObjectName("It's me");
        document.setTestDate(LocalDate.of(2010, 11, 12));
        document.setTestInt(10);
        document.setTable(generateTableDocuments());

        NestedObject nestedObject = new NestedObject();
        nestedObject.setNestedField("Nested field");
        document.setNestedObject(nestedObject);
        return document;
    }

    private List<Document> generateTableDocuments() {
        return IntStream.range(0, 5).mapToObj(this::generateTableDocument).collect(Collectors.toList());
    }

    private Document generateTableDocument(int number) {
        TestDocument document = new TestDocument();
        document.setId(UUID.randomUUID());
        document.setObjectName("table" + number);
        document.setTestInt(number);

        return document;
    }

    @Test
    void columnsTest() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/columns.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(PropertyPageMapperConfigurer.readonly(), propertyPage, document);

        // then
        List<Control> controls = propertyPageContent.getControls();
        assertEquals(1, controls.size());

        assertTrue(controls.get(0) instanceof Columns);
        Columns columns = (Columns) controls.get(0);
        assertEquals(2, columns.getControls().size());

        assertTrue(columns.getControls().get(0) instanceof ComboRadio);
        assertTrue(columns.getControls().get(1) instanceof Money);
    }

    @Test
    void comboTest() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/combo.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(PropertyPageMapperConfigurer.readonly(), propertyPage, document);

        // then
        List<Control> controls = propertyPageContent.getControls();
        assertEquals(1, controls.size());

        assertTrue(controls.get(0) instanceof ComboRadio);
        ComboRadio combo = (ComboRadio) controls.get(0);
        assertEquals("Combo test", combo.getLabel());
        assertEquals("testString", combo.getProperty());
        assertInstanceOf(DynamicLoad.class, combo.getLoadSettings());

        var dynamicLoad = (DynamicLoad) combo.getLoadSettings();
        assertEquals("Test Document", dynamicLoad.getType());
        assertEquals("{\"$limit\":2}", dynamicLoad.getQualification());
        assertTrue(combo.isRequired());
    }

    @Test
    void dateTest() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/date.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(PropertyPageMapperConfigurer.readonly(), propertyPage, document);

        // then
        List<Control> controls = propertyPageContent.getControls();
        assertEquals(1, controls.size());

        assertTrue(controls.get(0) instanceof PropertyControl);
        PropertyControl date = (PropertyControl) controls.get(0);
        assertEquals("Date test", date.getLabel());
        assertEquals("testDate", date.getProperty());
        assertTrue(date.isRequired());
    }

    @Test
    void moneyTest() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/money.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(PropertyPageMapperConfigurer.readonly(), propertyPage, document);

        // then
        List<Control> controls = propertyPageContent.getControls();
        assertEquals(1, controls.size());

        assertTrue(controls.get(0) instanceof Money);
        Money money = (Money) controls.get(0);
        assertEquals("Money test", money.getLabel());
        assertEquals("objectName", money.getProperty());
        assertEquals("PLN", money.getCurrency());
        assertTrue(money.isRequired());
    }

    @Test
    void numberTest() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/number.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(PropertyPageMapperConfigurer.readonly(), propertyPage, document);

        // then
        List<Control> controls = propertyPageContent.getControls();
        assertEquals(1, controls.size());

        assertTrue(controls.get(0) instanceof Number);
        Number number = (Number) controls.get(0);
        assertEquals("Number test", number.getLabel());
        assertEquals("testInt", number.getProperty());
        assertEquals(10, number.getMin());
    }

    @Test
    void radioTest() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/radio.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(PropertyPageMapperConfigurer.readonly(), propertyPage, document);

        // then
        assertEquals(READONLY, propertyPageContent.getContext());

        List<Control> controls = propertyPageContent.getControls();
        assertEquals(1, controls.size());

        assertTrue(controls.get(0) instanceof ComboRadio);
        ComboRadio radio = (ComboRadio) controls.get(0);
        assertEquals("Radio test", radio.getLabel());
        assertEquals("testString", radio.getProperty());

        Map<String, String> values = Map.of("1", "10", "2", "20", "3", "30");
        assertEquals(values, radio.getOptions());
    }

    @Test
    void tableTest() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/table.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(PropertyPageMapperConfigurer.readonly(), propertyPage, document);

        // then
        List<Control> controls = propertyPageContent.getControls();
        assertEquals(1, controls.size());

        assertTrue(controls.get(0) instanceof Table);
        Table table = (Table) controls.get(0);
        assertEquals("table", table.getProperty());
        assertEquals(3, table.getControls().size());

        PropertyControl control1 = table.getControls().get(0);
        assertTrue(control1 instanceof Text);
        assertTrue(control1.isRequired());

        PropertyControl control2 = table.getControls().get(1);
        assertTrue(control2 instanceof Number);
        assertTrue(control2.isRequired());

        PropertyControl control3 = table.getControls().get(2);
        assertTrue(control3 instanceof Hidden);
        assertEquals("id", control3.getProperty());
    }

    @Test
    void tabsTest() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/tabs.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(PropertyPageMapperConfigurer.readonly(), propertyPage, document);

        // then
        List<Control> controls = propertyPageContent.getControls();
        assertEquals(1, controls.size());

        assertTrue(controls.get(0) instanceof Tabs);
        Tabs tabs = (Tabs) controls.get(0);
        assertEquals("Tabs test", tabs.getLabel());
        assertEquals(2, tabs.getTabs().size());

        Tab tab1 = tabs.getTabs().get(0);
        assertEquals("Tab 1", tab1.getName());
        assertTrue(tab1.getControls().get(0) instanceof Number);

        Tab tab2 = tabs.getTabs().get(1);
        assertEquals("Tab 2", tab2.getName());
        assertTrue(tab2.getControls().get(0) instanceof PropertyControl);
    }

    @Test
    void textareaTest() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/textarea.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(PropertyPageMapperConfigurer.readonly(), propertyPage, document);

        // then
        List<Control> controls = propertyPageContent.getControls();
        assertEquals(1, controls.size());

        assertTrue(controls.get(0) instanceof Textarea);
        Textarea textarea = (Textarea) controls.get(0);
        assertEquals("Textarea test", textarea.getLabel());
        assertEquals("testString", textarea.getProperty());
        assertEquals(25, textarea.getRows());
    }

    @Test
    void twoControlsTest() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/twoControls.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(PropertyPageMapperConfigurer.readonly(), propertyPage, document);

        // then
        List<Control> controls = propertyPageContent.getControls();
        assertEquals(2, controls.size());

        assertTrue(controls.get(0) instanceof Label);
        assertTrue(controls.get(1) instanceof Text);
    }

    @Test
    void resolveDictionaryFromPropertyPageConfiguration() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/dictionaryFromProperty.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(PropertyPageMapperConfigurer.readonly(), propertyPage, document);

        // then
        ComboRadio combo = (ComboRadio) propertyPageContent.getControls().get(0);
        Map<String, String> values = Map.of("1", "10", "2", "20", "3", "30");
        assertEquals(values, combo.getOptions());
    }

    @Test
    void resolveDictionaryFromFieldAnnotation() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/dictionaryFromAnnotation.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(PropertyPageMapperConfigurer.readonly(), propertyPage, document);

        // then
        ComboRadio combo = (ComboRadio) propertyPageContent.getControls().get(0);
        Map<String, String> values = Map.of("1", "10", "2", "20", "3", "30");
        assertEquals(values, combo.getOptions());
    }

    @Test
    void dictionaryNotSpecified() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/dictionaryNotSpecified.json");

        // when
        var configurer = PropertyPageMapperConfigurer.readonly();
        assertThrows(DictionaryNotFoundException.class, () -> propertyPageMapper.convertToContent(configurer, propertyPage, document));
    }

    @Test
    void dictionaryFromPropertyPageHasHigherImportance() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/dictionaryOverride.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(PropertyPageMapperConfigurer.readonly(), propertyPage, document);

        // then
        ComboRadio combo = (ComboRadio) propertyPageContent.getControls().get(0);
        Map<String, String> values = Map.of("a", "A", "b", "B");
        assertEquals(values, combo.getOptions());
    }

    @Test
    void emptyLabelSetToPropertyNameWhenNoDisplayName() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/propertyControlLabel.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(PropertyPageMapperConfigurer.readonly(), propertyPage, document);

        // then
        assertEquals(2, propertyPageContent.getControls().size());
        Text text = (Text) propertyPageContent.getControls().get(0);
        assertEquals("testDate", text.getLabel());
    }

    @Test
    void emptyLabelSetToDisplayName() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/propertyControlLabel.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(PropertyPageMapperConfigurer.readonly(), propertyPage, document);

        // then
        assertEquals(2, propertyPageContent.getControls().size());
        Text text = (Text) propertyPageContent.getControls().get(1);
        assertEquals("Some test integer", text.getLabel());
    }

    @Test
    void nestedPropertyTest() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/nestedProperty.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(PropertyPageMapperConfigurer.readonly(), propertyPage, document);

        // then
        List<Control> controls = propertyPageContent.getControls();
        assertEquals(1, controls.size());

        assertTrue(controls.get(0) instanceof Text);
        Text text = (Text) controls.get(0);
        assertEquals("Nested field", text.getValue());
    }

    @Test
    void tablePropertyTest() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/table.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(PropertyPageMapperConfigurer.readonly(), propertyPage, document);

        // then
        List<Control> controls = propertyPageContent.getControls();
        assertEquals(1, controls.size());

        assertTrue(controls.get(0) instanceof Table);
        Table table = (Table) controls.get(0);

        PropertyControl control1 = table.getControls().get(0);
        assertEquals(List.of("table0", "table1", "table2", "table3", "table4"), control1.getValue());

        PropertyControl control2 = table.getControls().get(1);
        assertEquals(List.of("0", "1", "2", "3", "4"), control2.getValue());
    }

    @Test
    void constraintsTest() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/constraints.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(PropertyPageMapperConfigurer.readonly(), propertyPage, document);

        // then
        List<Control> controls = propertyPageContent.getControls();
        assertEquals(1, controls.size());

        assertTrue(controls.get(0) instanceof Number);
        Number number = (Number) controls.get(0);

        Constraints constraints = number.getConstraints();
        assertNotNull(constraints);

        Constraint visibility = constraints.getVisibility();
        assertNotNull(visibility);
        assertEquals(List.of(INSERT), visibility.getContexts());
        assertEquals("{\"objectName\":\"TEST\"}", visibility.getConditions());

        Constraint requirement = constraints.getRequirement();
        assertNotNull(requirement);
        assertEquals(List.of(INSERT, EDIT), requirement.getContexts());
        assertEquals("{\"objectName\":\"TEST\"}", requirement.getConditions());
    }

    private PropertyPage createPropertyPage(String contentPath) {
        PropertyPage propertyPage = new PropertyPage();
        propertyPage.setId(UUID.randomUUID());
        Content content = createContent(contentPath);

        propertyPage.setContents(Set.of(content));
        return propertyPage;
    }

    private Content createContent(String contentPath) {
        Content content = new Content();
        content.setFileStorePath(getPath(contentPath));
        Format format = new Format();
        format.setObjectName("pdf");
        content.setFormat(format);
        return content;
    }

    private String getPath(String resource) {
        try {
            return new ClassPathResource(resource).getFile().getAbsolutePath();
        } catch (IOException e) {
            log.error("Resource {} does not exist", resource);
        }

        return "";
    }
}