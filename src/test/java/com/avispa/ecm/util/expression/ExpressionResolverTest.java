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
        assertEquals("ABC_XY_ABC", expressionResolver.resolve(document, "ABC_$value('testString')_ABC"));
    }

    @Test
    void applyNonExistingField() throws ExpressionResolverException {
        assertEquals("ABC__ABC", expressionResolver.resolve(document, "ABC_$value('nonExistingField')_ABC"));
    }

    @Test
    void replaceInteger() throws ExpressionResolverException {
        assertEquals("ABC_5_ABC", expressionResolver.resolve(document, "ABC_$value('testInt')_ABC"));
    }

    @Test
    void functionOnly() throws ExpressionResolverException {
        assertEquals("XY", expressionResolver.resolve(document, "$value('testString')"));
    }

    @Test
    void functionWithStringConcatenation() throws ExpressionResolverException {
        assertEquals("XY", expressionResolver.resolve(document, "$value('test' + 'String')"));
    }

    @Test
    void insufficientNumberOfArguments() {
        assertThrows(IllegalArgumentException.class, () -> expressionResolver.resolve(document, "ABC_$default('nonExistingField')_ABC"));
    }

    @Test
    void unknownFunction() throws ExpressionResolverException {
        assertEquals("ABC_$unknownFunction('nonExistingField')_ABC", expressionResolver.resolve(document, "ABC_$unknownFunction('nonExistingField')_ABC"));
    }

    @Test
    void dateValueFunction() throws ExpressionResolverException {
        assertEquals("ABC_10_ABC", expressionResolver.resolve(document, "ABC_$datevalue('testDateTime', 'MM')_ABC"));
    }

    @Test
    void dateValueFunctionInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> expressionResolver.resolve(document, "ABC_$datevalue('testDateTime', 'invalid_format')_ABC"));
    }

    @Test
    void nestedFunction() throws ExpressionResolverException {
        assertEquals("ABC_XY_ABC", expressionResolver.resolve(document, "ABC_$default($value('testString'), 'This is default value')_ABC"));
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
    void nestedFunctionWithConcatenationSurroundedByWhitespaces() throws ExpressionResolverException {
        assertEquals("ABC_XY", expressionResolver.resolve(document, "$default('ABC_'\t+ $value('testString'), 'This is default value')"));
    }

    @Test
    void twoFunctions() throws ExpressionResolverException {
        assertEquals("ABC_XY", expressionResolver.resolve(document, "$value('objectName')_$default($value('testString'), 'This is default value')"));
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
    void plainText() throws ExpressionResolverException {
        assertEquals("invalid", expressionResolver.resolve(document, "invalid"));
    }

    @Test
    void syntaxError() {
        assertThrows(ExpressionResolverException.class, () -> expressionResolver.resolve(document, "inva$lid"));
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

    @Test
    void dollarSymbolEscape() throws ExpressionResolverException {
        assertEquals("Dollar $ symbol", expressionResolver.resolve(document, "Dollar \\$ symbol"));
    }

    @Test
    void dollarSymbolEscapeSurroundedByFunctions() throws ExpressionResolverException {
        assertEquals("ABC $ ABC", expressionResolver.resolve(document, "$value('objectName') \\$ $value('objectName')"));
    }

    @Test
    void dollarSymbolSurroundedByFunctionsAndCharactersNotBeingAPartOfFunctionDef() throws ExpressionResolverException {
        assertEquals("ABC $ ABC", expressionResolver.resolve(document, "$value('objectName') $ $value('objectName')"));
    }

    @Test
    void syntaxErrorWhenDollarSymbolPrecededByLetter() {
        assertThrows(ExpressionResolverException.class, () -> expressionResolver.resolve(document, "$value('objectName') $value $value('objectName')"));
    }

    @Test
    void dollarSymbolEscapeWhenPrecededByLetter() throws ExpressionResolverException {
        assertEquals("ABC $value ABC", expressionResolver.resolve(document, "$value('objectName') \\$value $value('objectName')"));
    }

    @Test
    void backslashAndDollarSymbolEscapeWhenPrecededByLetter() throws ExpressionResolverException {
        assertEquals("ABC \\$value ABC", expressionResolver.resolve(document, "$value('objectName') \\\\$value $value('objectName')"));
    }

    @Test
    void apostropheOutsideFunction() throws ExpressionResolverException {
        assertEquals("Text ' ABC", expressionResolver.resolve(document, "Text ' $value('objectName')"));
    }

    @Test
    void apostrophesOutsideFunction() throws ExpressionResolverException {
        assertEquals("Text ' ABC ' Text", expressionResolver.resolve(document, "Text ' $value('objectName') ' Text"));
    }

    @Test
    void commaOutsideFunction() throws ExpressionResolverException {
        assertEquals("Text , XY ,", expressionResolver.resolve(document, "Text , $default($value('testString'), 'This is default value') ,"));
    }

    @Test
    void apostropheEscapeInFunctionParam() throws ExpressionResolverException {
        assertEquals("This is default 'value'", expressionResolver.resolve(document, "$default($value('nonExisting'), 'This is default \\'value\\'')"));
    }

    @Test
    void mixedTest() throws ExpressionResolverException {
        assertEquals("F\\G_$dollar$'Test \\$' + ABC I'm test, no? ABC end",
                expressionResolver.resolve(document,
                        "F\\G_\\$dollar$$default($value('nonExisting'), '\\'Test $\\'') + $value('object' + 'Name') I'm test, no? $value('objectName') end"));
    }

    public static String printSyntaxTree(Parser parser, ParseTree root) {
        StringBuilder buf = new StringBuilder();
        recursive(root, buf, 0, Arrays.asList(parser.getRuleNames()));
        return buf.toString();
    }

    private static void recursive(ParseTree aRoot, StringBuilder buf, int offset, List<String> ruleNames) {
        buf.append("  ".repeat(Math.max(0, offset)));

        buf.append(Trees.getNodeText(aRoot, ruleNames)).append("\n");
        if (aRoot instanceof ParserRuleContext prc) {
            if (prc.children != null) {
                for (ParseTree child : prc.children) {
                    recursive(child, buf, offset + 1, ruleNames);
                }
            }
        }
    }
}