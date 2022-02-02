package com.avispa.ecm.util.parser;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Rafał Hiszpański
 */
@Component
public final class ParserFactory {
    @Value("${csv.separator:,}")
    private char attributeSeparator;

    /**
     * Get correct parser based on the extension of the file
     * @param extension
     * @return
     */
    public IFileParser get(String extension) {
        switch (extension) {
            case "txt":
                return new TXTParser();
            case "csv":
                return new CSVParser(attributeSeparator);
            /*case "xls":
                return new XLSParser();
            case "xlsx":
                return new XLSXParser();*/
            default:
                throw new IllegalArgumentException(String.format("Unsupported format: %s", extension));
        }
    }
}
