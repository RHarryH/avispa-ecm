package com.avispa.ecm.model.configuration.propertypage.content.mapper;

import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.dictionary.Dictionary;
import com.avispa.ecm.model.configuration.dictionary.DictionaryService;
import com.avispa.ecm.model.configuration.dictionary.DictionaryValue;
import com.avispa.ecm.model.configuration.display.DisplayService;
import com.avispa.ecm.model.configuration.propertypage.PropertyPage;
import com.avispa.ecm.model.configuration.propertypage.content.PropertyPageContent;
import com.avispa.ecm.model.configuration.propertypage.content.control.Columns;
import com.avispa.ecm.model.configuration.propertypage.content.control.ComboRadio;
import com.avispa.ecm.model.configuration.propertypage.content.control.Control;
import com.avispa.ecm.model.configuration.propertypage.content.control.Label;
import com.avispa.ecm.model.configuration.propertypage.content.control.Money;
import com.avispa.ecm.model.configuration.propertypage.content.control.Number;
import com.avispa.ecm.model.configuration.propertypage.content.control.PropertyControl;
import com.avispa.ecm.model.configuration.propertypage.content.control.Tab;
import com.avispa.ecm.model.configuration.propertypage.content.control.Table;
import com.avispa.ecm.model.configuration.propertypage.content.control.Tabs;
import com.avispa.ecm.model.configuration.propertypage.content.control.Text;
import com.avispa.ecm.model.configuration.propertypage.content.control.Textarea;
import com.avispa.ecm.model.content.Content;
import com.avispa.ecm.model.document.Document;
import com.avispa.ecm.model.format.Format;
import com.avispa.ecm.model.type.Type;
import com.avispa.ecm.model.type.TypeRepository;
import com.avispa.ecm.util.SuperDocument;
import com.avispa.ecm.util.expression.ExpressionResolver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
@DataJpaTest
@AutoConfigureJson
@Import({PropertyPageMapper.class, ExpressionResolver.class, DictionaryService.class, DisplayService.class})
class PropertyPageMapperIntegrationTest {
    private Document document;

    @MockBean
    private TypeRepository typeRepository;

    @MockBean
    private EcmConfigRepository<Dictionary> dictionaryRepository;

    @Autowired
    private PropertyPageMapper propertyPageMapper;

    @BeforeEach
    void init() {
        document = createSuperDocument();

        // first dictionary
        {
            Dictionary testDict = new Dictionary();
            testDict.setObjectName("TestDict");

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

            when(dictionaryRepository.findByObjectName("TestDict")).thenReturn(Optional.of(testDict));
        }

        // second dictionary
        {
            Dictionary testDict = new Dictionary();
            testDict.setObjectName("TestDict2");

            DictionaryValue dv1 = new DictionaryValue();
            dv1.setKey("a");
            dv1.setLabel("A");

            DictionaryValue dv2 = new DictionaryValue();
            dv2.setKey("b");
            dv2.setLabel("B");

            testDict.addValue(dv1);
            testDict.addValue(dv2);

            when(dictionaryRepository.findByObjectName("TestDict2")).thenReturn(Optional.of(testDict));
        }

        Type type = new Type();
        type.setObjectName("Super Document");
        type.setEntityClass(SuperDocument.class);

        when(typeRepository.findByTypeName("Super Document")).thenReturn(type);
    }

    private Document createSuperDocument() {
        SuperDocument document = new SuperDocument();
        document.setId(UUID.randomUUID());
        document.setObjectName("It's me");
        document.setExtraDate(LocalDate.of(2010, 11, 12));
        document.setExtraInt(10);
        return document;
    }

    @Test
    void columnsTest() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/columns.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(propertyPage, document, true);

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
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(propertyPage, document, true);

        // then
        List<Control> controls = propertyPageContent.getControls();
        assertEquals(1, controls.size());

