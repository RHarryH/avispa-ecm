-- Add EcmObject type
DO $$
DECLARE
    EcmObjectTypeId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (EcmObjectTypeId, 'ECM Object', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (EcmObjectTypeId, current_timestamp, current_timestamp);
    INSERT INTO type (id, class_name) VALUES (EcmObjectTypeId, 'com.avispa.ecm.model.EcmObject');
END $$;

-- Add Folder type
DO $$
DECLARE
    FolderTypeId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (FolderTypeId, 'Folder', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (FolderTypeId, current_timestamp, current_timestamp);
    INSERT INTO type (id, class_name) VALUES (FolderTypeId, 'com.avispa.ecm.model.folder.Folder');
END $$;

-- Add FileStore type
DO $$
DECLARE
    FileStoreTypeId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (FileStoreTypeId, 'File Store', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (FileStoreTypeId, current_timestamp, current_timestamp);
    INSERT INTO type (id, class_name) VALUES (FileStoreTypeId, 'com.avispa.ecm.model.filestore.FileStore');
END $$;

-- Add Document type
DO $$
DECLARE
    DocumentTypeId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (DocumentTypeId, 'Document', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (DocumentTypeId, current_timestamp, current_timestamp);
    INSERT INTO type (id, class_name) VALUES (DocumentTypeId, 'com.avispa.ecm.model.document.Document');
END $$;

-- Add Content type
DO $$
DECLARE
    ContentTypeId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (ContentTypeId, 'Content', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (ContentTypeId, current_timestamp, current_timestamp);
    INSERT INTO type (id, class_name) VALUES (ContentTypeId, 'com.avispa.ecm.model.content.Content');
END $$;

-- Add Format type
DO $$
DECLARE
    FormatTypeId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (FormatTypeId, 'Format', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (FormatTypeId, current_timestamp, current_timestamp);
    INSERT INTO type (id, class_name) VALUES (FormatTypeId, 'com.avispa.ecm.model.format.Format');
END $$;

-- Add Autolink type
DO $$
DECLARE
    AutolinkTypeId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (AutolinkTypeId, 'Autolink', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (AutolinkTypeId, current_timestamp, current_timestamp);
    INSERT INTO type (id, class_name) VALUES (AutolinkTypeId, 'com.avispa.ecm.model.configuration.callable.autolink.Autolink');
END $$;

-- Add Autoname type
DO $$
DECLARE
    AutonameTypeId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (AutonameTypeId, 'Autoname', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (AutonameTypeId, current_timestamp, current_timestamp);
    INSERT INTO type (id, class_name) VALUES (AutonameTypeId, 'com.avispa.ecm.model.configuration.callable.autoname.Autoname');
END $$;

-- Add Context type
DO $$
DECLARE
    ContextTypeId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (ContextTypeId, 'Context', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (ContextTypeId, current_timestamp, current_timestamp);
    INSERT INTO type (id, class_name) VALUES (ContextTypeId, 'com.avispa.ecm.model.configuration.context.Context');
END $$;

-- Add Dictionary type
DO $$
DECLARE
    DictionaryTypeId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (DictionaryTypeId, 'Dictionary', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (DictionaryTypeId, current_timestamp, current_timestamp);
    INSERT INTO type (id, class_name) VALUES (DictionaryTypeId, 'com.avispa.ecm.model.configuration.dictionary.Dictionary');
END $$;

-- Add Property Page type
DO $$
DECLARE
    PropertyPageTypeId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (PropertyPageTypeId, 'Property Page', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (PropertyPageTypeId, current_timestamp, current_timestamp);
    INSERT INTO type (id, class_name) VALUES (PropertyPageTypeId, 'com.avispa.ecm.model.configuration.propertypage.PropertyPage');
END $$;

-- Add Template type
DO $$
DECLARE
    TemplateTypeId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (TemplateTypeId, 'Template', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (TemplateTypeId, current_timestamp, current_timestamp);
    INSERT INTO type (id, class_name) VALUES (TemplateTypeId, 'com.avispa.ecm.model.configuration.template.Template');
END $$;

-- Add Upsert type
DO $$
DECLARE
    UpsertTypeId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (UpsertTypeId, 'Upsert', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (UpsertTypeId, current_timestamp, current_timestamp);
    INSERT INTO type (id, class_name) VALUES (UpsertTypeId, 'com.avispa.ecm.model.configuration.upsert.Upsert');
END $$;

-- Supported Formats
-- unknown
DO $$
DECLARE
    DefaultFormatId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (DefaultFormatId, 'Default format', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (DefaultFormatId, current_timestamp, current_timestamp);
    INSERT INTO format (id, description, mime_type) VALUES (DefaultFormatId, 'Default format used when it is not officially supported by ECM', 'application/octet-stream');
END $$;

-- text formats
DO $$
DECLARE
    PdfFormatId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (PdfFormatId, 'pdf', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (PdfFormatId, current_timestamp, current_timestamp);
    INSERT INTO format (id, description, mime_type) VALUES (PdfFormatId, 'Adobe Portable Document Format (PDF)', 'application/pdf');
END $$;

DO $$
DECLARE
    OdtFormatId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (OdtFormatId, 'odt', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (OdtFormatId, current_timestamp, current_timestamp);
    INSERT INTO format (id, description, mime_type) VALUES (OdtFormatId, 'OpenDocument text document', 'application/vnd.oasis.opendocument.text');
END $$;

DO $$
DECLARE
    RtfFormatId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (RtfFormatId, 'rtf', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (RtfFormatId, current_timestamp, current_timestamp);
    INSERT INTO format (id, description, mime_type) VALUES (RtfFormatId, 'Rich Text Format (RTF)', 'application/rtf');
END $$;

DO $$
DECLARE
    DocFormatId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (DocFormatId, 'doc', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (DocFormatId, current_timestamp, current_timestamp);
    INSERT INTO format (id, description, mime_type) VALUES (DocFormatId, 'Microsoft Word', 'application/msword');
END $$;

-- docx format
DO $$
DECLARE
    DocxFormatId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (DocxFormatId, 'docx', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (DocxFormatId, current_timestamp, current_timestamp);
    INSERT INTO format (id, description, mime_type) VALUES (DocxFormatId, 'Microsoft Word (OpenXML)', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document');
END $$;

-- csv format
DO $$
DECLARE
    CsvFormatId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (CsvFormatId, 'csv', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (CsvFormatId, current_timestamp, current_timestamp);
    INSERT INTO format (id, description, mime_type) VALUES (CsvFormatId, 'Comma-separated values (CSV)', 'text/csv');
END $$;

-- txt format
DO $$
DECLARE
    TxtFormatId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (TxtFormatId, 'txt', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (TxtFormatId, current_timestamp, current_timestamp);
    INSERT INTO format (id, description, mime_type) VALUES (TxtFormatId, 'Text', 'text/plain');
END $$;

-- zipping formats
DO $$
DECLARE
    ZipFormatId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (ZipFormatId, 'zip', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (ZipFormatId, current_timestamp, current_timestamp);
    INSERT INTO format (id, description, mime_type) VALUES (ZipFormatId, 'ZIP archive', 'application/zip');
END $$;

DO $$
DECLARE
    RarFormatId UUID := gen_random_uuid();
BEGIN
    INSERT INTO ecm_entity (id, object_name, version) VALUES (RarFormatId, 'rar', 0);
    INSERT INTO ecm_object (id, creation_date, modification_date) VALUES (RarFormatId, current_timestamp, current_timestamp);
    INSERT INTO format (id, description, mime_type) VALUES (RarFormatId, 'RAR archive', 'application/vnd.rar');
END $$;