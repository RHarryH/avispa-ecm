package com.avispa.ecm.util.parser;

import java.io.File;
import java.util.List;

/**
 * @author Rafał Hiszpański
 */
public interface IFileParser {
    List<List<String>> parse(File file);
}
