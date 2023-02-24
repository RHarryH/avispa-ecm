package com.avispa.ecm.util.expression.parser;

import com.avispa.ecm.util.expression.FunctionFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExpressionVisitorImpl extends ExpressionBaseVisitor<String> {

    private final Object object;

    public ExpressionVisitorImpl(Object object) {
        this.object = object;
    }

    @Override
    public String visitFunction(ExpressionParser.FunctionContext ctx) {
        String functionSignature = ctx.getText();
        String functionName = ctx.FUNCTION_NAME().getText();

        log.debug("Function name: {}", functionName);

        List<String> params = new ArrayList<>();

        processParams(ctx, params);

        log.debug("Function params: {}", params);

        String returnedValue = FunctionFactory.resolve(functionName.substring(1), params.toArray(new String[0]), object);
        if(null != returnedValue) {
            return returnedValue;
        } else {
            log.error("Can't resolve '{}' function", functionSignature);
            return functionSignature;
        }
    }

    private void processParams(ExpressionParser.FunctionContext ctx, List<String> params) {
        if(null != ctx.params()) {
            for (ExpressionParser.ExpressionContext child : ctx.params().expression()) {
                String param = this.visit(child);

                log.debug("Param: {}", param);

                params.add(param);
            }
        }
    }

    @Override
    public String visitConcatExpr(ExpressionParser.ConcatExprContext ctx) {
        String left = this.visit(ctx.left);
        String right = this.visit(ctx.right);
        String result = left + right;

        log.debug("Concatenate result: {} ({} + {})", result, left, right);

        return result;
    }

    @Override
    public String visitTextExpr(ExpressionParser.TextExprContext ctx) {
        String string = ctx.getText();

        log.debug("String: {}", string);

        return string.substring(1, string.length() - 1);
    }
}