delete from application_approval_history where application_id = 'APP-ENT-001';
delete from application_rule_hit where application_id = 'APP-ENT-001';
delete from covenant_item where application_id = 'APP-ENT-001';
delete from tbo_item where application_id = 'APP-ENT-001';
delete from collateral_facility_link
where collateral_id in (
    select sample_collateral.id
    from (select id from collateral where application_id = 'APP-ENT-001') sample_collateral
);
delete from collateral where application_id = 'APP-ENT-001';
delete from facility where application_id = 'APP-ENT-001';
delete from credit_application where application_id = 'APP-ENT-001';

delete from financial_analysis_snapshot
where financial_statement_id in (
    select sample_statement.id
    from (select id from financial_statement where customer_cif = 'CIF-ENT-001') sample_statement
);
delete from financial_statement where customer_cif = 'CIF-ENT-001';
delete from bank_statement where customer_cif = 'CIF-ENT-001';
delete from supplier_profile where customer_cif = 'CIF-ENT-001';
delete from buyer_profile where customer_cif = 'CIF-ENT-001';
delete from competitor_profile where customer_cif = 'CIF-ENT-001';
delete from customer_related_party where customer_cif = 'CIF-ENT-001';
delete from customer_shareholder where customer_cif = 'CIF-ENT-001';
delete from customer_key_management where customer_cif = 'CIF-ENT-001';
delete from customer_portfolio where cif_number = 'CIF-ENT-001';

insert into app_user (
    username,
    password_hash,
    full_name,
    email,
    active_flag,
    created_at,
    updated_at
)
values
    ('admin', '$2a$10$ahB.BI8h8wVjjF1eGMoiNuT4lLcjdSnUcWMwg7zRf/CLIHbHync9i', 'System Administrator', 'admin@ulos.local', b'1', current_timestamp(6), current_timestamp(6)),
    ('rm.user', '$2a$10$UZYL7aLPeqBEBSvhBvmkSujTEeQx0awTywcM.IVVa2xmwiobyKYUu', 'Relationship Manager', 'rm@ulos.local', b'1', current_timestamp(6), current_timestamp(6)),
    ('analyst.user', '$2a$10$fRJxA.lZA8n7ZqT0S29aZuM9DzkG3qAaEPI6m4dKUCwz9zIK5lIBm', 'Credit Analyst', 'analyst@ulos.local', b'1', current_timestamp(6), current_timestamp(6)),
    ('branch.manager', '$2a$10$z/GID9g9DQcEBK1lQFw/euFuHzFLO2Snq3K/u3gqZzKvendFGoRmO', 'Branch Manager', 'branch@ulos.local', b'1', current_timestamp(6), current_timestamp(6)),
    ('regional.head', '$2a$10$xGVWdQtR3/5UQX4b5nVTY.wy/PR8hNd9MLTS.2iJ6j9pfMnLsZEaO', 'Regional Head', 'regional@ulos.local', b'1', current_timestamp(6), current_timestamp(6)),
    ('committee.user', '$2a$10$Cgmb0kTRit1thf6fCIFF0uh4f3R6v877kSyf04ZIYwLAS6uQYAAK.', 'Credit Committee', 'committee@ulos.local', b'1', current_timestamp(6), current_timestamp(6)),
    ('board.user', '$2a$10$T2LXO7zGNux1d1Z8xOUlfuqAHpxF3PTu..G.kYos6AX3sPDTi7Hd.', 'Board Member', 'board@ulos.local', b'1', current_timestamp(6), current_timestamp(6)),
    ('workflow.admin', '$2a$10$M8H/bOMGysoYjPaD5nYIIu2J1Yvd.i3DIcXubRDneVH1f7111KejS', 'Workflow Administrator', 'workflow@ulos.local', b'1', current_timestamp(6), current_timestamp(6))
on duplicate key update
    password_hash = values(password_hash),
    full_name = values(full_name),
    email = values(email),
    active_flag = values(active_flag),
    updated_at = values(updated_at);

insert ignore into app_user_role (user_id, role_id)
select user_ref.id, role_ref.id
from app_user user_ref
join app_role role_ref on role_ref.code = 'ADMIN'
where user_ref.username = 'admin';

insert ignore into app_user_role (user_id, role_id)
select user_ref.id, role_ref.id
from app_user user_ref
join app_role role_ref on role_ref.code = 'WORKFLOW_ADMIN'
where user_ref.username = 'admin';

insert ignore into app_user_role (user_id, role_id)
select user_ref.id, role_ref.id
from app_user user_ref
join app_role role_ref on role_ref.code = 'RM'
where user_ref.username = 'rm.user';

