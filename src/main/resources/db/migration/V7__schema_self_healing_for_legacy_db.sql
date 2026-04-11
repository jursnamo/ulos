create table if not exists bpmn_model (
    id bigint not null auto_increment,
    process_key varchar(100) not null,
    process_name varchar(200) not null,
    resource_name varchar(200) not null,
    bpmn_xml longtext not null,
    deployment_id varchar(100),
    process_definition_id varchar(150),
    version_no int not null,
    active_flag bit not null,
    deployed_by varchar(100),
    change_summary varchar(500),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    key uk_bpmn_process_version (process_key, version_no)
);

create table if not exists loan_submission (
    loan_id varchar(50) not null,
    customer_id varchar(50) not null,
    customer_name varchar(150) not null,
    loan_amount decimal(18, 2) not null,
    tenor_months int not null,
    business_key varchar(100) not null,
    process_instance_id varchar(100),
    process_definition_key varchar(100) not null,
    status varchar(50) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (loan_id),
    unique key uk_loan_submission_business_key (business_key),
    unique key uk_loan_submission_process_instance (process_instance_id)
);

create table if not exists los_customer (
    id bigint not null auto_increment,
    cif_number varchar(50) not null,
    company_name varchar(200) not null,
    legal_name varchar(200),
    company_type varchar(50),
    registration_date date,
    tax_id varchar(100),
    sector varchar(100),
    location varchar(120),
    status varchar(30) not null,
    profile_json longtext,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    unique key uk_los_customer_cif (cif_number)
);

create table if not exists los_application (
    id bigint not null auto_increment,
    application_id varchar(60) not null,
    customer_id bigint not null,
    application_type varchar(60) not null,
    segment_code varchar(20),
    workflow_status varchar(40) not null,
    current_stage varchar(80),
    process_instance_id varchar(100),
    proposed_exposure decimal(18, 2),
    setup_json longtext,
    compliance_json longtext,
    facilities_json longtext,
    collaterals_json longtext,
    links_json longtext,
    drawdown_conditions_json longtext,
    global_tc_json longtext,
    tbo_json longtext,
    tbo_docs_json longtext,
    financials_json longtext,
    slik_json longtext,
    remarks longtext,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    submitted_at datetime(6),
    primary key (id),
    unique key uk_los_application_code (application_id),
    unique key uk_los_application_process (process_instance_id),
    constraint fk_los_app_customer foreign key (customer_id) references los_customer (id)
);

create table if not exists los_workflow_history (
    id bigint not null auto_increment,
    application_id bigint not null,
    stage_code varchar(80) not null,
    decision_code varchar(40) not null,
    actor varchar(120) not null,
    notes varchar(1000),
    decided_at datetime(6) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_los_workflow_history_app foreign key (application_id) references los_application (id)
);

create table if not exists los_master_product (
    id bigint not null auto_increment,
    product_code varchar(20) not null,
    product_name varchar(120) not null,
    category varchar(40) not null,
    active_flag bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    unique key uk_los_product_code (product_code)
);

create table if not exists los_master_facility (
    id bigint not null auto_increment,
    facility_code varchar(30) not null,
    facility_name varchar(150) not null,
    product_code varchar(20) not null,
    segment_codes varchar(120) not null,
    revolving_flag bit not null,
    funded_flag bit not null,
    non_funded_flag bit not null,
    active_flag bit not null,
    field_mapping_json longtext,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    unique key uk_los_facility_code (facility_code)
);

create table if not exists los_role (
    id bigint not null auto_increment,
    role_code varchar(50) not null,
    role_name varchar(120) not null,
    description varchar(400),
    active_flag bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    unique key uk_los_role_code (role_code)
);

create table if not exists los_user (
    id bigint not null auto_increment,
    username varchar(80) not null,
    full_name varchar(150) not null,
    email varchar(180),
    password_hash varchar(128) not null,
    active_flag bit not null,
    last_login_at datetime(6),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    unique key uk_los_user_username (username)
);

create table if not exists los_user_role (
    user_id bigint not null,
    role_id bigint not null,
    primary key (user_id, role_id),
    constraint fk_los_user_role_user foreign key (user_id) references los_user (id),
    constraint fk_los_user_role_role foreign key (role_id) references los_role (id)
);

create table if not exists los_auth_token (
    id bigint not null auto_increment,
    token_value varchar(120) not null,
    user_id bigint not null,
    issued_at datetime(6) not null,
    expires_at datetime(6) not null,
    last_accessed_at datetime(6),
    revoked_flag bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    unique key uk_los_auth_token_value (token_value),
    constraint fk_los_auth_token_user foreign key (user_id) references los_user (id)
);
