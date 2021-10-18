package com.avispa.ecm.util.expression;

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

    private static SuperDocument document;

    private final ExpressionResolver expressionResolver = new ExpressionResolver();

    @BeforeAll
    static void init() {
        document = new SuperDocument();
        document.setObjectName("ABC");
        document.setExtraDateTime(LocalDateTime.of(2021, 10, 11, 10, 54, 18));
        document.setExtraField("XY");
        document.setExtraInt(5);
    }

    @Test
    void applyFieldFromSuperDocument() {
        assertEquals("ABC_XY_ABC", expressionResolver.resolve(document, "'ABC_' + $value('extraField') + '_ABC'"));
    }

    @Test
    void applyNonExistingField() {
        assertEquals("ABC__ABC", expressionResolver.resolve(document, "'ABC_' + $value('nonExistingField') + '_ABC'"));
    }

    @Test
    void replaceInteger() {
        assertEquals("ABC_5_ABC", expressionResolver.resolve(document, "'ABC_' + $value('extraInt') + '_ABC'"));
    }

    @Test
    void functionOnly() {
        assertEquals("XY", expressionResolver.resolve(document, "$value('extraField')"));
    }

    @Test
    void insufficientNumberOfArguments() {
        assertThrows(IllegalArgumentException.class, () -> expressionResolver.resolve(document, "'ABC_' + $default('nonExistingField') + '_ABC'"));
    }

    @Test
    void unknownFunction() {
        assertEquals("ABC_$unknownFunction('nonExistingField')_ABC", expressionResolver.resolve(document, "'ABC_' + $unknownFunction('nonExistingField') + '_ABC'"));
    }

    @Test
    void dateValueFunction() {
        assertEquals("ABC_10_ABC", expressionResolver.resolve(document, "'ABC_' + $datevalue('extraDateTime', 'MM') + '_ABC'"));
    }

    @Test
    void dateValueFunctionInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> expressionResolver.resolve(document, "'ABC_' + $datevalue('extraDateTime', 'invalid_format') + '_ABC'"));
    }

    @Test
    void nestedFunction() {
        assertEquals("ABC_XY_ABC", expressionResolver.resolve(document, "'ABC_' + $default($value('extraField'), 'This is default value') + '_ABC'"));
    }

    @Test
    void defaultFunctionExtractsNonExistingField() {
        assertEquals("This is default value", expressionResolver.resolve(document, "$default($value('nonExistingField'), 'This is default value')"));
    }

    @Test
    void nestedFunctionWithConcatenation() {
        assertEquals("ABC_XY", expressionResolver.resolve(document, "$default('ABC_'+$value('extraField'), 'This is default value')"));
    }

    @Test
    void twoFunctions() {
        assertEquals("ABC_XY", expressionResolver.resolve(document, "$value('objectName') + '_' + $default($value('extraField'), 'This is default value')"));
    }

    @Test
    void noParamsFunction() {
        assertThrows(IllegalArgumentException.class, () -> expressionResolver.resolve(document, "$value()"));
    }

    @Test
    void tooManyParamsFunction() {
        assertEquals("XY", expressionResolver.resolve(document, "$value('extraField', 'redundantParameter')"));
    }

    @Test
    void syntaxError() {
        assertEquals("invalid", expressionResolver.resolve(document, "invalid"));
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