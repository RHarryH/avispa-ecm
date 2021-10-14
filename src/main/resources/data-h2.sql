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

-- add Context type
SET @ContextTypeId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@ContextTypeId, 'Context', current_timestamp, current_timestamp, 0);
insert into type (id, class_name) values (@ContextTypeId, 'com.avispa.ecm.model.context.Context');

-- add Autolink type
SET @AutolinkTypeId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@AutolinkTypeId, 'Autolink', current_timestamp, current_timestamp, 0);
insert into type (id, class_name) values (@AutolinkTypeId, 'com.avispa.ecm.model.configuration.autolink.Autolink');

-- add Autoname type
SET @AutonameTypeId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@AutonameTypeId, 'Autoname', current_timestamp, current_timestamp, 0);
insert into type (id, class_name) values (@AutonameTypeId, 'com.avispa.ecm.model.configuration.autoname.Autoname');