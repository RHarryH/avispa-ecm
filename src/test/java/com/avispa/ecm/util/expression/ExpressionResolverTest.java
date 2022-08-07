package com.avispa.ecm.util.expression;

import com.avispa.ecm.util.TestDocument;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class ExpressionResolverTest {

    private static TestDocument document;

    private final ExpressionResolver expressionResolver = new ExpressionResolver();

    @BeforeAll
    static void init() {
        document = new TestDocument();
        document.setObjectName("ABC");
        document.setTestDateTime(LocalDateTime.of(2021, 10, 11, 10, 54, 18));
        document.setTestString("XY");
        document.setTestInt(5);
    }

    @Test
    void applyFieldFromTestDocument() throws ExpressionResolverException {
        assertEquals("ABC_XY_ABC", expressionResolver.resolve(document, "'ABC_' + $value('testString') + '_ABC'"));
    }

    @Test
    void applyNonExistingField() throws ExpressionResolverException {
        assertEquals("ABC__ABC", expressionResolver.resolve(document, "'ABC_' + $value('nonExistingField') + '_ABC'"));
    }

    @Test
    void replaceInteger() throws ExpressionResolverException {
        assertEquals("ABC_5_ABC", expressionResolver.resolve(document, "'ABC_' + $value('testInt') + '_ABC'"));
    }

    @Test
    void functionOnly() throws ExpressionResolverException {
        assertEquals("XY", expressionResolver.resolve(document, "$value('testString')"));
    }

    @Test
    void insufficientNumberOfArguments() {
        assertThrows(IllegalArgumentException.class, () -> expressionResolver.resolve(document, "'ABC_' + $default('nonExistingField') + '_ABC'"));
    }

    @Test
    void unknownFunction() throws ExpressionResolverException {
        assertEquals("ABC_$unknownFunction('nonExistingField')_ABC", expressionResolver.resolve(document, "'ABC_' + $unknownFunction('nonExistingField') + '_ABC'"));
    }

    @Test
    void dateValueFunction() throws ExpressionResolverException {
        assertEquals("ABC_10_ABC", expressionResolver.resolve(document, "'ABC_' + $datevalue('testDateTime', 'MM') + '_ABC'"));
    }

    @Test
    void dateValueFunctionInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> expressionResolver.resolve(document, "'ABC_' + $datevalue('testDateTime', 'invalid_format') + '_ABC'"));
    }

    @Test
    void nestedFunction() throws ExpressionResolverException {
        assertEquals("ABC_XY_ABC", expressionResolver.resolve(document, "'ABC_' + $default($value('testString'), 'This is default value') + '_ABC'"));
    }

    @Test
    void defaultFunctionExtractsNonExistingField() throws ExpressionResolverException {
        assertEquals("This is default value", expressionResolver.resolve(document, "$default($value('nonExistingField'), 'This is default value')"));
    }

    @Test
    void nestedFunctionWithConcatenation() throws ExpressionResolverException {
        assertEquals("ABC_XY", expressionResolver.resolve(document, "$default('ABC_'+$value('testString'), 'This is default value')"));
    }

    @Test
    void twoFunctions() throws ExpressionResolverException {
        assertEquals("ABC_XY", expressionResolver.resolve(document, "$value('objectName') + '_' + $default($value('testString'), 'This is default value')"));
    }

    @Test
    void noParamsFunction() {
        assertThrows(IllegalArgumentException.class, () -> expressionResolver.resolve(document, "$value()"));
    }

    @Test
    void tooManyParamsFunction() throws ExpressionResolverException {
        assertEquals("XY", expressionResolver.resolve(document, "$value('testString', 'redundantParameter')"));
    }

    @Test
    void syntaxError() {
        assertThrows(ExpressionResolverException.class, () -> expressionResolver.resolve(document, "invalid"));
    }

    @Test
    void padFunctionWithDefaultPaddingCharacter() throws ExpressionResolverException {
        assertEquals("000a", expressionResolver.resolve(document, "$pad('a', '4')"));
    }

    @Test
    void padFunctionWithCustomPaddingCharacter() throws ExpressionResolverException {
        assertEquals("XXXa", expressionResolver.resolve(document, "$pad('a', '4', 'X')"));
    }

    @Test
    void padFunctionWithShorterLenghtThanProvidedValue() throws ExpressionResolverException {
        assertEquals("aaaa", expressionResolver.resolve(document, "$pad('aaaa', '2')"));
    }

    public static String printSyntaxTree(Parser parser, ParseTree root) {
        StringBuilder buf = new StringBuilder();
        recursive(root, buf, 0, Arrays.asList(parser.getRuleNames()));
        return buf.toString();
    }

    private static void recursive(ParseTree aRoot, StringBuilder buf, int offset, List<String> ruleNames) {
        buf.append("  ".repeat(Math.max(0, offset)));

        buf.append(Trees.getNodeText(aRoot, ruleNames)).append("\n");
        if (aRoot instanceof ParserRuleContext) {
            ParserRuleContext prc = (ParserRuleContext) aRoot;
            if (prc.children != null) {
                for (ParseTree child : prc.children) {
                    recursive(child, buf, offset + 1, ruleNames);
                }
            }
        }
    }
}