insert ignore into app_user_role (user_id, role_id)
select user_ref.id, role_ref.id
from app_user user_ref
join app_role role_ref on role_ref.code = 'ANALYST'
where user_ref.username = 'analyst.user';

insert ignore into app_user_role (user_id, role_id)
select user_ref.id, role_ref.id
from app_user user_ref
join app_role role_ref on role_ref.code = 'BRANCH_MANAGER'
where user_ref.username = 'branch.manager';

insert ignore into app_user_role (user_id, role_id)
select user_ref.id, role_ref.id
from app_user user_ref
join app_role role_ref on role_ref.code = 'REGIONAL_HEAD'
where user_ref.username = 'regional.head';

insert ignore into app_user_role (user_id, role_id)
select user_ref.id, role_ref.id
from app_user user_ref
join app_role role_ref on role_ref.code = 'CREDIT_COMMITTEE'
where user_ref.username = 'committee.user';

insert ignore into app_user_role (user_id, role_id)
select user_ref.id, role_ref.id
from app_user user_ref
join app_role role_ref on role_ref.code = 'BOARD_OF_DIRECTORS'
where user_ref.username = 'board.user';

insert ignore into app_user_role (user_id, role_id)
select user_ref.id, role_ref.id
from app_user user_ref
join app_role role_ref on role_ref.code = 'WORKFLOW_ADMIN'
where user_ref.username = 'workflow.admin';

insert into customer_portfolio (
    cif_number,
    company_name,
    company_type,
    date_of_establishment,
    place_of_establishment,
    tax_id,
    business_license,
    office_address,
    factory_address,
    created_at,
    updated_at
)
values (
    'CIF-ENT-001',
    'PT Meridian Alloy Indonesia',
    'PT',
    '2012-05-14',
    'Jakarta',
    '01.234.567.8-999.000',
    'NIB-ENT-001',
    'Menara Meridian, Jakarta Selatan',
    'Karawang Industrial Estate',
    current_timestamp(6),
    current_timestamp(6)
)
on duplicate key update
    company_name = values(company_name),
    company_type = values(company_type),
    date_of_establishment = values(date_of_establishment),
    place_of_establishment = values(place_of_establishment),
    tax_id = values(tax_id),
    business_license = values(business_license),
    office_address = values(office_address),
    factory_address = values(factory_address),
    updated_at = values(updated_at);

insert into customer_key_management (customer_cif, name, national_id_number, title, created_at, updated_at)
values
    ('CIF-ENT-001', 'Raka Mahendra', '3174xxxxxx', 'President Director', current_timestamp(6), current_timestamp(6)),
    ('CIF-ENT-001', 'Ayu Saraswati', '3175xxxxxx', 'Finance Director', current_timestamp(6), current_timestamp(6));

insert into customer_shareholder (customer_cif, name, ownership_percentage, share_nominal, created_at, updated_at)
values
    ('CIF-ENT-001', 'Meridian Holdings Pte Ltd', 70.00, 7000000000.00, current_timestamp(6), current_timestamp(6)),
    ('CIF-ENT-001', 'PT Sinar Baja Nusantara', 30.00, 3000000000.00, current_timestamp(6), current_timestamp(6));

insert into customer_related_party (customer_cif, relation_type, name, identity_number, contact_details, address, created_at, updated_at)
values
    ('CIF-ENT-001', 'PARENT_COMPANY', 'Meridian Holdings Pte Ltd', 'SG-998877', '65-123456', 'Singapore', current_timestamp(6), current_timestamp(6)),
    ('CIF-ENT-001', 'THIRD_PARTY', 'PT Garuda Leasing', '02.111.222.3-444.000', '021-889900', 'Jakarta', current_timestamp(6), current_timestamp(6));

insert into financial_statement (
    customer_cif,
    statement_id,
    period_label,
    audit_status,
    auditor_name,
    group_holding_name,
    consolidated_flag,
    cash,
    accounts_receivable,
    inventory,
    fixed_assets,
    accounts_payable,
    short_term_loan,
    long_term_loan,
    total_equity,
    sales_revenue,
    cogs,
    gross_profit,
    operating_expenses,
    ebitda,
    interest_expense,
    net_income,
    intercompany_elimination,
    created_at,
    updated_at
)
values
    (
        'CIF-ENT-001',
        'FS-2025-12',
        '12/2025',
        'AUDITED',
        'KAP Prima Reksa',
        null,
        b'0',
        5400000000.00,
        4200000000.00,
        2600000000.00,
        9100000000.00,
        2100000000.00,
        2600000000.00,
        4100000000.00,
        8200000000.00,
        26500000000.00,
        16800000000.00,
        9700000000.00,
        4300000000.00,
        5400000000.00,
        920000000.00,
        2800000000.00,
        0.00,
        current_timestamp(6),
        current_timestamp(6)
    ),
    (
        'CIF-ENT-001',
        'CONS-2025-12',
        '12/2025',
        'AUDITED',
        'KAP Prima Reksa',
        'Meridian Holdings',
        b'1',
        8800000000.00,
        6100000000.00,
        3600000000.00,
        15300000000.00,
        3300000000.00,
        4100000000.00,
        6400000000.00,
        12400000000.00,
        41000000000.00,
        26100000000.00,
        14900000000.00,
        6800000000.00,
        8100000000.00,
        1330000000.00,
        4300000000.00,
        600000000.00,
        current_timestamp(6),
        current_timestamp(6)
    );

