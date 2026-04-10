create table if not exists app_role (
    id bigint not null auto_increment,
    code varchar(50) not null,
    name varchar(100) not null,
    description varchar(255),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    unique key uk_app_role_code (code)
);

create table if not exists app_user (
    id bigint not null auto_increment,
    username varchar(100) not null,
    password_hash varchar(255) not null,
    full_name varchar(150) not null,
    email varchar(150),
    active_flag bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    unique key uk_app_user_username (username)
);

create table if not exists app_user_role (
    user_id bigint not null,
    role_id bigint not null,
    primary key (user_id, role_id),
    constraint fk_user_role_user foreign key (user_id) references app_user (id),
    constraint fk_user_role_role foreign key (role_id) references app_role (id)
);

create table if not exists customer_portfolio (
    cif_number varchar(50) not null,
    company_name varchar(200) not null,
    company_type varchar(50) not null,
    date_of_establishment date,
    place_of_establishment varchar(150),
    tax_id varchar(50),
    business_license varchar(100),
    office_address varchar(500),
    factory_address varchar(500),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (cif_number)
);

create table if not exists customer_key_management (
    id bigint not null auto_increment,
    customer_cif varchar(50) not null,
    name varchar(150) not null,
    national_id_number varchar(50),
    title varchar(100),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_key_management_customer foreign key (customer_cif) references customer_portfolio (cif_number)
);

create table if not exists customer_shareholder (
    id bigint not null auto_increment,
    customer_cif varchar(50) not null,
    name varchar(150) not null,
    ownership_percentage decimal(9, 2),
    share_nominal decimal(18, 2),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_shareholder_customer foreign key (customer_cif) references customer_portfolio (cif_number)
);

create table if not exists customer_related_party (
    id bigint not null auto_increment,
    customer_cif varchar(50) not null,
    relation_type varchar(50) not null,
    name varchar(150) not null,
    identity_number varchar(100),
    contact_details varchar(150),
    address varchar(500),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_related_party_customer foreign key (customer_cif) references customer_portfolio (cif_number)
);

create table if not exists financial_statement (
    id bigint not null auto_increment,
    customer_cif varchar(50) not null,
    statement_id varchar(100),
    period_label varchar(20) not null,
    audit_status varchar(50),
    auditor_name varchar(150),
    group_holding_name varchar(150),
    consolidated_flag bit not null,
    cash decimal(18, 2),
    accounts_receivable decimal(18, 2),
    inventory decimal(18, 2),
    fixed_assets decimal(18, 2),
    accounts_payable decimal(18, 2),
    short_term_loan decimal(18, 2),
    long_term_loan decimal(18, 2),
    total_equity decimal(18, 2),
    sales_revenue decimal(18, 2),
    cogs decimal(18, 2),
    gross_profit decimal(18, 2),
    operating_expenses decimal(18, 2),
    ebitda decimal(18, 2),
    interest_expense decimal(18, 2),
    net_income decimal(18, 2),
    intercompany_elimination decimal(18, 2),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_financial_statement_customer foreign key (customer_cif) references customer_portfolio (cif_number)
);

create table if not exists financial_analysis_snapshot (
    id bigint not null auto_increment,
    financial_statement_id bigint not null,
    current_ratio decimal(18, 4),
    quick_ratio decimal(18, 4),
    debt_to_equity_ratio decimal(18, 4),
    interest_coverage_ratio decimal(18, 4),
    return_on_assets decimal(18, 4),
    return_on_equity decimal(18, 4),
    gross_profit_margin decimal(18, 4),
    net_profit_margin decimal(18, 4),
    ar_days decimal(18, 4),
    inventory_days decimal(18, 4),
    ap_days decimal(18, 4),
    cash_conversion_cycle decimal(18, 4),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    unique key uk_financial_analysis_statement (financial_statement_id),
    constraint fk_financial_analysis_statement foreign key (financial_statement_id) references financial_statement (id)
);

create table if not exists bank_statement (
    id bigint not null auto_increment,
    customer_cif varchar(50) not null,
    bank_name varchar(150) not null,
    account_number varchar(100) not null,
    period_label varchar(20) not null,
    total_inflow decimal(18, 2),
    total_outflow decimal(18, 2),
    average_balance decimal(18, 2),
    cheque_return_count int,
    cheque_return_nominal decimal(18, 2),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_bank_statement_customer foreign key (customer_cif) references customer_portfolio (cif_number)
);

create table if not exists supplier_profile (
    id bigint not null auto_increment,
    customer_cif varchar(50) not null,
    supplier_name varchar(150) not null,
    purchase_percentage decimal(9, 2),
    payment_terms_days int,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_supplier_customer foreign key (customer_cif) references customer_portfolio (cif_number)
);

create table if not exists buyer_profile (
    id bigint not null auto_increment,
    customer_cif varchar(50) not null,
    buyer_name varchar(150) not null,
    sales_percentage decimal(9, 2),
    payment_terms_days int,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_buyer_customer foreign key (customer_cif) references customer_portfolio (cif_number)
);

create table if not exists competitor_profile (
    id bigint not null auto_increment,
    customer_cif varchar(50) not null,
    competitor_name varchar(150) not null,
    estimated_market_share decimal(9, 2),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_competitor_customer foreign key (customer_cif) references customer_portfolio (cif_number)
);

