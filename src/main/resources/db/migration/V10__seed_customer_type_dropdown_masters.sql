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
values
    ('subject_kind','COMPANY','COMPANY','COMP',1,b'1','Subjek badan usaha.',null,current_timestamp(6),current_timestamp(6)),
    ('subject_kind','INDIVIDUAL','INDIVIDUAL','INDV',2,b'1','Subjek perorangan.',null,current_timestamp(6),current_timestamp(6)),
    ('subject_kind','GROUP','GROUP','GRP',3,b'1','Subjek grup.',null,current_timestamp(6),current_timestamp(6)),

    ('segment_applicability','SME','SME','SME',1,b'1','Segment SME.',null,current_timestamp(6),current_timestamp(6)),
    ('segment_applicability','COMBA','COMBA','COMBA',2,b'1','Segment Commercial Banking.',null,current_timestamp(6),current_timestamp(6)),
    ('segment_applicability','COBA','COBA','COBA',3,b'1','Segment Corporate Banking.',null,current_timestamp(6),current_timestamp(6)),
    ('segment_applicability','ALL','ALL','ALL',4,b'1','Berlaku untuk semua segment.',null,current_timestamp(6),current_timestamp(6)),

    ('default_risk_class','LOW','LOW','L',1,b'1','Kelas risiko rendah.',null,current_timestamp(6),current_timestamp(6)),
    ('default_risk_class','MEDIUM','MEDIUM','M',2,b'1','Kelas risiko menengah.',null,current_timestamp(6),current_timestamp(6)),
    ('default_risk_class','HIGH','HIGH','H',3,b'1','Kelas risiko tinggi.',null,current_timestamp(6),current_timestamp(6));
