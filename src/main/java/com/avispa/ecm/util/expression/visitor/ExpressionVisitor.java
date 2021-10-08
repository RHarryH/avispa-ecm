package com.avispa.ecm.util.expression.visitor;

import com.avispa.cms.util.expression.parser.ExpressionsBaseVisitor;
import com.avispa.cms.util.expression.parser.ExpressionsParser;
import com.avispa.ecm.model.document.Document;
import com.avispa.ecm.util.expression.FunctionFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExpressionVisitor extends ExpressionsBaseVisitor<String> {

    private final Document document;

    public ExpressionVisitor(Document document) {
        this.document = document;
    }

    @Override
    public String visitFunction(ExpressionsParser.FunctionContext ctx) {
        String functionSignature = ctx.getText();
        String functionName = ctx.FUNCTION_NAME().getText();

        if(log.isDebugEnabled()) {
            log.debug("Function name: {}", functionName);
        }

        List<String> params = new ArrayList<>();

        processParams(ctx, params);

        if(log.isDebugEnabled()) {
            log.debug("Function params: {}", params);
        }

        String returnedValue = FunctionFactory.resolve(functionName.substring(1), params.toArray(new String[0]), document);
        if(null != returnedValue) {
            return returnedValue;
        } else {
            log.error("Can't resolve '{}' function", functionSignature);
            return functionSignature;
        }
    }

    private void processParams(ExpressionsParser.FunctionContext ctx, List<String> params) {
        if(null != ctx.params()) {
            for (ExpressionsParser.ExpressionContext child : ctx.params().expression()) {
                String param = this.visit(child);
                if (log.isDebugEnabled()) {
                    log.debug("Param: {}", param);
                }
                params.add(param);
            }
        }
    }

    @Override
    public String visitConcatExpr(ExpressionsParser.ConcatExprContext ctx) {
        String left = this.visit(ctx.left);
        String right = this.visit(ctx.right);
        String result = left + right;
        if(log.isDebugEnabled()) {
            log.debug("Concatenate result: {} ({} + {})", result, left, right);
        }
        return result;
    }

    @Override
    public String visitTextExpr(ExpressionsParser.TextExprContext ctx) {
        String string = ctx.getText();
        if(log.isDebugEnabled()) {
            log.debug("String: {}", string);
        }
        return string.substring(1, string.length() - 1);
    }
}