create table if not exists approval_rule (
    rule_id varchar(50) not null,
    rule_name varchar(150) not null,
    rule_type varchar(50) not null,
    metric_key varchar(50) not null,
    operator_key varchar(10) not null,
    threshold_value decimal(18, 4) not null,
    action_routing varchar(50) not null,
    condition_expression varchar(500),
    base_approver varchar(100),
    escalated_approver varchar(100),
    committee_approver varchar(100),
    board_approver varchar(100),
    justification_required bit not null,
    active_flag bit not null,
    message_template varchar(500),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (rule_id)
);

create table if not exists credit_application (
    application_id varchar(50) not null,
    customer_cif varchar(50) not null,
    application_date date not null,
    application_type varchar(50) not null,
    rm_username varchar(100) not null,
    branch_name varchar(100),
    region_name varchar(100),
    cbc_name varchar(100),
    group_relationship_status varchar(50),
    core_capital_bank decimal(18, 2),
    max_lending_limit_percentage decimal(9, 2),
    existing_exposure_group decimal(18, 2),
    proposed_exposure decimal(18, 2),
    available_limit decimal(18, 2),
    bi_sector_code varchar(50),
    sub_sector_description varchar(150),
    industry_outlook varchar(50),
    esg_green_status varchar(100),
    justification_note varchar(2000),
    workflow_status varchar(50) not null,
    current_approval_tier varchar(100),
    process_instance_id varchar(100),
    collateral_coverage decimal(18, 4),
    current_ratio decimal(18, 4),
    debt_to_equity_ratio decimal(18, 4),
    created_by bigint,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (application_id),
    unique key uk_credit_application_process_instance (process_instance_id),
    constraint fk_credit_application_customer foreign key (customer_cif) references customer_portfolio (cif_number),
    constraint fk_credit_application_created_by foreign key (created_by) references app_user (id)
);

create table if not exists facility (
    id bigint not null auto_increment,
    application_id varchar(50) not null,
    facility_code varchar(50) not null,
    facility_name varchar(100) not null,
    revolving_status varchar(50),
    currency_code varchar(10),
    limit_amount decimal(18, 2),
    tenor_months int,
    maturity_date date,
    interest_rate_type varchar(50),
    interest_rate decimal(9, 4),
    provision_fee decimal(9, 4),
    admin_fee decimal(18, 2),
    commitment_fee decimal(9, 4),
    penalty_fee decimal(9, 4),
    repayment_type varchar(100),
    purpose_of_loan varchar(500),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    unique key uk_facility_application_code (application_id, facility_code),
    constraint fk_facility_application foreign key (application_id) references credit_application (application_id)
);

create table if not exists collateral (
    id bigint not null auto_increment,
    application_id varchar(50) not null,
    collateral_id varchar(50) not null,
    collateral_type varchar(100) not null,
    owner_name varchar(150),
    location_address varchar(500),
    appraisal_date date,
    appraiser_name varchar(150),
    market_value decimal(18, 2),
    liquidation_value decimal(18, 2),
    margin_of_advance_percentage decimal(9, 2),
    bankable_value decimal(18, 2),
    legal_document_info varchar(255),
    legal_document_expiry_date date,
    insurance_name varchar(150),
    insurance_coverage_value decimal(18, 2),
    insurance_expiry_date date,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    unique key uk_collateral_application_code (application_id, collateral_id),
    constraint fk_collateral_application foreign key (application_id) references credit_application (application_id)
);

create table if not exists collateral_facility_link (
    collateral_id bigint not null,
    facility_code varchar(50) not null,
    primary key (collateral_id, facility_code),
    constraint fk_collateral_link_collateral foreign key (collateral_id) references collateral (id)
);

create table if not exists tbo_item (
    id bigint not null auto_increment,
    application_id varchar(50) not null,
    tbo_id varchar(50) not null,
    linked_facility_code varchar(50),
    tbo_category varchar(100),
    document_requirement varchar(500),
    due_date date,
    status varchar(50),
    pic varchar(100),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    unique key uk_tbo_application_code (application_id, tbo_id),
    constraint fk_tbo_application foreign key (application_id) references credit_application (application_id)
);

create table if not exists covenant_item (
    id bigint not null auto_increment,
    application_id varchar(50) not null,
    covenant_id varchar(50) not null,
    covenant_type varchar(100),
    description_text varchar(1000),
    testing_frequency varchar(50),
    measurement_date date,
    status varchar(50),
    penalty_details varchar(500),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    unique key uk_covenant_application_code (application_id, covenant_id),
    constraint fk_covenant_application foreign key (application_id) references credit_application (application_id)
);

create table if not exists application_rule_hit (
    id bigint not null auto_increment,
    application_id varchar(50) not null,
    rule_id varchar(50) not null,
    rule_name varchar(150) not null,
    rule_type varchar(50) not null,
    metric_key varchar(50) not null,
    operator_key varchar(10) not null,
    threshold_value decimal(18, 4),
    actual_value decimal(18, 4),
    action_routing varchar(50),
    justification_required bit not null,
    message_text varchar(500),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_rule_hit_application foreign key (application_id) references credit_application (application_id)
);

create table if not exists application_approval_history (
    id bigint not null auto_increment,
    application_id varchar(50) not null,
    stage varchar(100) not null,
    decision varchar(50) not null,
    actor varchar(100) not null,
    notes varchar(1000),
    decided_at datetime(6) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    constraint fk_approval_history_application foreign key (application_id) references credit_application (application_id)
);

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
