create table if not exists los_precheck_session (
    id bigint not null auto_increment,
    check_ref varchar(80) not null,
    check_type varchar(20) not null,
    customer_id bigint not null,
    status varchar(30) not null default 'REQUESTED',
    requested_at datetime(6),
    completed_at datetime(6),
    result_summary varchar(255),
    result_json longtext,
    linked_application_id varchar(60),
    notes longtext,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    unique key uk_los_precheck_ref (check_ref),
    key idx_los_precheck_type_customer_status (check_type, customer_id, status),
    key idx_los_precheck_updated (updated_at),
    constraint fk_los_precheck_customer foreign key (customer_id) references los_customer (id)
);

set @add_linked_slik_check_ref = (
    select if(
        exists(
            select 1
            from information_schema.columns
            where table_schema = database()
              and table_name = 'los_application'
              and column_name = 'linked_slik_check_ref'
        ),
        'select 1',
        'alter table los_application add column linked_slik_check_ref varchar(80) null'
    )
);
prepare stmt_add_linked_slik_check_ref from @add_linked_slik_check_ref;
execute stmt_add_linked_slik_check_ref;
deallocate prepare stmt_add_linked_slik_check_ref;

set @add_linked_collateral_check_ref = (
    select if(
        exists(
            select 1
            from information_schema.columns
            where table_schema = database()
              and table_name = 'los_application'
              and column_name = 'linked_collateral_check_ref'
        ),
        'select 1',
        'alter table los_application add column linked_collateral_check_ref varchar(80) null'
    )
);
prepare stmt_add_linked_collateral_check_ref from @add_linked_collateral_check_ref;
execute stmt_add_linked_collateral_check_ref;
deallocate prepare stmt_add_linked_collateral_check_ref;

insert into los_precheck_session (
    check_ref,
    check_type,
    customer_id,
    status,
    requested_at,
    completed_at,
    result_summary,
    result_json,
    linked_application_id,
    notes,
    created_at,
    updated_at
)
select
    'SLIK-20260411-DEMO01',
    'SLIK',
    c.id,
    'COMPLETED',
    current_timestamp(6),
    current_timestamp(6),
    'Collectibility 1, no delinquency',
    '{"subjects":[{"id":"SLIK-SUB-001","name":"PT Global Logistics Systems","type":"Corporate","relationship":"Main Borrower","idType":"NPWP","idNo":"88-2940219-X","status":"Success","lastInquiryDate":"2026-04-11"}],"summary":{"riskFlag":"Low","worstCollectibility":"1","maxDpd":0,"totalOutstanding":18500000000}}',
    null,
    'Seeded pre-check for demo linkage',
    current_timestamp(6),
    current_timestamp(6)
from los_customer c
where c.cif_number = 'CIF-ENT-001'
  and not exists (select 1 from los_precheck_session p where p.check_ref = 'SLIK-20260411-DEMO01')
limit 1;

insert into los_precheck_session (
    check_ref,
    check_type,
    customer_id,
    status,
    requested_at,
    completed_at,
    result_summary,
    result_json,
    linked_application_id,
    notes,
    created_at,
    updated_at
)
select
    'COLLATERAL-20260411-DEMO01',
    'COLLATERAL',
    c.id,
    'COMPLETED',
    current_timestamp(6),
    current_timestamp(6),
    'Warehouse appraisal completed',
    '{"collaterals":[{"id":"1","code":"COL-DEMO-001","type":"Real Estate","description":"Warehouse - Chicago","value":12000000,"eligibleValue":9600000,"appraisalDate":"2026-04-11","appraisalStatus":"Completed"}]}',
    null,
    'Seeded appraisal pre-check for demo linkage',
    current_timestamp(6),
    current_timestamp(6)
from los_customer c
where c.cif_number = 'CIF-ENT-001'
  and not exists (select 1 from los_precheck_session p where p.check_ref = 'COLLATERAL-20260411-DEMO01')
limit 1;
