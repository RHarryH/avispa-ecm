package com.avispa.ecm.model.configuration.dictionary;

import com.avispa.ecm.model.EcmObjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Rafał Hiszpański
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DictionaryService {
    private final EcmObjectRepository<Dictionary> dictionaryRepository;

    public Dictionary getDictionary(String dictionaryName) {
        return dictionaryRepository.findByObjectName(dictionaryName).orElseThrow(DictionaryNotFoundException::new);
    }
}
