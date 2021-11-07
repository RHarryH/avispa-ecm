-- add EcmObject type
SET @EcmObjectTypeId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@EcmObjectTypeId, 'ECM Object', current_timestamp, current_timestamp, 0);
insert into type (id, class_name) values (@EcmObjectTypeId, 'com.avispa.ecm.model.EcmObject');

-- add Folder type
SET @FolderTypeId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@FolderTypeId, 'Folder', current_timestamp, current_timestamp, 0);
insert into type (id, class_name) values (@FolderTypeId, 'com.avispa.ecm.model.folder.Folder');

-- add FileStore type
SET @FileStoreTypeId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@FileStoreTypeId, 'File Store', current_timestamp, current_timestamp, 0);
insert into type (id, class_name) values (@FileStoreTypeId, 'com.avispa.ecm.model.filestore.FileStore');

-- add Document type
SET @DocumentTypeId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@DocumentTypeId, 'Document', current_timestamp, current_timestamp, 0);
insert into type (id, class_name) values (@DocumentTypeId, 'com.avispa.ecm.model.document.Document');

-- add Content type
SET @ContentTypeId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@ContentTypeId, 'Content', current_timestamp, current_timestamp, 0);
insert into type (id, class_name) values (@ContentTypeId, 'com.avispa.ecm.model.content.Content');

-- add Format type
SET @FormatTypeId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@FormatTypeId, 'Content', current_timestamp, current_timestamp, 0);
insert into type (id, class_name) values (@FormatTypeId, 'com.avispa.ecm.model.format.Format');

-- add Context type
SET @ContextTypeId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@ContextTypeId, 'Context', current_timestamp, current_timestamp, 0);
insert into type (id, class_name) values (@ContextTypeId, 'com.avispa.ecm.model.context.Context');

-- add Autolink type
SET @AutolinkTypeId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@AutolinkTypeId, 'Autolink', current_timestamp, current_timestamp, 0);
insert into type (id, class_name) values (@AutolinkTypeId, 'com.avispa.ecm.model.configuration.callable.autolink.Autolink');

-- add Autoname type
SET @AutonameTypeId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@AutonameTypeId, 'Autoname', current_timestamp, current_timestamp, 0);
insert into type (id, class_name) values (@AutonameTypeId, 'com.avispa.ecm.model.configuration.callable.autoname.Autoname');

-- add Property Page type
SET @PropertyPageTypeId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@PropertyPageTypeId, 'Property Page', current_timestamp, current_timestamp, 0);
insert into type (id, class_name) values (@PropertyPageTypeId, 'com.avispa.ecm.model.configuration.propertypage.PropertyPage');

-- add Upsert type
SET @UpsertTypeId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@UpsertTypeId, 'Upsert', current_timestamp, current_timestamp, 0);
insert into type (id, class_name) values (@UpsertTypeId, 'com.avispa.ecm.model.configuration.upsert.Upsert');

-- SUPPORTED FORMATS
-- unknown
SET @DefaultFormatId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@DefaultFormatId, 'Default format', current_timestamp, current_timestamp, 0);
insert into format (id, description, mime_type) values (@DefaultFormatId, 'Default format used when it is not officially supported by ECM', 'application/octet-stream');

-- text formats
SET @PdfFormatId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@PdfFormatId, 'pdf', current_timestamp, current_timestamp, 0);
insert into format (id, description, mime_type) values (@PdfFormatId, 'Adobe Portable Document Format (PDF)', 'application/pdf');

SET @OdtFormatId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@OdtFormatId, 'odt', current_timestamp, current_timestamp, 0);
insert into format (id, description, mime_type) values (@OdtFormatId, 'OpenDocument text document', 'application/vnd.oasis.opendocument.text');

SET @RtfFormatId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@RtfFormatId, 'rtf', current_timestamp, current_timestamp, 0);
insert into format (id, description, mime_type) values (@RtfFormatId, 'Rich Text Format (RTF)', 'application/rtf');

SET @DocFormatId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@DocFormatId, 'doc', current_timestamp, current_timestamp, 0);
insert into format (id, description, mime_type) values (@DocFormatId, 'Microsoft Word', 'application/msword');

SET @DocxFormatId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@DocxFormatId, 'docx', current_timestamp, current_timestamp, 0);
insert into format (id, description, mime_type) values (@DocxFormatId, 'Microsoft Word (OpenXML)', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document');

SET @CsvFormatId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@CsvFormatId, 'csv', current_timestamp, current_timestamp, 0);
insert into format (id, description, mime_type) values (@CsvFormatId, 'Comma-separated values (CSV)', 'text/csv');

SET @TxtFormatId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@TxtFormatId, 'txt', current_timestamp, current_timestamp, 0);
insert into format (id, description, mime_type) values (@TxtFormatId, 'Text', 'text/plain');

-- zipping formats
SET @ZipFormatId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@ZipFormatId, 'zip', current_timestamp, current_timestamp, 0);
insert into format (id, description, mime_type) values (@ZipFormatId, 'ZIP archive', 'application/zip');

SET @RarFormatId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@RarFormatId, 'rar', current_timestamp, current_timestamp, 0);
insert into format (id, description, mime_type) values (@RarFormatId, 'RAR archive', 'application/vnd.rar');

-- FOLDER CONTEXT
SET @FolderContextId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@FolderContextId, 'Folder context', current_timestamp, current_timestamp, 0);
insert into context (id, type_id, importance) values (@FolderContextId, @FolderTypeId, 0);

-- DOCUMENT CONTEXT
SET @DocumentContextId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@DocumentContextId, 'Document context', current_timestamp, current_timestamp, 0);
insert into context (id, type_id, importance) values (@DocumentContextId, @DocumentTypeId, 1);

