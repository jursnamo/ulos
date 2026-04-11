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
    ('application_purpose','NEW','New','NEW',1,b'1','Pengajuan kredit baru.','{"purposeCategory":"NEW_CREDIT","segmentApplicability":"ALL","requiresSupportingDocument":true,"requiresCollateralChange":true,"requiresCommitteeApproval":true}',current_timestamp(6),current_timestamp(6)),
    ('application_purpose','ADDITIONAL_PLAFOND','Additional Plafond','APL',2,b'1','Permintaan penambahan limit fasilitas.','{"purposeCategory":"MAINTENANCE","segmentApplicability":"ALL","requiresSupportingDocument":true,"requiresCollateralChange":true,"requiresCommitteeApproval":true}',current_timestamp(6),current_timestamp(6)),
    ('application_purpose','CHANGES_NON_LIMIT','Changes Beyond Additional Plafond','CNP',3,b'1','Perubahan struktur/syarat selain penambahan plafon.','{"purposeCategory":"MAINTENANCE","segmentApplicability":"ALL","requiresSupportingDocument":true,"requiresCollateralChange":true,"requiresCommitteeApproval":true}',current_timestamp(6),current_timestamp(6)),
    ('application_purpose','PARTIAL_PERMANENT_SETTLEMENT','Partial Permanent Settlement','PPS',4,b'1','Pelunasan permanen sebagian atas fasilitas berjalan.','{"purposeCategory":"SETTLEMENT","segmentApplicability":"ALL","requiresSupportingDocument":true,"requiresCollateralChange":true,"requiresCommitteeApproval":false}',current_timestamp(6),current_timestamp(6)),
    ('application_purpose','EXTENSION_TERM_LOAN_REVIEW','Extension / Term Loan Review','ETR',5,b'1','Perpanjangan tenor atau review term loan.','{"purposeCategory":"MAINTENANCE","segmentApplicability":"ALL","requiresSupportingDocument":true,"requiresCollateralChange":false,"requiresCommitteeApproval":true}',current_timestamp(6),current_timestamp(6)),
    ('application_purpose','RESTRUCTURE_RESCHEDULE','Restructure and Reschedule','RRS',6,b'1','Restrukturisasi dan penjadwalan ulang kewajiban debitur.','{"purposeCategory":"RESTRUCTURE","segmentApplicability":"ALL","requiresSupportingDocument":true,"requiresCollateralChange":true,"requiresCommitteeApproval":true}',current_timestamp(6),current_timestamp(6)),
    ('application_purpose','LOAN_APP_NO_CHANGES','Loan Application (Without Changes)','LNC',7,b'1','Pengajuan lanjutan tanpa perubahan struktur kredit.','{"purposeCategory":"MAINTENANCE","segmentApplicability":"ALL","requiresSupportingDocument":true,"requiresCollateralChange":false,"requiresCommitteeApproval":false}',current_timestamp(6),current_timestamp(6)),
    ('application_purpose','CANCEL_ALL_FACILITY','Cancellation for All Facility - Permanent','CAF',8,b'1','Penutupan permanen seluruh fasilitas debitur.','{"purposeCategory":"SETTLEMENT","segmentApplicability":"ALL","requiresSupportingDocument":true,"requiresCollateralChange":true,"requiresCommitteeApproval":false}',current_timestamp(6),current_timestamp(6)),
    ('application_purpose','COLLATERAL_RELEASE','Collateral Release','CLR',9,b'1','Pelepasan agunan sesuai kebijakan dan syarat terpenuhi.','{"purposeCategory":"COLLATERAL","segmentApplicability":"ALL","requiresSupportingDocument":true,"requiresCollateralChange":true,"requiresCommitteeApproval":true}',current_timestamp(6),current_timestamp(6)),
    ('application_purpose','BLOCK_RENEWAL','Block Renewal','BLR',10,b'1','Pemblokiran perpanjangan fasilitas saat trigger risiko terpenuhi.','{"purposeCategory":"DOCUMENTATION","segmentApplicability":"ALL","requiresSupportingDocument":true,"requiresCollateralChange":false,"requiresCommitteeApproval":true}',current_timestamp(6),current_timestamp(6)),
    ('application_purpose','UPDATE_SUPPORTING_DOCUMENT','Update Supporting Document','USD',11,b'1','Update dokumen pendukung tanpa perubahan struktur fasilitas.','{"purposeCategory":"DOCUMENTATION","segmentApplicability":"ALL","requiresSupportingDocument":true,"requiresCollateralChange":false,"requiresCommitteeApproval":false}',current_timestamp(6),current_timestamp(6));
