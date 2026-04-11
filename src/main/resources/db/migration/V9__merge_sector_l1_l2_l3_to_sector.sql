insert into los_master_portfolio_lookup (
    master_type,
    item_code,
    item_name,
    legacy_code,
    sort_order,
    active_flag,
    description,
    extra_json,
    created_at,
    updated_at
)
values
    ('sector','L1','L1',null,1,b'1','Level sektor 1.',null,current_timestamp(6),current_timestamp(6)),
    ('sector','L2','L2',null,2,b'1','Level sektor 2.',null,current_timestamp(6),current_timestamp(6)),
    ('sector','L3','L3',null,3,b'1','Level sektor 3.',null,current_timestamp(6),current_timestamp(6))
on duplicate key update
    item_name = values(item_name),
    active_flag = values(active_flag),
    updated_at = values(updated_at);

insert ignore into los_master_portfolio_lookup (
    master_type,
    item_code,
    item_name,
    legacy_code,
    sort_order,
    active_flag,
    description,
    extra_json,
    created_at,
    updated_at
)
select
    'sector',
    concat('L1_', item_code),
    concat('L1 - ', item_name),
    legacy_code,
    sort_order + 100,
    active_flag,
    concat('Migrated from sector_l1. ', coalesce(description, '')),
    extra_json,
    current_timestamp(6),
    current_timestamp(6)
from los_master_portfolio_lookup
where lower(master_type) = 'sector_l1';

insert ignore into los_master_portfolio_lookup (
    master_type,
    item_code,
    item_name,
    legacy_code,
    sort_order,
    active_flag,
    description,
    extra_json,
    created_at,
    updated_at
)
select
    'sector',
    concat('L2L3_', item_code),
    concat('L2-L3 - ', item_name),
    legacy_code,
    sort_order + 200,
    active_flag,
    concat('Migrated from sector_l2_l3. ', coalesce(description, '')),
    extra_json,
    current_timestamp(6),
    current_timestamp(6)
from los_master_portfolio_lookup
where lower(master_type) = 'sector_l2_l3';
