-- add CmsObject type
insert into cms_object (object_name, version) values ('CMS Object', 0);
insert into type (id, class_name) values (identity(), 'com.avispa.cms.model.CmsObject');

-- add Folder type
insert into cms_object (object_name, version) values ('Folder', 0);
insert into type (id, class_name) values (identity(), 'com.avispa.cms.model.folder.Folder');

-- add FileStore type
insert into cms_object (object_name, version) values ('FileStore', 0);
insert into type (id, class_name) values (identity(), 'com.avispa.cms.model.filestore.FileStore');

-- add Document type
insert into cms_object (object_name, version) values ('Document', 0);
insert into type (id, class_name) values (identity(), 'com.avispa.cms.model.document.Document');

-- add Content type
insert into cms_object (object_name, version) values ('Content', 0);
insert into type (id, class_name) values (identity(), 'com.avispa.cms.model.content.Content');

-- add Context type
insert into cms_object (object_name, version) values ('Context', 0);
insert into type (id, class_name) values (identity(), 'com.avispa.cms.model.context.Context');

-- add Autolink type
insert into cms_object (object_name, version) values ('Autolink', 0);
insert into type (id, class_name) values (identity(), 'com.avispa.cms.model.configuration.autolink.Autolink');

-- add Autoname type
insert into cms_object (object_name, version) values ('Autoname', 0);
insert into type (id, class_name) values (identity(), 'com.avispa.cms.model.configuration.autoname.Autoname');