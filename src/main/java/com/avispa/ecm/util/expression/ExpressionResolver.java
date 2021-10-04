package com.avispa.ecm.util.expression;

import com.avispa.ecm.model.document.Document;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
@Component
public class ExpressionResolver {
    // function must start with upper or lower case letter, later digits are allowed,
    private static final String FUNCTION_REGEX = "\\$[A-Za-z]+[0-9]*\\((.*)\\)";
    private static final String FUNCTION_PARAMS_REGEX = "([^,]+\\(.+?\\))|([^,]+)";

    private final Pattern functionPattern = Pattern.compile(FUNCTION_REGEX);
    private final Pattern functionParamsPattern = Pattern.compile(FUNCTION_PARAMS_REGEX);

    public String resolve(Document document, String expression) {
        return resolveFunctions(document, expression);
    }

    /**
     * Checks if provided expression contains functions and tries to resolve them
     * @param document context document
     * @param expression expression to resolve
     * @return
     */
    private String resolveFunctions(Document document, String expression) {
        final StringBuilder sb = new StringBuilder(expression.length());

        Matcher m = functionPattern.matcher(expression);

        while (m.find()) {
            String functionSignature = m.group();
            log.info("Function '{}' found", functionSignature);

            String functionName = getFunctionName(functionSignature);
            String[] functionParams = getFunctionParams(document, m.group(1));

            String returnedValue = FunctionFactory.resolve(functionName, functionParams, document);
            if(null != returnedValue) {
                m.appendReplacement(sb, returnedValue);
            } else {
                log.error("Can't resolve '{}' function", functionSignature);
            }

        }
        m.appendTail(sb);

        return sb.toString();
    }

    /**
     * Retrieves function name from it's signature
     * @param signature
     * @return
     */
    private String getFunctionName(String signature) {
        return signature.substring(1, signature.indexOf("("));
    }

    /**
     * Returns array of function parameters retrieved from expression.
     * If parameter is another function or mix of functions, they will be resolved first
     * @param document context document
     * @param params function parameters extracted from the expression
     * @return
     */
    private String[] getFunctionParams(Document document, String params) {
        List<String> functionParams = new ArrayList<>();
        Matcher m = functionParamsPattern.matcher(params);

        while (m.find()) {
            String paramValue = resolveFunctions(document, m.group().trim()); // if parameter contains functions, resolve them first

            functionParams.add(paramValue);
            if(log.isDebugEnabled()) {
                log.debug("Function parameter found: {}", paramValue);
            }
        }
        return functionParams.toArray(new String[0]);
    }
}