insert into financial_analysis_snapshot (
    financial_statement_id,
    current_ratio,
    quick_ratio,
    debt_to_equity_ratio,
    interest_coverage_ratio,
    return_on_assets,
    return_on_equity,
    gross_profit_margin,
    net_profit_margin,
    ar_days,
    inventory_days,
    ap_days,
    cash_conversion_cycle,
    created_at,
    updated_at
)
select
    fs.id,
    2.5957,
    2.0426,
    0.8171,
    5.8696,
    0.1315,
    0.3415,
    0.3660,
    0.1057,
    57.8491,
    56.4881,
    45.6250,
    68.7122,
    current_timestamp(6),
    current_timestamp(6)
from financial_statement fs
where fs.statement_id = 'FS-2025-12';

insert into financial_analysis_snapshot (
    financial_statement_id,
    current_ratio,
    quick_ratio,
    debt_to_equity_ratio,
    interest_coverage_ratio,
    return_on_assets,
    return_on_equity,
    gross_profit_margin,
    net_profit_margin,
    ar_days,
    inventory_days,
    ap_days,
    cash_conversion_cycle,
    created_at,
    updated_at
)
select
    fs.id,
    2.5000,
    2.0135,
    0.8468,
    6.0902,
    0.1295,
    0.3468,
    0.3634,
    0.1049,
    54.3049,
    50.3448,
    46.1494,
    58.5003,
    current_timestamp(6),
    current_timestamp(6)
from financial_statement fs
where fs.statement_id = 'CONS-2025-12';

insert into bank_statement (
    customer_cif,
    bank_name,
    account_number,
    period_label,
    total_inflow,
    total_outflow,
    average_balance,
    cheque_return_count,
    cheque_return_nominal,
    created_at,
    updated_at
)
values (
    'CIF-ENT-001',
    'Bank Mandiri',
    '1100009988',
    '12/2025',
    12800000000.00,
    11800000000.00,
    2600000000.00,
    0,
    0.00,
    current_timestamp(6),
    current_timestamp(6)
);

insert into supplier_profile (customer_cif, supplier_name, purchase_percentage, payment_terms_days, created_at, updated_at)
values ('CIF-ENT-001', 'PT Baja Sumber Makmur', 42.00, 45, current_timestamp(6), current_timestamp(6));

insert into buyer_profile (customer_cif, buyer_name, sales_percentage, payment_terms_days, created_at, updated_at)
values ('CIF-ENT-001', 'PT Nusantara Komponen', 38.00, 60, current_timestamp(6), current_timestamp(6));

insert into competitor_profile (customer_cif, competitor_name, estimated_market_share, created_at, updated_at)
values ('CIF-ENT-001', 'PT Global Alloy Manufacturing', 17.50, current_timestamp(6), current_timestamp(6));

insert into credit_application (
    application_id,
    customer_cif,
    application_date,
    application_type,
    rm_username,
    branch_name,
    region_name,
    cbc_name,
    group_relationship_status,
    core_capital_bank,
    max_lending_limit_percentage,
    existing_exposure_group,
    proposed_exposure,
    available_limit,
    bi_sector_code,
    sub_sector_description,
    industry_outlook,
    esg_green_status,
    justification_note,
    workflow_status,
    current_approval_tier,
    process_instance_id,
    collateral_coverage,
    current_ratio,
    debt_to_equity_ratio,
    created_by,
    created_at,
    updated_at
)
values (
    'APP-ENT-001',
    'CIF-ENT-001',
    '2026-04-10',
    'NEW',
    'rm.user',
    'Jakarta Corporate Branch',
    'Region West',
    'CBC Alpha',
    'NON_CONNECTED_PARTY',
    1500000000000.00,
    25.00,
    12000000000.00,
    21000000000.00,
    342000000000.00,
    'C241',
    'Basic Metal Manufacturing',
    'STABLE',
    'LOW_RISK',
    'Request remains within policy and supported by operating cash flow.',
    'DRAFT',
    'DRAFT',
    null,
    72.3810,
    2.5957,
    0.8171,
    (select id from app_user where username = 'rm.user'),
    current_timestamp(6),
    current_timestamp(6)
)
on duplicate key update
    application_date = values(application_date),
    application_type = values(application_type),
    rm_username = values(rm_username),
    branch_name = values(branch_name),
    region_name = values(region_name),
    cbc_name = values(cbc_name),
    group_relationship_status = values(group_relationship_status),
    core_capital_bank = values(core_capital_bank),
    max_lending_limit_percentage = values(max_lending_limit_percentage),
    existing_exposure_group = values(existing_exposure_group),
    proposed_exposure = values(proposed_exposure),
    available_limit = values(available_limit),
    bi_sector_code = values(bi_sector_code),
    sub_sector_description = values(sub_sector_description),
    industry_outlook = values(industry_outlook),
    esg_green_status = values(esg_green_status),
    justification_note = values(justification_note),
    workflow_status = values(workflow_status),
    current_approval_tier = values(current_approval_tier),
    collateral_coverage = values(collateral_coverage),
    current_ratio = values(current_ratio),
    debt_to_equity_ratio = values(debt_to_equity_ratio),
    created_by = values(created_by),
    updated_at = values(updated_at);

