package com.avispa.ecm.model.configuration.autolink;

import com.avispa.ecm.model.configuration.EcmConfigService;
import com.avispa.ecm.model.document.Document;
import com.avispa.ecm.model.folder.Folder;
import com.avispa.ecm.model.folder.FolderService;
import com.avispa.ecm.util.expression.ExpressionResolver;
import lombok.RequiredArgsConstructor;
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
public class AutolinkService extends EcmConfigService<Autolink> {

    private final ExpressionResolver expressionResolver;
    private final FolderService folderService;

    public void apply(Autolink autolink, Document contextDocument) {
        final List<String> rules = autolink.getRules();

        Folder folder = null;
        Folder ancestor = null;

        for(String rule : rules) {
            String folderName = getFolderName(autolink, contextDocument, rule);

            folder = folderService.findFolderByObjectNameAndPath(folderName, null != ancestor ? ancestor.getPath() : "/");
            if(null == folder) { // if folder does not exist
                folder = ancestor = folderService.createNewFolder(folderName, ancestor);
            }
        }

        contextDocument.setFolder(folder);
    }

    private String getFolderName(Autolink autolink, Document contextDocument, String rule) {
        String name = expressionResolver.resolve(contextDocument, rule);
        if(StringUtils.isEmpty(name)) {
            name = autolink.getDefaultValue();
        }
        return name;
    }
}
