INSERT INTO auth_permission (archived, version, created_by, created_on, updated_by, updated_on, can_create, can_export, can_read, can_remove, can_write, condition_value, condition_params, name, object, id) 
	VALUES (null, 0, 1, '2024-03-21 13:44:40.337732', null, null, TRUE, TRUE, TRUE, TRUE, TRUE, null, null, 'perm.auth.all', 'com.axelor.auth.db.*', nextval('auth_permission_seq'));

INSERT INTO meta_permission (archived, version, created_by, created_on, updated_by, updated_on, active, name, object, id)
    VALUES (NULL, 0, 1, (SELECT now()::timestamp), NULL,NULL , TRUE, 'perm.contact.isSubjectToTaxes.re', 'com.axelor.contact.db.Contact', nextval('meta_permission_seq'));
    
insert into meta_permission_rule (archived, version, created_by, created_on, updated_by, updated_on, can_export, can_read, can_write, field, hide_if, meta_permission, readonly_if, id) 
	values (null, 0 , 1, (SELECT now()::timestamp),null , null, TRUE, TRUE, FALSE,'isSubjectToTaxes' , null, 1 , null, nextval('meta_permission_rule_seq'));

INSERT INTO auth_permission (archived, version, created_by, created_on, updated_by, updated_on, can_create, can_export, can_read, can_remove, can_write, condition_value, condition_params, name, object, id)
VALUES (null, 0, 1, (SELECT CURRENT_TIMESTAMP), null, null, TRUE, TRUE, TRUE, TRUE, TRUE, null, null, 'perm.auth.all', 'com.axelor.auth.db.*', nextval('auth_permission_seq')),
       (null, 0, 1, (SELECT CURRENT_TIMESTAMP), null, null, TRUE, TRUE, FALSE, TRUE, TRUE, null, null, 'perm.auth.create', 'com.axelor.auth.db.*', nextval('auth_permission_seq')),
       (null, 0, 1, (SELECT CURRENT_TIMESTAMP), null, null, TRUE, TRUE, FALSE, TRUE, TRUE, null, null, 'perm.auth.self', 'com.axelor.auth.db.*', nextval('auth_permission_seq')),
       (null, 0, 1, (SELECT CURRENT_TIMESTAMP), null, null, TRUE, TRUE, FALSE, TRUE, TRUE, null, null, 'perm.auth.my', 'com.axelor.auth.db.*', nextval('auth_permission_seq'));

INSERT INTO auth_role (archived, version, created_by, created_on, updated_by, updated_on, description, name, id)
VALUES (null, 0, 1, (SELECT CURRENT_TIMESTAMP), null, null, 'Super', 'role.super', nextval('auth_role_seq')),
       (null, 0, 1, (SELECT CURRENT_TIMESTAMP), null, null, 'Moderator', 'role.moderator', nextval('auth_role_seq')),
       (null, 0, 1, (SELECT CURRENT_TIMESTAMP), null, null, 'Manager', 'role.manager', nextval('auth_role_seq')),
       (null, 0, 1, (SELECT CURRENT_TIMESTAMP), null, null, 'User', 'role.user', nextval('auth_role_seq'));

INSERT INTO auth_role_permissions (auth_role, permissions)
VALUES 
((SELECT id FROM auth_role WHERE name = 'CRM'), (SELECT id FROM auth_permission WHERE name = 'perm.address.rwcde')),
((SELECT id FROM auth_role WHERE name = 'CRM'), (SELECT id FROM auth_permission WHERE name = 'perm.contact.rwcde')),
((SELECT id FROM auth_role WHERE name = 'CRM'), (SELECT id FROM auth_permission WHERE name = 'perm.user.self.rw')),
((SELECT id FROM auth_role WHERE name = 'CRM'), (SELECT id FROM auth_permission WHERE name = 'perm.sale.re'));

insert into auth_user (archived, version, created_by, created_on, updated_by, updated_on, activate_on, blocked, code, email, expires_on, force_password_change, group_id, home_action, image, language, name, no_help, password, password_updated_on, single_tab, theme, id) 
values (null, 1,1 ,(SELECT now():: timestamp), null, (SELECT now():: timestamp), null, FALSE, 'hruser', 'hr@gmail.com',null,FALSE, null, null, null,null, 'Humane Resoorce', FALSE, 'hruser', null, FALSE, null, nextval('auth_user_seq'));