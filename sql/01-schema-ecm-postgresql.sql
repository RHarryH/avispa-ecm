CREATE TABLE IF NOT EXISTS ecm_entity (
                                          id CHAR(36) DEFAULT gen_random_uuid() NOT NULL,
    object_name VARCHAR(255),
    version INT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS ecm_object (
    creation_date TIMESTAMP,
    modification_date TIMESTAMP,
    id        CHAR(36) NOT NULL,
    folder_id CHAR(36),
    PRIMARY KEY (id),
    CONSTRAINT fk_ecm_object_ecm_entity_id FOREIGN KEY (id) REFERENCES ecm_entity
);

CREATE TABLE IF NOT EXISTS format (
    description VARCHAR(255),
    mime_type VARCHAR(255),
    id CHAR(36) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_format_ecm_object_id FOREIGN KEY (id) REFERENCES ecm_object
);

CREATE TABLE IF NOT EXISTS content (
    file_store_path VARCHAR(255),
    size BIGINT NOT NULL,
    id                CHAR(36) NOT NULL,
    format_id VARCHAR(255) NOT NULL,
    related_entity_id CHAR(36) NOT NULL,
    PRIMARY KEY (id),
	CONSTRAINT fk_content_format_id FOREIGN KEY (format_id) REFERENCES format,
	CONSTRAINT fk_content_related_entity_id FOREIGN KEY (related_entity_id) REFERENCES ecm_entity,
	CONSTRAINT fk_content_ecm_object_id FOREIGN KEY (id) REFERENCES ecm_object
);

CREATE TABLE IF NOT EXISTS document (
                                        id CHAR(36) NOT NULL,
    PRIMARY KEY (id),
	CONSTRAINT fk_document_ecm_object_id FOREIGN KEY (id) REFERENCES ecm_object
);

CREATE TABLE IF NOT EXISTS file_store (
    id VARCHAR(255) NOT NULL,
    root_path CHAR(36) NOT NULL,
    PRIMARY KEY (id),
	CONSTRAINT fk_file_store_ecm_object_id FOREIGN KEY (id) REFERENCES ecm_object
);

CREATE TABLE IF NOT EXISTS folder (
                                      id CHAR(36) NOT NULL,
    path VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
	CONSTRAINT fk_folder_ecm_object_id FOREIGN KEY (id) REFERENCES ecm_object
);

ALTER TABLE ecm_object ADD CONSTRAINT fk_ecm_object_folder_id FOREIGN KEY (folder_id) REFERENCES folder;

CREATE TABLE IF NOT EXISTS folder_ancestors (
                                                folder_id    CHAR(36) NOT NULL,
                                                ancestors_id CHAR(36) NOT NULL,
    distance INT NOT NULL,
    PRIMARY KEY (folder_id, distance),
	CONSTRAINT fk_folder_ancestors_ancestors_id FOREIGN KEY (ancestors_id) REFERENCES folder,
	CONSTRAINT fk_folder_ancestors_folder_id FOREIGN KEY (folder_id) REFERENCES folder
);

CREATE TABLE IF NOT EXISTS type (
    class_name VARCHAR(255) NOT NULL,
    id CHAR(36) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_type_class_name UNIQUE (class_name),
    CONSTRAINT fk_type_ecm_object_id FOREIGN KEY (id) REFERENCES ecm_object
);

CREATE TABLE IF NOT EXISTS dto_object (
    discriminator VARCHAR(255),
    dto_name VARCHAR(255) NOT NULL,
    id      CHAR(36) NOT NULL,
    type_id CHAR(36) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_dto_object_dto_name UNIQUE (dto_name),
    CONSTRAINT fk_dto_object_type_id FOREIGN KEY (type_id) REFERENCES type,
    CONSTRAINT fk_dto_object_ecm_object_id FOREIGN KEY (id) REFERENCES ecm_object
);

CREATE TABLE IF NOT EXISTS ecm_config (
                                          id CHAR(36) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_ecm_config_ecm_entity_id FOREIGN KEY (id) REFERENCES ecm_entity
);

CREATE TABLE IF NOT EXISTS autolink (
    default_value VARCHAR(255) DEFAULT 'Unknown' NOT NULL,
    id CHAR(36) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_autolink_ecm_config_id FOREIGN KEY (id) REFERENCES ecm_config
    );

CREATE TABLE IF NOT EXISTS autolink_rules (
                                              autolink_id CHAR(36) NOT NULL,
    rules VARCHAR(255),
    rules_order INT NOT NULL,
    PRIMARY KEY (autolink_id, rules_order),
    CONSTRAINT fk_autolink_rules_autolink_id FOREIGN KEY (autolink_id) REFERENCES autolink
    );

CREATE TABLE IF NOT EXISTS autoname (
    property_name VARCHAR(255) NOT NULL,
    rule VARCHAR(255) NOT NULL,
    id CHAR(36) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_autoname_ecm_config_id FOREIGN KEY (id) REFERENCES ecm_config
);

CREATE TABLE IF NOT EXISTS context (
    importance INT NOT NULL,
    match_rule VARCHAR(255) DEFAULT '{}' NOT NULL,
    id      CHAR(36) NOT NULL,
    type_id CHAR(36) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_context_type_id FOREIGN KEY (type_id) REFERENCES type,
    CONSTRAINT fk_context_ecm_config_id FOREIGN KEY (id) REFERENCES ecm_config
);

CREATE TABLE IF NOT EXISTS context_ecm_configs (
                                                   context_id     CHAR(36) NOT NULL,
                                                   ecm_configs_id CHAR(36) NOT NULL,
    CONSTRAINT fk_context_ecm_configs_ecm_config_id FOREIGN KEY (ecm_configs_id) REFERENCES ecm_config,
    CONSTRAINT fk_context_ecm_configs_context_id FOREIGN KEY (context_id) REFERENCES context
);

CREATE TABLE IF NOT EXISTS dictionary (
    description VARCHAR(255),
    id CHAR(36) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_dictionary_ecm_config_id FOREIGN KEY (id) REFERENCES ecm_config
);

CREATE TABLE IF NOT EXISTS dictionary_value (
    label VARCHAR(255) NOT NULL,
    id CHAR(36) NOT NULL,
    dictionary_id VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_dictionary_value_dictionary_id FOREIGN KEY (dictionary_id) REFERENCES dictionary,
    CONSTRAINT fk_dictionary_value_ecm_config_id FOREIGN KEY (id) REFERENCES ecm_config
);

CREATE TABLE IF NOT EXISTS dictionary_value_columns (
                                                        dictionary_value_id CHAR(36) NOT NULL,
    columns VARCHAR(255),
    columns_key VARCHAR(255) NOT NULL,
    PRIMARY KEY (dictionary_value_id, columns_key),
    CONSTRAINT fk_dictionary_value_columns_dictionary_value_id FOREIGN KEY (dictionary_value_id) REFERENCES dictionary_value
);

CREATE TABLE IF NOT EXISTS property_page (
                                             id CHAR(36) NOT NULL,
    PRIMARY KEY (id),
	CONSTRAINT fk_property_page_ecm_config_id FOREIGN KEY (id) REFERENCES ecm_config
);

CREATE TABLE IF NOT EXISTS template (
                                        id CHAR(36) NOT NULL,
    PRIMARY KEY (id),
	CONSTRAINT fk_template_ecm_config_id FOREIGN KEY (id) REFERENCES ecm_config
);

CREATE TABLE IF NOT EXISTS upsert (
                                      id               CHAR(36) NOT NULL,
                                      property_page_id CHAR(36) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_upsert_property_page FOREIGN KEY (property_page_id) REFERENCES property_page,
    CONSTRAINT fk_upsert_ecm_config FOREIGN KEY (id) REFERENCES ecm_config
);