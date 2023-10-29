-- add Folder type
SET @FolderTypeId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@FolderTypeId, 'Folder', 0);
insert into ecm_object (id, creation_date, modification_date) values (@FolderTypeId, current_timestamp, current_timestamp);
insert into type (id, class_name) values (@FolderTypeId, 'com.avispa.ecm.model.folder.Folder');

-- add FileStore type
SET @FileStoreTypeId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@FileStoreTypeId, 'File Store', 0);
insert into ecm_object (id, creation_date, modification_date) values (@FileStoreTypeId, current_timestamp, current_timestamp);
insert into type (id, class_name) values (@FileStoreTypeId, 'com.avispa.ecm.model.filestore.FileStore');

-- add Document type
SET @DocumentTypeId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@DocumentTypeId, 'Document', 0);
insert into ecm_object (id, creation_date, modification_date) values (@DocumentTypeId, current_timestamp, current_timestamp);
insert into type (id, class_name) values (@DocumentTypeId, 'com.avispa.ecm.model.document.Document');

-- add Content type
SET @ContentTypeId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@ContentTypeId, 'Content', 0);
insert into ecm_object (id, creation_date, modification_date) values (@ContentTypeId, current_timestamp, current_timestamp);
insert into type (id, class_name) values (@ContentTypeId, 'com.avispa.ecm.model.content.Content');

-- add Format type
SET @FormatTypeId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@FormatTypeId, 'Format', 0);
insert into ecm_object (id, creation_date, modification_date) values (@FormatTypeId, current_timestamp, current_timestamp);
insert into type (id, class_name) values (@FormatTypeId, 'com.avispa.ecm.model.format.Format');

-- add Autolink type
SET @AutolinkTypeId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@AutolinkTypeId, 'Autolink', 0);
insert into ecm_object (id, creation_date, modification_date) values (@AutolinkTypeId, current_timestamp, current_timestamp);
insert into type (id, class_name) values (@AutolinkTypeId, 'com.avispa.ecm.model.configuration.callable.autolink.Autolink');

-- add Autoname type
SET @AutonameTypeId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@AutonameTypeId, 'Autoname', 0);
insert into ecm_object (id, creation_date, modification_date) values (@AutonameTypeId, current_timestamp, current_timestamp);
insert into type (id, class_name) values (@AutonameTypeId, 'com.avispa.ecm.model.configuration.callable.autoname.Autoname');

-- add Context type
SET @ContextTypeId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@ContextTypeId, 'Context', 0);
insert into ecm_object (id, creation_date, modification_date) values (@ContextTypeId, current_timestamp, current_timestamp);
insert into type (id, class_name) values (@ContextTypeId, 'com.avispa.ecm.model.configuration.context.Context');

-- add Dictionary type
SET @DictionaryTypeId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@DictionaryTypeId, 'Dictionary', 0);
insert into ecm_object (id, creation_date, modification_date) values (@DictionaryTypeId, current_timestamp, current_timestamp);
insert into type (id, class_name) values (@DictionaryTypeId, 'com.avispa.ecm.model.configuration.dictionary.Dictionary');

-- add Property Page type
SET @PropertyPageTypeId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@PropertyPageTypeId, 'Property Page', 0);
insert into ecm_object (id, creation_date, modification_date) values (@PropertyPageTypeId, current_timestamp, current_timestamp);
insert into type (id, class_name) values (@PropertyPageTypeId, 'com.avispa.ecm.model.configuration.propertypage.PropertyPage');

-- add Template type
SET @TemplateTypeId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@TemplateTypeId, 'Template', 0);
insert into ecm_object (id, creation_date, modification_date) values (@TemplateTypeId, current_timestamp, current_timestamp);
insert into type (id, class_name) values (@TemplateTypeId, 'com.avispa.ecm.model.configuration.template.Template');

-- add Upsert type
SET @UpsertTypeId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@UpsertTypeId, 'Upsert', 0);
insert into ecm_object (id, creation_date, modification_date) values (@UpsertTypeId, current_timestamp, current_timestamp);
insert into type (id, class_name) values (@UpsertTypeId, 'com.avispa.ecm.model.configuration.upsert.Upsert');

-- SUPPORTED FORMATS
-- unknown
SET @DefaultFormatId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@DefaultFormatId, 'Default format', 0);
insert into ecm_object (id, creation_date, modification_date) values (@DefaultFormatId, current_timestamp, current_timestamp);
insert into format (id, description, mime_type) values (@DefaultFormatId, 'Default format used when it is not officially supported by ECM', 'application/octet-stream');

-- text formats
SET @PdfFormatId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@PdfFormatId, 'pdf', 0);
insert into ecm_object (id, creation_date, modification_date) values (@PdfFormatId, current_timestamp, current_timestamp);
insert into format (id, description, mime_type) values (@PdfFormatId, 'Adobe Portable Document Format (PDF)', 'application/pdf');

SET @OdtFormatId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@OdtFormatId, 'odt', 0);
insert into ecm_object (id, creation_date, modification_date) values (@OdtFormatId, current_timestamp, current_timestamp);
insert into format (id, description, mime_type) values (@OdtFormatId, 'OpenDocument text document', 'application/vnd.oasis.opendocument.text');

SET @RtfFormatId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@RtfFormatId, 'rtf', 0);
insert into ecm_object (id, creation_date, modification_date) values (@RtfFormatId, current_timestamp, current_timestamp);
insert into format (id, description, mime_type) values (@RtfFormatId, 'Rich Text Format (RTF)', 'application/rtf');

SET @DocFormatId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@DocFormatId, 'doc', 0);
insert into ecm_object (id, creation_date, modification_date) values (@DocFormatId, current_timestamp, current_timestamp);
insert into format (id, description, mime_type) values (@DocFormatId, 'Microsoft Word', 'application/msword');

SET @DocxFormatId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@DocxFormatId, 'docx', 0);
insert into ecm_object (id, creation_date, modification_date) values (@DocxFormatId, current_timestamp, current_timestamp);
insert into format (id, description, mime_type) values (@DocxFormatId, 'Microsoft Word (OpenXML)', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document');

SET @CsvFormatId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@CsvFormatId, 'csv', 0);
insert into ecm_object (id, creation_date, modification_date) values (@CsvFormatId, current_timestamp, current_timestamp);
insert into format (id, description, mime_type) values (@CsvFormatId, 'Comma-separated values (CSV)', 'text/csv');

SET @TxtFormatId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@TxtFormatId, 'txt', 0);
insert into ecm_object (id, creation_date, modification_date) values (@TxtFormatId, current_timestamp, current_timestamp);
insert into format (id, description, mime_type) values (@TxtFormatId, 'Text', 'text/plain');

-- zipping formats
SET @ZipFormatId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@ZipFormatId, 'zip', 0);
insert into ecm_object (id, creation_date, modification_date) values (@ZipFormatId, current_timestamp, current_timestamp);
insert into format (id, description, mime_type) values (@ZipFormatId, 'ZIP archive', 'application/zip');

SET @RarFormatId=random_uuid();
insert into ecm_entity (id, object_name, version) values (@RarFormatId, 'rar', 0);
insert into ecm_object (id, creation_date, modification_date) values (@RarFormatId, current_timestamp, current_timestamp);
insert into format (id, description, mime_type) values (@RarFormatId, 'RAR archive', 'application/vnd.rar');