-- FOLDER PROPERTY PAGE
SET @FolderPropertyPageId=random_uuid();
insert into ecm_config_object (id, object_name, creation_date, modification_date, version) values (@FolderPropertyPageId, 'Folder property page', current_timestamp, current_timestamp, 0);
insert into property_page (id) values (@FolderPropertyPageId);

-- add controls
SET @FolderPropertyPageObjectNameId=random_uuid();
insert into control (id, object_name, creation_date, modification_date, version, label, control_type, specific_control_type, required)
    values (@FolderPropertyPageObjectNameId, 'Folder property page folder name', current_timestamp, current_timestamp, 0, '''Properties of "'' + $value(''objectName'') + ''" folder''', 'organization_control', 'label', true);
SET @FolderPropertyPageSeparatorId=random_uuid();
insert into control (id, object_name, creation_date, modification_date, version, label, control_type, specific_control_type, required)
values (@FolderPropertyPageSeparatorId, 'Folder property page separator', current_timestamp, current_timestamp, 0, 'Separator', 'organization_control', 'separator', true);
SET @FolderPropertyPagePathId=random_uuid();
insert into control (id, object_name, creation_date, modification_date, version, name, label, control_type, specific_control_type, required)
    values (@FolderPropertyPagePathId, 'Folder property page folder path', current_timestamp, current_timestamp, 0, 'path', 'Folder path', 'property_control', 'text', true);
SET @FolderPropertyPageCreationDateId=random_uuid();
insert into control (id, object_name, creation_date, modification_date, version, name, label, control_type, specific_control_type, required)
    values (@FolderPropertyPageCreationDateId, 'Folder property folder creation date', current_timestamp, current_timestamp, 0, 'creationDate', 'Creation date', 'property_control', 'text', true);
SET @FolderPropertyPageModificationDateId=random_uuid();
insert into control (id, object_name, creation_date, modification_date, version, name, label, control_type, specific_control_type, required)
    values (@FolderPropertyPageModificationDateId, 'Folder property folder modification date', current_timestamp, current_timestamp, 0, 'modificationDate', 'Modification date', 'property_control', 'text', true);

-- attach controls to property page
insert into property_page_controls (property_page_id, controls_id, controls_order) values (@FolderPropertyPageId, @FolderPropertyPageObjectNameId, 0);
insert into property_page_controls (property_page_id, controls_id, controls_order) values (@FolderPropertyPageId, @FolderPropertyPageSeparatorId, 1);
insert into property_page_controls (property_page_id, controls_id, controls_order) values (@FolderPropertyPageId, @FolderPropertyPagePathId, 2);
insert into property_page_controls (property_page_id, controls_id, controls_order) values (@FolderPropertyPageId, @FolderPropertyPageCreationDateId, 3);
insert into property_page_controls (property_page_id, controls_id, controls_order) values (@FolderPropertyPageId, @FolderPropertyPageModificationDateId, 4);

-- DOCUMENT PROPERTY PAGE
SET @DocumentPropertyPageId=random_uuid();
insert into ecm_config_object (id, object_name, creation_date, modification_date, version) values (@DocumentPropertyPageId, 'Document property page', current_timestamp, current_timestamp, 0);
insert into property_page (id) values (@DocumentPropertyPageId);

-- add controls
SET @DocumentPropertyPageObjectNameId=random_uuid();
insert into control (id, object_name, creation_date, modification_date, version, label, control_type, specific_control_type, required)
values (@DocumentPropertyPageObjectNameId, 'Document property page document name', current_timestamp, current_timestamp, 0, '''Properties of "'' + $value(''objectName'') + ''" document''', 'organization_control', 'label', true);
SET @DocumentPropertyPageSeparatorId=random_uuid();
insert into control (id, object_name, creation_date, modification_date, version, label, control_type, specific_control_type, required)
values (@DocumentPropertyPageSeparatorId, 'Document property page separator', current_timestamp, current_timestamp, 0, 'Separator', 'organization_control', 'separator', true);
SET @DocumentPropertyPageCreationDateId=random_uuid();
insert into control (id, object_name, creation_date, modification_date, version, name, label, control_type, specific_control_type, required)
values (@DocumentPropertyPageCreationDateId, 'Document property creation date', current_timestamp, current_timestamp, 0, 'creationDate', 'Creation date', 'property_control', 'text', true);
SET @DocumentPropertyPageModificationDateId=random_uuid();
insert into control (id, object_name, creation_date, modification_date, version, name, label, control_type, specific_control_type, required)
values (@DocumentPropertyPageModificationDateId, 'Document property modification date', current_timestamp, current_timestamp, 0, 'modificationDate', 'Modification date', 'property_control', 'text', true);

-- attach controls to property page
insert into property_page_controls (property_page_id, controls_id, controls_order) values (@DocumentPropertyPageId, @DocumentPropertyPageObjectNameId, 0);
insert into property_page_controls (property_page_id, controls_id, controls_order) values (@DocumentPropertyPageId, @DocumentPropertyPageSeparatorId, 1);
insert into property_page_controls (property_page_id, controls_id, controls_order) values (@DocumentPropertyPageId, @DocumentPropertyPageCreationDateId, 2);
insert into property_page_controls (property_page_id, controls_id, controls_order) values (@DocumentPropertyPageId, @DocumentPropertyPageModificationDateId, 3);

-- assign Folder Property Page to the Folder Context
insert into context_ecm_config_objects (context_id, ecm_config_objects_id) values (@FolderContextId, @FolderPropertyPageId);

-- assign Document Property Page to the Document Context
insert into context_ecm_config_objects (context_id, ecm_config_objects_id) values (@DocumentContextId, @DocumentPropertyPageId);
