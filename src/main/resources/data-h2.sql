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
insert into type (id, class_name) values (@AutonameTypeId, 'Autoname');

-- supported formats
-- unknown
SET @DefaultFormatId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@DefaultFormatId, 'Default format', current_timestamp, current_timestamp, 0);
insert into format (id, description, mime_type, icon) values (@DefaultFormatId, 'Default format used when it is not officially supported by ECM', 'application/octet-stream', 'bi bi-file-earmark');

-- text formats
SET @PdfFormatId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@PdfFormatId, 'pdf', current_timestamp, current_timestamp, 0);
insert into format (id, description, mime_type, icon) values (@PdfFormatId, 'Adobe Portable Document Format (PDF)', 'application/pdf', 'bi bi-file-earmark-pdf');

SET @OdtFormatId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@OdtFormatId, 'odt', current_timestamp, current_timestamp, 0);
insert into format (id, description, mime_type, icon) values (@OdtFormatId, 'OpenDocument text document', 'application/vnd.oasis.opendocument.text', 'bi bi-file-richtext');

SET @RtfFormatId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@RtfFormatId, 'rtf', current_timestamp, current_timestamp, 0);
insert into format (id, description, mime_type, icon) values (@RtfFormatId, 'Rich Text Format (RTF)', 'application/rtf', 'bi bi-file-richtext');

SET @DocFormatId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@DocFormatId, 'doc', current_timestamp, current_timestamp, 0);
insert into format (id, description, mime_type, icon) values (@DocFormatId, 'Microsoft Word', 'application/msword', 'bi bi-file-earmark-word');

SET @DocxFormatId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@DocxFormatId, 'docx', current_timestamp, current_timestamp, 0);
insert into format (id, description, mime_type, icon) values (@DocxFormatId, 'Microsoft Word (OpenXML)', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'bi bi-file-earmark-word');

SET @CsvFormatId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@CsvFormatId, 'csv', current_timestamp, current_timestamp, 0);
insert into format (id, description, mime_type, icon) values (@CsvFormatId, 'Comma-separated values (CSV)', 'text/csv', 'bi bi-file-earmark-text');

SET @TxtFormatId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@TxtFormatId, 'txt', current_timestamp, current_timestamp, 0);
insert into format (id, description, mime_type, icon) values (@TxtFormatId, 'Text', 'text/plain', 'bi bi-file-earmark-text');

-- zipping formats
SET @ZipFormatId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@ZipFormatId, 'zip', current_timestamp, current_timestamp, 0);
insert into format (id, description, mime_type, icon) values (@ZipFormatId, 'ZIP archive', 'application/zip', 'bi bi-file-earmark-zip');

SET @RarFormatId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@RarFormatId, 'rar', current_timestamp, current_timestamp, 0);
insert into format (id, description, mime_type, icon) values (@RarFormatId, 'RAR archive', 'application/vnd.rar', 'bi bi-file-earmark-zip');

-- add Folder context
SET @FolderContextId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@FolderContextId, 'Folder context', current_timestamp, current_timestamp, 0);
insert into context (id, type_id) values (@FolderContextId, @FolderTypeId);

-- folder property page
SET @FolderPropertyPageId=random_uuid();
insert into ecm_config_object (id, object_name, creation_date, modification_date, version) values (@FolderPropertyPageId, 'Folder property page', current_timestamp, current_timestamp, 0);
insert into property_page (id) values (@FolderPropertyPageId);

SET @FolderPropertyPageObjectNameId=random_uuid();
insert into ecm_config_object (id, object_name, creation_date, modification_date, version) values (@FolderPropertyPageObjectNameId, 'Folder property page folder name', current_timestamp, current_timestamp, 0);
insert into property (id, name, label, type, required) values (@FolderPropertyPageObjectNameId, 'objectName', 'Folder name', 'text', true);
SET @FolderPropertyPagePathId=random_uuid();
insert into ecm_config_object (id, object_name, creation_date, modification_date, version) values (@FolderPropertyPagePathId, 'Folder property page folder path', current_timestamp, current_timestamp, 0);
insert into property (id, name, label, type, required) values (@FolderPropertyPagePathId, 'path', 'Folder path', 'text', true);
SET @FolderPropertyPageCreationDateId=random_uuid();
insert into ecm_config_object (id, object_name, creation_date, modification_date, version) values (@FolderPropertyPageCreationDateId, 'Folder property folder creation date', current_timestamp, current_timestamp, 0);
insert into property (id, name, label, type, required) values (@FolderPropertyPageCreationDateId, 'creationDate', 'Creation date', 'text', true);
SET @FolderPropertyPageModificationDateId=random_uuid();
insert into ecm_config_object (id, object_name, creation_date, modification_date, version) values (@FolderPropertyPageModificationDateId, 'Folder property folder modification date', current_timestamp, current_timestamp, 0);
insert into property (id, name, label, type, required) values (@FolderPropertyPageModificationDateId, 'modificationDate', 'Modification date', 'text', true);

insert into property_page_properties (property_page_id, properties_id, properties_order) values (@FolderPropertyPageId, @FolderPropertyPageObjectNameId, 0);
insert into property_page_properties (property_page_id, properties_id, properties_order) values (@FolderPropertyPageId, @FolderPropertyPagePathId, 1);
insert into property_page_properties (property_page_id, properties_id, properties_order) values (@FolderPropertyPageId, @FolderPropertyPageCreationDateId, 2);
insert into property_page_properties (property_page_id, properties_id, properties_order) values (@FolderPropertyPageId, @FolderPropertyPageModificationDateId, 3);

-- assign property page to the Folder Context
insert into context_ecm_config_objects (context_id, ecm_config_objects_id) values (@FolderContextId, @FolderPropertyPageId);
