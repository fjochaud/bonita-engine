-- ------------------------------------------------ Foreign Keys -----------------------------------------------
ALTER TABLE actor DROP CONSTRAINT fk_actor_tenantId;
ALTER TABLE actormember DROP CONSTRAINT fk_actormember_tenantId;
ALTER TABLE actormember DROP CONSTRAINT fk_actormember_actorId;
ALTER TABLE breakpoint DROP CONSTRAINT fk_breakpoint_tenantId;
-- ALTER TABLE queriable_log DROP CONSTRAINT fk_queriable_log_tenantId;
ALTER TABLE queriablelog_p DROP CONSTRAINT fk_queriablelog_p_tenantId;
ALTER TABLE category DROP CONSTRAINT fk_category_tenantId;
ALTER TABLE command DROP CONSTRAINT fk_command_tenantId;
ALTER TABLE connector_instance DROP CONSTRAINT fk_connector_instance_tenantId;
ALTER TABLE data_instance DROP CONSTRAINT fk_data_instance_tenantId;
ALTER TABLE data_mapping DROP CONSTRAINT fk_data_mapping_tenantId;
ALTER TABLE datasource DROP CONSTRAINT fk_datasource_tenantId;
ALTER TABLE datasourceparameter DROP CONSTRAINT fk_datasourceparameter_tenantId;
ALTER TABLE dependency DROP CONSTRAINT fk_dependency_tenantId;
ALTER TABLE dependencymapping DROP CONSTRAINT fk_dependencymapping_tenantId;
ALTER TABLE document_content DROP CONSTRAINT fk_document_content_tenantId;
ALTER TABLE document_mapping DROP CONSTRAINT fk_document_mapping_tenantId;
ALTER TABLE event_trigger_instance DROP CONSTRAINT fk_event_trigger_instance_tenantId;
ALTER TABLE external_identity_mapping DROP CONSTRAINT fk_external_identity_mapping_tenantId;
ALTER TABLE flownode_instance DROP CONSTRAINT fk_flownode_instance_tenantId;
ALTER TABLE group_ DROP CONSTRAINT fk_group__tenantId;
ALTER TABLE hidden_activity DROP CONSTRAINT fk_hidden_activity_tenantId;
ALTER TABLE job_desc DROP CONSTRAINT fk_job_desc_tenantId;
ALTER TABLE job_param DROP CONSTRAINT fk_job_param_tenantId;
ALTER TABLE message_instance DROP CONSTRAINT fk_message_instance_tenantId;
-- ALTER TABLE migration_plan DROP CONSTRAINT fk_migration_plan_tenantId;
ALTER TABLE p_metadata_def DROP CONSTRAINT fk_p_metadata_def_tenantId;
ALTER TABLE p_metadata_val DROP CONSTRAINT fk_p_metadata_val_tenantId;
ALTER TABLE pending_mapping DROP CONSTRAINT fk_pending_mapping_tenantId;
ALTER TABLE pending_mapping DROP CONSTRAINT fk_pending_mapping_flownode_instanceId;
ALTER TABLE processcategorymapping DROP CONSTRAINT fk_processcategorymapping_tenantId;
ALTER TABLE process_comment DROP CONSTRAINT fk_process_comment_tenantId;
ALTER TABLE process_definition DROP CONSTRAINT fk_process_definition_tenantId;
ALTER TABLE process_instance DROP CONSTRAINT fk_process_instance_tenantId;
ALTER TABLE processsupervisor DROP CONSTRAINT fk_processsupervisor_tenantId;
ALTER TABLE profile DROP CONSTRAINT fk_profile_tenantId;
ALTER TABLE profileentry DROP CONSTRAINT fk_profileentry_tenantId;
ALTER TABLE profilemember DROP CONSTRAINT fk_profilemember_tenantId;
ALTER TABLE role DROP CONSTRAINT fk_role_tenantId;
ALTER TABLE theme DROP CONSTRAINT fk_theme_tenantId;
ALTER TABLE user_ DROP CONSTRAINT fk_user__tenantId;
ALTER TABLE user_membership DROP CONSTRAINT fk_user_membership_tenantId;
ALTER TABLE waiting_event DROP CONSTRAINT fk_waiting_event_tenantId;

ALTER TABLE profilemember DROP CONSTRAINT fk_profilemember_profileId;
ALTER TABLE profileentry DROP CONSTRAINT fk_profileentry_profileId;
-- ALTER TABLE process_comment DROP CONSTRAINT fk_process_comment_process_instanceId;


--  ------------------------ Foreign Keys to disable if archiving is on another BD ------------------
ALTER TABLE arch_document_mapping DROP CONSTRAINT fk_arch_document_mapping_tenantId;
ALTER TABLE arch_flownode_instance DROP CONSTRAINT fk_arch_flownode_instance_tenantId;
ALTER TABLE arch_process_comment DROP CONSTRAINT fk_arch_process_comment_tenantId;
ALTER TABLE arch_process_instance DROP CONSTRAINT fk_arch_process_instance_tenantId;
ALTER TABLE arch_transition_instance DROP CONSTRAINT fk_arch_transition_instance_tenantId;
ALTER TABLE arch_data_instance DROP CONSTRAINT fk_arch_data_instance_tenantId;
ALTER TABLE arch_data_mapping DROP CONSTRAINT fk_arch_data_mapping_tenantId;
