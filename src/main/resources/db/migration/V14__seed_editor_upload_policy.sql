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
    ('editor_upload_policy','EXT_PNG','png','PNG',1,b'1','Allowed image extension for TinyMCE upload.',null,current_timestamp(6),current_timestamp(6)),
    ('editor_upload_policy','EXT_JPG','jpg','JPG',2,b'1','Allowed image extension for TinyMCE upload.',null,current_timestamp(6),current_timestamp(6)),
    ('editor_upload_policy','EXT_JPEG','jpeg','JPEG',3,b'1','Allowed image extension for TinyMCE upload.',null,current_timestamp(6),current_timestamp(6)),
    ('editor_upload_policy','EXT_WEBP','webp','WEBP',4,b'1','Allowed image extension for TinyMCE upload.',null,current_timestamp(6),current_timestamp(6)),
    ('editor_upload_policy','EXT_GIF','gif','GIF',5,b'1','Allowed image extension for TinyMCE upload.',null,current_timestamp(6),current_timestamp(6)),
    ('editor_upload_policy','MAX_SIZE_MB','5','MB',100,b'1','Maximum upload file size in MB for TinyMCE image upload.',null,current_timestamp(6),current_timestamp(6))
on duplicate key update
    item_name = values(item_name),
    legacy_code = values(legacy_code),
    sort_order = values(sort_order),
    active_flag = values(active_flag),
    description = values(description),
    extra_json = values(extra_json),
    updated_at = values(updated_at);
