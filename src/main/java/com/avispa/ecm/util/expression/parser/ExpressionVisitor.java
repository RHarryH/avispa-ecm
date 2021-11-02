package com.avispa.ecm.util.expression.parser;

import com.avispa.cms.util.expression.parser.ExpressionBaseVisitor;
import com.avispa.cms.util.expression.parser.ExpressionParser;
import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.util.expression.FunctionFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExpressionVisitor extends ExpressionBaseVisitor<String> {

    private final EcmObject ecmObject;

    public ExpressionVisitor(EcmObject ecmObject) {
        this.ecmObject = ecmObject;
    }

    @Override
    public String visitFunction(ExpressionParser.FunctionContext ctx) {
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

        String returnedValue = FunctionFactory.resolve(functionName.substring(1), params.toArray(new String[0]), ecmObject);
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
                if (log.isDebugEnabled()) {
                    log.debug("Param: {}", param);
                }
                params.add(param);
            }
        }
    }

    @Override
    public String visitConcatExpr(ExpressionParser.ConcatExprContext ctx) {
        String left = this.visit(ctx.left);
        String right = this.visit(ctx.right);
        String result = left + right;
        if(log.isDebugEnabled()) {
            log.debug("Concatenate result: {} ({} + {})", result, left, right);
        }
        return result;
    }

    @Override
    public String visitTextExpr(ExpressionParser.TextExprContext ctx) {
        String string = ctx.getText();
        if(log.isDebugEnabled()) {
            log.debug("String: {}", string);
        }
        return string.substring(1, string.length() - 1);
    }
}