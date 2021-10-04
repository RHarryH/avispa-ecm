package com.avispa.ecm.util.expression;

import com.avispa.ecm.model.document.Document;
import com.avispa.ecm.util.expression.function.Function;
import com.avispa.ecm.util.expression.function.impl.DateValue;
import com.avispa.ecm.util.expression.function.impl.Default;
import com.avispa.ecm.util.expression.function.impl.Pad;
import com.avispa.ecm.util.expression.function.impl.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
class FunctionFactory {
    private static final String VALUE_FUNCTION = "value";
    private static final String DATEVALUE_FUNCTION = "datevalue";
    private static final String DEFAULT_FUNCTION = "default";
    private static final String PAD_FUNCTION = "pad";

    private FunctionFactory() {

    }

    public static String resolve(String functionName, String[] functionParams, Document document) {
        Function function;

        switch(functionName) {
            case VALUE_FUNCTION:
                function = new Value();
                break;
            case DATEVALUE_FUNCTION:
                function = new DateValue();
                break;
            case DEFAULT_FUNCTION:
                function = new Default();
                break;
            case PAD_FUNCTION:
                function = new Pad();
                break;
            default:
                log.error("Unknown function '{}'", functionName);
                return null;
        }

        return resolveFunction(document, functionParams, function);
    }

    private static String resolveFunction(Document document, String[] functionParams, Function function) {
        String r = function.resolve(document, functionParams);

        return Matcher.quoteReplacement(r); // runs quoteReplacement to escape slashes and dollar characters
    }
}
