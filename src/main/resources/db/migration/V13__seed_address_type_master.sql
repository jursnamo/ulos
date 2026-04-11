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
    ('address_type','BUSINESS_ADDRESS','Business Address','BUS',1,b'1','Alamat utama operasional/debitur.',null,current_timestamp(6),current_timestamp(6)),
    ('address_type','OTHER_ADDRESS','Other Address','OTH',2,b'1','Alamat alternatif lainnya.',null,current_timestamp(6),current_timestamp(6)),
    ('address_type','MAILING_ADDRESS','Mailing Address','MAIL',3,b'1','Alamat korespondensi surat.',null,current_timestamp(6),current_timestamp(6)),
    ('address_type','BILLING_ADDRESS','Billing Address','BILL',4,b'1','Alamat penagihan invoice.',null,current_timestamp(6),current_timestamp(6)),
    ('address_type','REGISTERED_ADDRESS','Registered Address','REG',5,b'1','Alamat legal sesuai dokumen perusahaan.',null,current_timestamp(6),current_timestamp(6)),
    ('address_type','OPERATIONAL_ADDRESS','Operational Address','OPS',6,b'1','Alamat kegiatan operasional harian.',null,current_timestamp(6),current_timestamp(6))
on duplicate key update
    item_name = values(item_name),
    legacy_code = values(legacy_code),
    sort_order = values(sort_order),
    active_flag = values(active_flag),
    description = values(description),
    extra_json = values(extra_json),
    updated_at = values(updated_at);
