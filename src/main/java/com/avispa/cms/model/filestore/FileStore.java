package com.avispa.cms.model.filestore;

import com.avispa.cms.model.CmsObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Setter
public final class FileStore extends CmsObject {
    /*@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "repository_seq")
    @GenericGenerator(
            name = "repository_seq",
            strategy = "com.avispa.cms.util.CombinedSequenceIdGenerator",
            parameters = {
                    @Parameter(name = CombinedSequenceIdGenerator.INCREMENT_PARAM, value = "50"),
                    @Parameter(name = CombinedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "B_"),
                    @Parameter(name = CombinedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d") })
    private long repositoryId;*/

    @Column(nullable = false)
    private String rootPath;
}
