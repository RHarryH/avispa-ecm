-- add SuperDocument type
SET @SuperDocumentTypeId=random_uuid();
insert into ecm_object (id, object_name, creation_date, modification_date, version) values (@SuperDocumentTypeId, 'Super Document', current_timestamp, current_timestamp, 0);
insert into type (id, class_name) values (@SuperDocumentTypeId, 'com.avispa.ecm.util.SuperDocument');