insert into facility (
    application_id,
    facility_code,
    facility_name,
    revolving_status,
    currency_code,
    limit_amount,
    tenor_months,
    maturity_date,
    interest_rate_type,
    interest_rate,
    provision_fee,
    admin_fee,
    commitment_fee,
    penalty_fee,
    repayment_type,
    purpose_of_loan,
    created_at,
    updated_at
)
values
    (
        'APP-ENT-001',
        'KMK-001',
        'KMK',
        'REVOLVING',
        'IDR',
        15000000000.00,
        12,
        '2027-04-10',
        'FLOATING',
        12.5000,
        1.0000,
        1500000.00,
        0.5000,
        2.0000,
        'AMORTIZATION',
        'Raw material financing',
        current_timestamp(6),
        current_timestamp(6)
    ),
    (
        'APP-ENT-001',
        'KI-001',
        'KREDIT_INVESTASI',
        'NON_REVOLVING',
        'IDR',
        6000000000.00,
        36,
        '2029-04-10',
        'FIXED',
        11.7500,
        0.7500,
        1000000.00,
        0.0000,
        1.5000,
        'GRACE_PERIOD',
        'Machine upgrade',
        current_timestamp(6),
        current_timestamp(6)
    );

insert into collateral (
    application_id,
    collateral_id,
    collateral_type,
    owner_name,
    location_address,
    appraisal_date,
    appraiser_name,
    market_value,
    liquidation_value,
    margin_of_advance_percentage,
    bankable_value,
    legal_document_info,
    legal_document_expiry_date,
    insurance_name,
    insurance_coverage_value,
    insurance_expiry_date,
    created_at,
    updated_at
)
values (
    'APP-ENT-001',
    'COL-001',
    'TANAH_BANGUNAN',
    'PT Meridian Alloy Indonesia',
    'Karawang Industrial Estate',
    '2026-03-10',
    'KJPP Mitra',
    25000000000.00,
    19000000000.00,
    20.00,
    15200000000.00,
    'SHGB No. 8877',
    '2032-12-31',
    'Asuransi Properti Nusantara',
    22000000000.00,
    '2027-04-10',
    current_timestamp(6),
    current_timestamp(6)
);

insert into collateral_facility_link (collateral_id, facility_code)
select col.id, 'KMK-001'
from collateral col
where col.application_id = 'APP-ENT-001'
  and col.collateral_id = 'COL-001';

insert into collateral_facility_link (collateral_id, facility_code)
select col.id, 'KI-001'
from collateral col
where col.application_id = 'APP-ENT-001'
  and col.collateral_id = 'COL-001';

insert into tbo_item (
    application_id,
    tbo_id,
    linked_facility_code,
    tbo_category,
    document_requirement,
    due_date,
    status,
    pic,
    created_at,
    updated_at
)
values (
    'APP-ENT-001',
    'TBO-001',
    'KMK-001',
    'SYARAT_PENCAIRAN',
    'Bukti lunas pajak tahun terakhir',
    '2026-05-10',
    'PENDING',
    'RM',
    current_timestamp(6),
    current_timestamp(6)
);

insert into covenant_item (
    application_id,
    covenant_id,
    covenant_type,
    description_text,
    testing_frequency,
    measurement_date,
    status,
    penalty_details,
    created_at,
    updated_at
)
values (
    'APP-ENT-001',
    'COV-001',
    'FINANCIAL',
    'Nasabah wajib menjaga Current Ratio > 1.0x',
    'QUARTERLY',
    '2026-07-10',
    'PENDING',
    'Warning letter and repricing',
    current_timestamp(6),
    current_timestamp(6)
);
