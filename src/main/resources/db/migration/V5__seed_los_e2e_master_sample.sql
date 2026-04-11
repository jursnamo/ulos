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

insert into los_master_product (product_code, product_name, category, active_flag, created_at, updated_at)
values
    ('WC', 'Working Capital', 'Funded', b'1', current_timestamp(6), current_timestamp(6)),
    ('TL', 'Term Loan', 'Funded', b'1', current_timestamp(6), current_timestamp(6)),
    ('TR', 'Trade Finance', 'Mixed', b'1', current_timestamp(6), current_timestamp(6)),
    ('BG', 'Bank Guarantee', 'NonFunded', b'1', current_timestamp(6), current_timestamp(6)),
    ('FX', 'Treasury / FX Line', 'NonFunded', b'1', current_timestamp(6), current_timestamp(6)),
    ('SCF', 'Supply Chain Finance', 'Funded', b'1', current_timestamp(6), current_timestamp(6)),
    ('BIL', 'Bilateral / Structured', 'Mixed', b'1', current_timestamp(6), current_timestamp(6))
on duplicate key update
    product_name = values(product_name),
    category = values(category),
    active_flag = values(active_flag),
    updated_at = values(updated_at);

insert into los_master_facility (
    facility_code,
    facility_name,
    product_code,
    segment_codes,
    revolving_flag,
    funded_flag,
    non_funded_flag,
    active_flag,
    field_mapping_json,
    created_at,
    updated_at
)
values
    ('OD', 'Overdraft', 'WC', 'SME,COMBA', b'1', b'1', b'0', b'1', '["proposed_limit","currency","tenor_month","facility_purpose","repayment_type","interest_frequency","secured_indicator"]', current_timestamp(6), current_timestamp(6)),
    ('PRK', 'Pinjaman Rekening Koran', 'WC', 'SME,COMBA', b'1', b'1', b'0', b'1', '["proposed_limit","currency","tenor_month","facility_purpose","repayment_type","interest_frequency","secured_indicator"]', current_timestamp(6), current_timestamp(6)),
    ('RCF', 'Revolving Credit Facility', 'WC', 'COMBA,COBA', b'1', b'1', b'0', b'1', '["proposed_limit","currency","tenor_month","facility_purpose","repayment_type","secured_indicator"]', current_timestamp(6), current_timestamp(6)),
    ('TLF', 'Term Loan Fixed', 'TL', 'SME,COMBA,COBA', b'0', b'1', b'0', b'1', '["proposed_limit","currency","tenor_month","grace_period","repayment_type","installment_option","secured_indicator"]', current_timestamp(6), current_timestamp(6)),
    ('AF', 'Asset Financing', 'TL', 'SME,COMBA', b'0', b'1', b'0', b'1', '["proposed_limit","currency","tenor_month","acquisition_cost","down_payment","secured_indicator"]', current_timestamp(6), current_timestamp(6)),
    ('PF', 'Project Financing', 'TL', 'COBA', b'0', b'1', b'0', b'1', '["proposed_limit","currency","tenor_month","project_name","project_owner","grace_period","secured_indicator"]', current_timestamp(6), current_timestamp(6)),
    ('LCIMP', 'Import LC', 'TR', 'COMBA,COBA', b'0', b'0', b'1', b'1', '["proposed_limit","currency","availability_period","beneficiary_name","secured_indicator"]', current_timestamp(6), current_timestamp(6)),
    ('BG-PERF', 'Performance Bond', 'BG', 'SME,COMBA,COBA', b'0', b'0', b'1', b'1', '["proposed_limit","currency","beneficiary_name","underlying_contract","secured_indicator"]', current_timestamp(6), current_timestamp(6)),
    ('FXLINE', 'FX Line', 'FX', 'COMBA,COBA', b'1', b'0', b'1', b'1', '["proposed_limit","currency","hedging_purpose","secured_indicator"]', current_timestamp(6), current_timestamp(6)),
    ('SUPF', 'Supplier Financing', 'SCF', 'COMBA,COBA', b'1', b'1', b'0', b'1', '["proposed_limit","currency","anchor_name","tenor_month","secured_indicator"]', current_timestamp(6), current_timestamp(6)),
    ('UMB', 'Umbrella Line', 'BIL', 'COBA', b'1', b'1', b'1', b'1', '["proposed_limit","currency","tenor_month","facility_purpose","secured_indicator"]', current_timestamp(6), current_timestamp(6))
