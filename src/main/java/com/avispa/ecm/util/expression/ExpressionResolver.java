package com.avispa.ecm.util.expression;

import com.avispa.cms.util.expression.parser.ExpressionsLexer;
import com.avispa.cms.util.expression.parser.ExpressionsParser;
import com.avispa.ecm.model.document.Document;
import com.avispa.ecm.util.expression.visitor.ExpressionVisitor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.springframework.stereotype.Component;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
@Component
public class ExpressionResolver {
    public String resolve(Document document, String expression) {
        // create lexer and get tokens
        Lexer lexer = new ExpressionsLexer(CharStreams.fromString(expression));
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // run parser
        ExpressionsParser parser = new ExpressionsParser(tokens);
        ParseTree tree = parser.start();

        // go through parse tree
        ExpressionVisitor visitor = new ExpressionVisitor(document);

        return visitor.visit(tree);
    }
}