        assertTrue(controls.get(0) instanceof ComboRadio);
        ComboRadio combo = (ComboRadio) controls.get(0);
        assertEquals("Combo test", combo.getLabel());
        assertEquals("extraField", combo.getProperty());
        assertEquals("Super Document", combo.getTypeName());
        assertTrue(combo.isRequired());
    }

    @Test
    void dateTest() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/date.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(propertyPage, document, true);

        // then
        List<Control> controls = propertyPageContent.getControls();
        assertEquals(1, controls.size());

        assertTrue(controls.get(0) instanceof PropertyControl);
        PropertyControl date = (PropertyControl) controls.get(0);
        assertEquals("Date test", date.getLabel());
        assertEquals("extraDate", date.getProperty());
        assertTrue(date.isRequired());
    }

    @Test
    void moneyTest() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/money.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(propertyPage, document, true);

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
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(propertyPage, document, true);

        // then
        List<Control> controls = propertyPageContent.getControls();
        assertEquals(1, controls.size());

        assertTrue(controls.get(0) instanceof Number);
        Number number = (Number) controls.get(0);
        assertEquals("Number test", number.getLabel());
        assertEquals("extraInt", number.getProperty());
        assertEquals(10, number.getMin());
    }

    @Test
    void radioTest() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/radio.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(propertyPage, document, true);

        // then
        assertTrue(propertyPageContent.isReadonly());

        List<Control> controls = propertyPageContent.getControls();
        assertEquals(1, controls.size());

        assertTrue(controls.get(0) instanceof ComboRadio);
        ComboRadio radio = (ComboRadio) controls.get(0);
        assertEquals("Radio test", radio.getLabel());
        assertEquals("extraField", radio.getProperty());

        Map<String, String> values = Map.of("1", "10", "2", "20", "3", "30");
        assertEquals(values, radio.getValues());
    }

    @Test
    void tableTest() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/table.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(propertyPage, document, true);

        // then
        List<Control> controls = propertyPageContent.getControls();
        assertEquals(1, controls.size());

        assertTrue(controls.get(0) instanceof Table);
        Table tabs = (Table) controls.get(0);
        assertEquals("table", tabs.getProperty());
        assertEquals(1, tabs.getControls().size());

        Control control = tabs.getControls().get(0);
        assertTrue(control instanceof Number);
    }

    @Test
    void tabsTest() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/tabs.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(propertyPage, document, true);

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
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(propertyPage, document, true);

        // then
        List<Control> controls = propertyPageContent.getControls();
        assertEquals(1, controls.size());

        assertTrue(controls.get(0) instanceof Textarea);
        Textarea textarea = (Textarea) controls.get(0);
        assertEquals("Textarea test", textarea.getLabel());
        assertEquals("extraField", textarea.getProperty());
        assertEquals(25, textarea.getRows());
    }

    @Test
    void twoControlsTest() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/twoControls.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(propertyPage, document, true);

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
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(propertyPage, document, true);

        // then
        ComboRadio combo = (ComboRadio) propertyPageContent.getControls().get(0);
        Map<String, String> values = Map.of("1", "10", "2", "20", "3", "30");
        assertEquals(values, combo.getValues());
    }

    @Test
    void resolveDictionaryFromFieldAnnotation() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/dictionaryFromAnnotation.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(propertyPage, document, true);

        // then
        ComboRadio combo = (ComboRadio) propertyPageContent.getControls().get(0);
        Map<String, String> values = Map.of("1", "10", "2", "20", "3", "30");
        assertEquals(values, combo.getValues());
    }

    @Test
    void dictionaryNotSpecified() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/dictionaryNotSpecified.json");

        // when
        assertThrows(IllegalStateException.class, () -> propertyPageMapper.convertToContent(propertyPage, document, true));
    }

    @Test
    void dictionaryFromPropertyPageHasHigherImportance() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/dictionaryOverride.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(propertyPage, document, true);

        // then
        ComboRadio combo = (ComboRadio) propertyPageContent.getControls().get(0);
        Map<String, String> values = Map.of("a", "A", "b", "B");
        assertEquals(values, combo.getValues());
    }

    @Test
    void emptyLabelSetToPropertyNameWhenNoDisplayName() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/propertyControlLabel.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(propertyPage, document, true);

        // then
        assertEquals(2, propertyPageContent.getControls().size());
        Text text = (Text) propertyPageContent.getControls().get(0);
        assertEquals("extraDate", text.getLabel());
    }

    @Test
    void emptyLabelSetToDisplayName() {
        // given
        PropertyPage propertyPage = createPropertyPage("content/propertyControlLabel.json");

        // when
        PropertyPageContent propertyPageContent = propertyPageMapper.convertToContent(propertyPage, document, true);

        // then
        assertEquals(2, propertyPageContent.getControls().size());
        Text text = (Text) propertyPageContent.getControls().get(1);
        assertEquals("Some extra integer", text.getLabel());
    }

    private PropertyPage createPropertyPage(String contentPath) {
        PropertyPage propertyPage = new PropertyPage();
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