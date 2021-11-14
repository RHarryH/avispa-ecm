package com.avispa.ecm.util.expression;

import com.avispa.cms.util.expression.parser.ExpressionLexer;
import com.avispa.cms.util.expression.parser.ExpressionParser;
import com.avispa.ecm.model.EcmEntity;
import com.avispa.ecm.util.expression.parser.ExpressionErrorListener;
import com.avispa.ecm.util.expression.parser.ExpressionVisitor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.springframework.stereotype.Component;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
@Component
public class ExpressionResolver {
    public String resolve(Object object, String expression) {
        try {
            // create lexer and get tokens
            Lexer lexer = new ExpressionLexer(CharStreams.fromString(expression));
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // run parser
            ExpressionParser parser = new ExpressionParser(tokens);
            parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
            parser.addErrorListener(new ExpressionErrorListener());
            ParseTree tree = parser.start();

            // go through parse tree
            ExpressionVisitor visitor = new ExpressionVisitor(object);

            return visitor.visit(tree);
        } catch (ParseCancellationException e) {
            log.error("Syntax error: {}", e.getMessage());
        }

        return expression;
    }
}
