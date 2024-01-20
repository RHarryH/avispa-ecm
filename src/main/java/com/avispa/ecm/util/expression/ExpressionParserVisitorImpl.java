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

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExpressionParserVisitorImpl extends ExpressionParserBaseVisitor<String> {

    private final Object object;

    public ExpressionParserVisitorImpl(Object object) {
        this.object = object;
    }

    @Override
    public String visitSubString(ExpressionParser.SubStringContext ctx) {
        if (!ctx.PLAIN_TEXT().isEmpty()) {
            return ctx.getText();
        } else if (null != ctx.ESCAPE()) {
            return ctx.getText().substring(1);
        }

        return super.visitSubString(ctx);
    }

    @Override
    protected String aggregateResult(String aggregate, String nextResult) {
        return aggregate != null ? aggregate + nextResult : nextResult;
    }

    @Override
    public String visitFunction(com.avispa.ecm.util.expression.ExpressionParser.FunctionContext ctx) {
        String functionSignature = ctx.getText();
        String functionHeader = ctx.FUNCTION_HEADER().getText();
        String functionName = functionHeader.substring(0, functionHeader.length() - 1);

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

    private void processParams(com.avispa.ecm.util.expression.ExpressionParser.FunctionContext ctx, List<String> params) {
        if(null != ctx.params()) {
            for (com.avispa.ecm.util.expression.ExpressionParser.ExpressionContext child : ctx.params().expression()) {
                String param = this.visit(child);

                log.debug("Param: {}", param);

                params.add(param);
            }
        }
    }

    @Override
    public String visitConcatExpr(com.avispa.ecm.util.expression.ExpressionParser.ConcatExprContext ctx) {
        String left = this.visit(ctx.left);
        String right = this.visit(ctx.right);
        String result = left + right;

        log.debug("Concatenate result: {} ({} + {})", result, left, right);

        return result;
    }

    @Override
    public String visitTextExpr(com.avispa.ecm.util.expression.ExpressionParser.TextExprContext ctx) {
        String string = ctx.getText();

        log.debug("String: {}", string);

        string = string.replace("\\'", "'");

        return string.substring(1, string.length() - 1);
    }
}