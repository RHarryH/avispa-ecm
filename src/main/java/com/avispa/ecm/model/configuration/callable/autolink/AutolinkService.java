package com.avispa.ecm.model.configuration.callable.autolink;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.configuration.callable.CallableConfigService;
import com.avispa.ecm.model.folder.Folder;
import com.avispa.ecm.model.folder.FolderService;
import com.avispa.ecm.util.expression.ExpressionResolver;
import com.avispa.ecm.util.expression.ExpressionResolverException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Auto linking is a process of linking document to correct folder based on some predefined rule.
 * It creates folder tree but does not (TODO: what I wanted to add there)
 *
 * @author Rafał Hiszpański
 */
@Service
@RequiredArgsConstructor
@Slf4j
public final class AutolinkService implements CallableConfigService<Autolink> {

    private final ExpressionResolver expressionResolver;
    private final FolderService folderService;

    public void apply(Autolink autolink, EcmObject contextObject) {
        final List<String> rules = autolink.getRules();

        Folder folder = null;
        Folder ancestor = null;

        for(String rule : rules) {
            String folderName = getFolderName(autolink, contextObject, rule);

            folder = folderService.findFolderByNameAndAncestor(folderName, ancestor);
            if(null == folder) { // if folder does not exist
                ancestor = folder = folderService.createNewFolder(folderName, ancestor);
            } else {
                ancestor = folder;
            }
        }

        contextObject.setFolder(folder);
    }

    private String getFolderName(Autolink autolink, EcmObject ecmObject, String rule) {
        try {
            String name = expressionResolver.resolve(ecmObject, rule);
            if(StringUtils.isEmpty(name)) {
                name = autolink.getDefaultValue();
            }
            return name;
        } catch (ExpressionResolverException e) {
            log.error("Folder name can't be resolved from '{}' rule", rule, e);
        }

        return rule;
    }
}