on duplicate key update
    facility_name = values(facility_name),
    product_code = values(product_code),
    segment_codes = values(segment_codes),
    revolving_flag = values(revolving_flag),
    funded_flag = values(funded_flag),
    non_funded_flag = values(non_funded_flag),
    active_flag = values(active_flag),
    field_mapping_json = values(field_mapping_json),
    updated_at = values(updated_at);

insert into los_customer (
    cif_number,
    company_name,
    legal_name,
    company_type,
    registration_date,
    tax_id,
    sector,
    location,
    status,
    profile_json,
    created_at,
    updated_at
)
values (
    'DEB-9920-X1',
    'Global Logistics Systems Corp.',
    'Global Logistics Systems Corp.',
    'PT',
    '2008-10-14',
    '88-2940219-X',
    'Industrial Sector',
    'Chicago, Illinois',
    'ACTIVE',
    '{"keyPersonnel":["Elena Rodriguez (CFO)","Marcus Thorne (CEO)"],"rm":{"name":"Sarah Jenkins","role":"Senior Director, Logistics Division"}}',
    current_timestamp(6),
    current_timestamp(6)
)
on duplicate key update
    company_name = values(company_name),
    legal_name = values(legal_name),
    company_type = values(company_type),
    registration_date = values(registration_date),
    tax_id = values(tax_id),
    sector = values(sector),
    location = values(location),
    status = values(status),
    profile_json = values(profile_json),
    updated_at = values(updated_at);

insert into los_application (
    application_id,
    customer_id,
    application_type,
    segment_code,
    workflow_status,
    current_stage,
    proposed_exposure,
    setup_json,
    compliance_json,
    facilities_json,
    collaterals_json,
    links_json,
    drawdown_conditions_json,
    global_tc_json,
    tbo_json,
    tbo_docs_json,
    financials_json,
    slik_json,
    remarks,
    created_at,
    updated_at
)
select
    'APP-2024-001',
    c.id,
    'Renewal',
    'COMBA',
    'DRAFT',
    'Origination',
    15000000.00,
    '{"applicationCategory":"Standard","accountOfficer":"Sarah Jenkins"}',
    '{"bmpkCalculatedDebtor":12000,"industryMat":"Logistics & Transport"}',
    '[{"id":1,"type":"Term Loan","amount":10000000,"currency":"USD","tenor":"60 Months","pricing":"SOFR + 2.5%","status":"Proposed"},{"id":2,"type":"Working Capital Line","amount":5000000,"currency":"USD","tenor":"12 Months","pricing":"SOFR + 2.0%","status":"Proposed"}]',
    '[{"id":1,"type":"Real Estate","value":12000000,"description":"Warehouse in Chicago","code":"RE-001","appraisalStatus":"Completed","lastAppraisalDate":"2024-01-15"},{"id":2,"type":"Corporate Guarantee","value":5000000,"description":"Parent Co Guarantee","code":"CG-002","appraisalStatus":"Not Requested","lastAppraisalDate":"-"}]',
    '[{"facilityId":1,"collateralId":1},{"facilityId":1,"collateralId":2},{"facilityId":2,"collateralId":2}]',
    '[{"code":"AGR_SIGNED","name":"Signed Credit Agreement","category":"Legal","stage":"Before First Drawdown","mandatory":true,"blocking":true,"status":"Fulfilled","fulfilledDate":"2024-04-10"}]',
    '[{"code":"AUD_FS_ANN","name":"Submit Audited Financial Statements Annually","category":"Financial","type":"Affirmative","mandatory":true,"monitoringRequired":true,"status":"Active Monitoring"}]',
    '[{"category":"Lending","opportunityAmount":15000000,"capturedAmount":10000000,"probability":100,"status":"Won"}]',
    '[{"id":"1","name":"Akta Pendirian & Perubahan Terakhir","category":"Legal","scope":"Customer","requestedFrom":"Borrower","mandatory":true,"blocking":true,"status":"Verified"}]',
    '[{"year":"2021","type":"Audited","revenue":35000000,"grossProfit":8500000,"ebitda":4200000,"netProfit":2100000,"totalAssets":45000000,"totalLiabilities":28000000,"totalEquity":17000000,"currentRatio":1.45,"der":1.65,"dscr":1.35}]',
    '[{"id":"1","name":"PT Maju Jaya (Borrower)","type":"Corporate","relationship":"Main Borrower","idType":"TDP","idNo":"12345678","status":"Success","lastInquiryDate":"2024-04-10"}]',
    'Initial sample workspace for LOS conversion.',
    current_timestamp(6),
    current_timestamp(6)
from los_customer c
where c.cif_number = 'DEB-9920-X1'
  and not exists (select 1 from los_application existing where existing.application_id = 'APP-2024-001');
