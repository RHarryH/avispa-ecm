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

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
@Component
public class ExpressionResolver {
    public String resolve(Object object, String expression) throws ExpressionResolverException {
        try {
            if(StringUtils.isEmpty(expression)) {
                if(log.isWarnEnabled()) {
                    log.warn("Expression is empty");
                }
                return expression;
            }

            // create lexer and get tokens
            Lexer lexer = new ExpressionLexer(CharStreams.fromString(expression));
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // run parser
            ExpressionParser parser = new ExpressionParser(tokens);
            parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
            parser.addErrorListener(new ExpressionErrorListener());
            ParseTree tree = parser.start();

            // go through parse tree
            ExpressionParserVisitorImpl visitor = new ExpressionParserVisitorImpl(object);

            return visitor.visit(tree);
        } catch (ParseCancellationException e) {
            log.error("Syntax error: {}", e.getMessage());
            throw new ExpressionResolverException(String.format("Syntax error: %s", e.getMessage()));
        }
    }
}
