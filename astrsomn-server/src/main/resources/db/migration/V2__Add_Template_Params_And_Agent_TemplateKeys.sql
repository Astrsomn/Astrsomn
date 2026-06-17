-- =============================================================
-- V2: Add Template Params and Agent TemplateKeys
-- =============================================================
-- Adds dynamic parameter definitions to AI_TEMPLATE so each
-- template can declare its expected input parameters (JSON).
-- Adds template-keys linking to AI_AGENT (comma-separated)
-- so an agent can be associated with multiple templates.
-- =============================================================

ALTER TABLE AI_TEMPLATE
    ADD COLUMN PARAMS_DEFINITION TEXT DEFAULT NULL COMMENT 'JSON parameter definitions for dynamic template rendering';

ALTER TABLE AI_AGENT
    ADD COLUMN TEMPLATE_KEYS TEXT DEFAULT NULL COMMENT 'Comma-separated template keys for agent-template association';

CREATE INDEX IDX_AI_AGENT_TEMPLATE_KEYS ON AI_AGENT (TEMPLATE_KEYS(255));
