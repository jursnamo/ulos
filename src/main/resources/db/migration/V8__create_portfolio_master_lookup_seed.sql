create table if not exists los_master_portfolio_lookup (
    id bigint not null auto_increment,
    master_type varchar(80) not null,
    item_code varchar(100) not null,
    item_name varchar(180) not null,
    legacy_code varchar(100),
    sort_order int not null default 0,
    active_flag bit not null,
    description varchar(500),
    extra_json longtext,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    unique key uk_los_portfolio_lookup_type_code (master_type, item_code),
    key idx_los_portfolio_lookup_type_active (master_type, active_flag, sort_order)
);

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
    ('customer_type','CORPORATE','Corporate','CORP',1,b'1','Badan usaha korporasi.','{"subjectKind":"COMPANY","segmentApplicability":"ALL","defaultRiskClass":"MEDIUM","requiresRelatedParty":true,"slikMandatory":true}',current_timestamp(6),current_timestamp(6)),
    ('customer_type','SME','SME','SME',2,b'1','Small and Medium Enterprise.','{"subjectKind":"COMPANY","segmentApplicability":"SME","defaultRiskClass":"MEDIUM","requiresRelatedParty":true,"slikMandatory":true}',current_timestamp(6),current_timestamp(6)),
    ('customer_type','INDIVIDUAL','Individual','INDV',3,b'1','Perorangan / personal borrower.','{"subjectKind":"INDIVIDUAL","segmentApplicability":"ALL","defaultRiskClass":"LOW","requiresRelatedParty":false,"slikMandatory":true}',current_timestamp(6),current_timestamp(6)),

    ('owning_branch','MAIN_JKT','Main Branch - Jakarta','001',1,b'1','Cabang utama Jakarta.',null,current_timestamp(6),current_timestamp(6)),
    ('owning_branch','CORP_SBY','Corporate Branch - Surabaya','002',2,b'1','Cabang corporate Surabaya.',null,current_timestamp(6),current_timestamp(6)),
    ('owning_branch','SME_BDG','SME Branch - Bandung','003',3,b'1','Cabang SME Bandung.',null,current_timestamp(6),current_timestamp(6)),

    ('business_source','DIRECT','Direct','DIR',1,b'1','Prospek direct dari RM.',null,current_timestamp(6),current_timestamp(6)),
    ('business_source','REFERRAL','Referral','REF',2,b'1','Prospek referral internal/eksternal.',null,current_timestamp(6),current_timestamp(6)),
    ('business_source','BROKER','Broker','BRK',3,b'1','Prospek melalui broker.',null,current_timestamp(6),current_timestamp(6)),
    ('business_source','EXISTING_GROUP','Existing Customer Group','GRP',4,b'1','Cross-sell dari group existing.',null,current_timestamp(6),current_timestamp(6)),

    ('referral_source','INTERNAL','Internal','INT',1,b'1','Referral internal bank.',null,current_timestamp(6),current_timestamp(6)),
    ('referral_source','EXTERNAL','External','EXT',2,b'1','Referral pihak eksternal.',null,current_timestamp(6),current_timestamp(6)),
    ('referral_source','PARTNER_BANK','Partner Bank','PBK',3,b'1','Referral dari partner bank.',null,current_timestamp(6),current_timestamp(6)),

    ('referral_region','JAKARTA_RAYA','Jakarta Raya','JKT',1,b'1','Wilayah referral Jakarta.',null,current_timestamp(6),current_timestamp(6)),
    ('referral_region','JAWA_BARAT','Jawa Barat','JBR',2,b'1','Wilayah referral Jawa Barat.',null,current_timestamp(6),current_timestamp(6)),
    ('referral_region','JAWA_TIMUR','Jawa Timur','JTM',3,b'1','Wilayah referral Jawa Timur.',null,current_timestamp(6),current_timestamp(6)),
    ('referral_region','SUMATERA','Sumatera','SUM',4,b'1','Wilayah referral Sumatera.',null,current_timestamp(6),current_timestamp(6)),
    ('referral_region','KALIMANTAN','Kalimantan','KLM',5,b'1','Wilayah referral Kalimantan.',null,current_timestamp(6),current_timestamp(6)),

    ('id_type','NPWP','NPWP','NPWP',1,b'1','Nomor Pokok Wajib Pajak.',null,current_timestamp(6),current_timestamp(6)),
    ('id_type','KTP','KTP','KTP',2,b'1','Kartu Tanda Penduduk.',null,current_timestamp(6),current_timestamp(6)),
    ('id_type','PASSPORT','Passport','PASS',3,b'1','Paspor.',null,current_timestamp(6),current_timestamp(6)),
    ('id_type','NIB','NIB','NIB',4,b'1','Nomor Induk Berusaha.',null,current_timestamp(6),current_timestamp(6)),
    ('id_type','SIUP','SIUP','SIUP',5,b'1','Surat Izin Usaha Perdagangan.',null,current_timestamp(6),current_timestamp(6)),

    ('sustainable_finance_risk','LOW','Low','L',1,b'1','Risiko rendah.',null,current_timestamp(6),current_timestamp(6)),
    ('sustainable_finance_risk','MEDIUM','Medium','M',2,b'1','Risiko menengah.',null,current_timestamp(6),current_timestamp(6)),
    ('sustainable_finance_risk','HIGH','High','H',3,b'1','Risiko tinggi.',null,current_timestamp(6),current_timestamp(6)),
    ('sustainable_finance_risk','VERY_HIGH','Very High','VH',4,b'1','Risiko sangat tinggi.',null,current_timestamp(6),current_timestamp(6)),

    ('company_type','PT','PT','PT',1,b'1','Perseroan Terbatas.',null,current_timestamp(6),current_timestamp(6)),
    ('company_type','CV','CV','CV',2,b'1','Commanditaire Vennootschap.',null,current_timestamp(6),current_timestamp(6)),
    ('company_type','FIRM','Firm','FIRM',3,b'1','Firma.',null,current_timestamp(6),current_timestamp(6)),
    ('company_type','COOPERATIVE','Cooperative','KOP',4,b'1','Koperasi.',null,current_timestamp(6),current_timestamp(6)),
    ('company_type','FOUNDATION','Foundation','YYS',5,b'1','Yayasan.',null,current_timestamp(6),current_timestamp(6)),

    ('country','ID','Indonesia','ID',1,b'1','Indonesia.',null,current_timestamp(6),current_timestamp(6)),
    ('country','SG','Singapore','SG',2,b'1','Singapore.',null,current_timestamp(6),current_timestamp(6)),
    ('country','MY','Malaysia','MY',3,b'1','Malaysia.',null,current_timestamp(6),current_timestamp(6)),
    ('country','US','United States','US',4,b'1','United States.',null,current_timestamp(6),current_timestamp(6)),
    ('country','JP','Japan','JP',5,b'1','Japan.',null,current_timestamp(6),current_timestamp(6)),
    ('country','CN','China','CN',6,b'1','China.',null,current_timestamp(6),current_timestamp(6)),

    ('company_activity_type','TRADING','Trading','TRD',1,b'1','Kegiatan perdagangan.',null,current_timestamp(6),current_timestamp(6)),
    ('company_activity_type','MANUFACTURING','Manufacturing','MFG',2,b'1','Kegiatan manufaktur.',null,current_timestamp(6),current_timestamp(6)),
    ('company_activity_type','SERVICES','Services','SRV',3,b'1','Jasa.',null,current_timestamp(6),current_timestamp(6)),
    ('company_activity_type','AGRIBUSINESS','Agribusiness','AGRI',4,b'1','Agribisnis.',null,current_timestamp(6),current_timestamp(6)),
    ('company_activity_type','INFRASTRUCTURE','Infrastructure','INFRA',5,b'1','Infrastruktur.',null,current_timestamp(6),current_timestamp(6)),

    ('yes_no_flag','YES','Yes','Y',1,b'1','Pilihan Ya.',null,current_timestamp(6),current_timestamp(6)),
    ('yes_no_flag','NO','No','N',2,b'1','Pilihan Tidak.',null,current_timestamp(6),current_timestamp(6)),

    ('citizen_code','WNI','WNI','WNI',1,b'1','Warga Negara Indonesia.',null,current_timestamp(6),current_timestamp(6)),
    ('citizen_code','WNA','WNA','WNA',2,b'1','Warga Negara Asing.',null,current_timestamp(6),current_timestamp(6)),

    ('financial_year_end_month','DECEMBER','December','12',1,b'1','Tutup buku Desember.',null,current_timestamp(6),current_timestamp(6)),
    ('financial_year_end_month','JUNE','June','06',2,b'1','Tutup buku Juni.',null,current_timestamp(6),current_timestamp(6)),
    ('financial_year_end_month','MARCH','March','03',3,b'1','Tutup buku Maret.',null,current_timestamp(6),current_timestamp(6)),
    ('financial_year_end_month','SEPTEMBER','September','09',4,b'1','Tutup buku September.',null,current_timestamp(6),current_timestamp(6)),

    ('debtor_type','NEW','New','NEW',1,b'1','Debitur baru.',null,current_timestamp(6),current_timestamp(6)),
    ('debtor_type','EXISTING','Existing','EXS',2,b'1','Debitur existing.',null,current_timestamp(6),current_timestamp(6)),
    ('debtor_type','RETURNING','Returning','RET',3,b'1','Debitur kembali aktif.',null,current_timestamp(6),current_timestamp(6)),

    ('connected_party_relationship','PARENT','Parent','PRT',1,b'1','Induk perusahaan.',null,current_timestamp(6),current_timestamp(6)),
    ('connected_party_relationship','SUBSIDIARY','Subsidiary','SUB',2,b'1','Anak perusahaan.',null,current_timestamp(6),current_timestamp(6)),
    ('connected_party_relationship','DIRECTOR','Director','DIR',3,b'1','Direktur.',null,current_timestamp(6),current_timestamp(6)),
    ('connected_party_relationship','COMMISSIONER','Commissioner','COM',4,b'1','Komisaris.',null,current_timestamp(6),current_timestamp(6)),
    ('connected_party_relationship','SHAREHOLDER','Shareholder','SHR',5,b'1','Pemegang saham.',null,current_timestamp(6),current_timestamp(6)),
    ('connected_party_relationship','AFFILIATE','Affiliate','AFF',6,b'1','Afiliasi.',null,current_timestamp(6),current_timestamp(6)),

    ('export_orientation','NON_EXPORT','Non-Export','NEX',1,b'1','Orientasi non ekspor.',null,current_timestamp(6),current_timestamp(6)),
    ('export_orientation','EXPORT','Export','EXP',2,b'1','Orientasi ekspor.',null,current_timestamp(6),current_timestamp(6)),
    ('export_orientation','MIXED','Mixed','MIX',3,b'1','Campuran ekspor/non-ekspor.',null,current_timestamp(6),current_timestamp(6)),

    ('debtor_classification','PRIME','Prime','PRM',1,b'1','Kelas debitur prime.',null,current_timestamp(6),current_timestamp(6)),
    ('debtor_classification','STANDARD','Standard','STD',2,b'1','Kelas debitur standar.',null,current_timestamp(6),current_timestamp(6)),
    ('debtor_classification','WATCHLIST','Watchlist','WCH',3,b'1','Kelas debitur watchlist.',null,current_timestamp(6),current_timestamp(6)),

    ('debtor_status','ACTIVE','Active','ACT',1,b'1','Status aktif.',null,current_timestamp(6),current_timestamp(6)),
    ('debtor_status','INACTIVE','Inactive','INA',2,b'1','Status tidak aktif.',null,current_timestamp(6),current_timestamp(6)),
    ('debtor_status','UNDER_REVIEW','Under Review','URV',3,b'1','Sedang direview.',null,current_timestamp(6),current_timestamp(6)),

    ('sales_volume_category','LARGE','Large','L',1,b'1','Penjualan besar.',null,current_timestamp(6),current_timestamp(6)),
    ('sales_volume_category','MEDIUM','Medium','M',2,b'1','Penjualan menengah.',null,current_timestamp(6),current_timestamp(6)),
    ('sales_volume_category','SMALL','Small','S',3,b'1','Penjualan kecil.',null,current_timestamp(6),current_timestamp(6)),
    ('sales_volume_category','MICRO','Micro','MI',4,b'1','Penjualan mikro.',null,current_timestamp(6),current_timestamp(6)),

    ('debtor_category','CORPORATE','Corporate','CORP',1,b'1','Kategori corporate.',null,current_timestamp(6),current_timestamp(6)),
    ('debtor_category','SME','SME','SME',2,b'1','Kategori SME.',null,current_timestamp(6),current_timestamp(6)),
    ('debtor_category','COMMERCIAL','Commercial','COM',3,b'1','Kategori commercial.',null,current_timestamp(6),current_timestamp(6)),
    ('debtor_category','PUBLIC_SECTOR','Public Sector','PUB',4,b'1','Kategori sektor publik.',null,current_timestamp(6),current_timestamp(6)),

    ('credit_behavior','GOOD','Good','G',1,b'1','Perilaku kredit baik.',null,current_timestamp(6),current_timestamp(6)),
    ('credit_behavior','FAIR','Fair','F',2,b'1','Perilaku kredit cukup.',null,current_timestamp(6),current_timestamp(6)),
    ('credit_behavior','POOR','Poor','P',3,b'1','Perilaku kredit buruk.',null,current_timestamp(6),current_timestamp(6)),

    ('sector_l1','MANUFACTURING','Manufacturing','MFG',1,b'1','Sektor manufaktur.',null,current_timestamp(6),current_timestamp(6)),
    ('sector_l1','TRADE','Trade','TRD',2,b'1','Sektor perdagangan.',null,current_timestamp(6),current_timestamp(6)),
    ('sector_l1','SERVICES','Services','SRV',3,b'1','Sektor jasa.',null,current_timestamp(6),current_timestamp(6)),
    ('sector_l1','AGRICULTURE','Agriculture','AGR',4,b'1','Sektor pertanian.',null,current_timestamp(6),current_timestamp(6)),
    ('sector_l1','INFRASTRUCTURE','Infrastructure','INF',5,b'1','Sektor infrastruktur.',null,current_timestamp(6),current_timestamp(6)),

    ('sector_l2_l3','FOOD_PROCESSING','Food Processing','FP',1,b'1','Sub sektor food processing.',null,current_timestamp(6),current_timestamp(6)),
    ('sector_l2_l3','WHOLESALE_TRADING','Wholesale Trading','WT',2,b'1','Sub sektor wholesale trading.',null,current_timestamp(6),current_timestamp(6)),
    ('sector_l2_l3','LOGISTICS','Logistics','LOG',3,b'1','Sub sektor logistik.',null,current_timestamp(6),current_timestamp(6)),
    ('sector_l2_l3','ENERGY_SUPPORT','Energy Support','ENS',4,b'1','Sub sektor pendukung energi.',null,current_timestamp(6),current_timestamp(6)),

    ('bi_industrial_code','BI_100','BI-100','BI100',1,b'1','Kode industri BI-100.',null,current_timestamp(6),current_timestamp(6)),
    ('bi_industrial_code','BI_200','BI-200','BI200',2,b'1','Kode industri BI-200.',null,current_timestamp(6),current_timestamp(6)),
    ('bi_industrial_code','BI_300','BI-300','BI300',3,b'1','Kode industri BI-300.',null,current_timestamp(6),current_timestamp(6)),
    ('bi_industrial_code','BI_400','BI-400','BI400',4,b'1','Kode industri BI-400.',null,current_timestamp(6),current_timestamp(6)),

    ('funded_sector','CONSUMER','Consumer','CONS',1,b'1','Funded sektor consumer.',null,current_timestamp(6),current_timestamp(6)),
    ('funded_sector','COMMERCIAL','Commercial','COMM',2,b'1','Funded sektor commercial.',null,current_timestamp(6),current_timestamp(6)),
    ('funded_sector','PROJECT','Project','PROJ',3,b'1','Funded sektor project.',null,current_timestamp(6),current_timestamp(6)),
    ('funded_sector','TRADE','Trade','TRD',4,b'1','Funded sektor trade.',null,current_timestamp(6),current_timestamp(6)),

    ('sekom_lbu','LBU_01','LBU-01','LBU01',1,b'1','Mapping LBU 01.',null,current_timestamp(6),current_timestamp(6)),
    ('sekom_lbu','LBU_02','LBU-02','LBU02',2,b'1','Mapping LBU 02.',null,current_timestamp(6),current_timestamp(6)),
    ('sekom_lbu','LBU_03','LBU-03','LBU03',3,b'1','Mapping LBU 03.',null,current_timestamp(6),current_timestamp(6)),

    ('industry_rac_exception','NONE','None','NONE',1,b'1','Tanpa exception.',null,current_timestamp(6),current_timestamp(6)),
    ('industry_rac_exception','APPROVED','Approved','APV',2,b'1','Exception disetujui.',null,current_timestamp(6),current_timestamp(6)),
    ('industry_rac_exception','TEMP_WAIVER','Temporary Waiver','TWV',3,b'1','Waiver sementara.',null,current_timestamp(6),current_timestamp(6)),

    ('collectability','COL_1','1 - Pass','1',1,b'1','Kolektibilitas 1.',null,current_timestamp(6),current_timestamp(6)),
    ('collectability','COL_2','2 - Special Mention','2',2,b'1','Kolektibilitas 2.',null,current_timestamp(6),current_timestamp(6)),
    ('collectability','COL_3','3 - Substandard','3',3,b'1','Kolektibilitas 3.',null,current_timestamp(6),current_timestamp(6)),
    ('collectability','COL_4','4 - Doubtful','4',4,b'1','Kolektibilitas 4.',null,current_timestamp(6),current_timestamp(6)),
    ('collectability','COL_5','5 - Loss','5',5,b'1','Kolektibilitas 5.',null,current_timestamp(6),current_timestamp(6)),

    ('sector_appetite','HIGH','High','H',1,b'1','Appetite tinggi.',null,current_timestamp(6),current_timestamp(6)),
    ('sector_appetite','MEDIUM','Medium','M',2,b'1','Appetite menengah.',null,current_timestamp(6),current_timestamp(6)),
    ('sector_appetite','LOW','Low','L',3,b'1','Appetite rendah.',null,current_timestamp(6),current_timestamp(6)),
    ('sector_appetite','RESTRICTED','Restricted','R',4,b'1','Appetite dibatasi.',null,current_timestamp(6),current_timestamp(6)),

    ('rating_behavior','IMPROVING','Improving','IMP',1,b'1','Trend rating membaik.',null,current_timestamp(6),current_timestamp(6)),
    ('rating_behavior','STABLE','Stable','STB',2,b'1','Trend rating stabil.',null,current_timestamp(6),current_timestamp(6)),
    ('rating_behavior','DECLINING','Declining','DEC',3,b'1','Trend rating menurun.',null,current_timestamp(6),current_timestamp(6)),

    ('banking_relationship_type','NEW','New','NEW',1,b'1','Relasi bank baru.',null,current_timestamp(6),current_timestamp(6)),
    ('banking_relationship_type','EXISTING','Existing','EXS',2,b'1','Relasi bank existing.',null,current_timestamp(6),current_timestamp(6)),
    ('banking_relationship_type','DORMANT_REACT','Dormant Re-activation','DRT',3,b'1','Reaktivasi relasi dormant.',null,current_timestamp(6),current_timestamp(6)),

    ('district','MENTENG','Menteng','MTG',1,b'1','Kecamatan Menteng.',null,current_timestamp(6),current_timestamp(6)),
    ('district','SETIABUDI','Setiabudi','STB',2,b'1','Kecamatan Setiabudi.',null,current_timestamp(6),current_timestamp(6)),
    ('district','SUKAJADI','Sukajadi','SKJ',3,b'1','Kecamatan Sukajadi.',null,current_timestamp(6),current_timestamp(6)),
    ('district','WONOKROMO','Wonokromo','WNK',4,b'1','Kecamatan Wonokromo.',null,current_timestamp(6),current_timestamp(6)),

    ('village','KEBON_SIRIH','Kebon Sirih','KBS',1,b'1','Kelurahan Kebon Sirih.',null,current_timestamp(6),current_timestamp(6)),
    ('village','KARET','Karet','KRT',2,b'1','Kelurahan Karet.',null,current_timestamp(6),current_timestamp(6)),
    ('village','PASTEUR','Pasteur','PST',3,b'1','Kelurahan Pasteur.',null,current_timestamp(6),current_timestamp(6)),
    ('village','DARMO','Darmo','DRM',4,b'1','Kelurahan Darmo.',null,current_timestamp(6),current_timestamp(6)),

    ('province','DKI_JAKARTA','DKI Jakarta','JKT',1,b'1','Provinsi DKI Jakarta.',null,current_timestamp(6),current_timestamp(6)),
    ('province','JAWA_BARAT','Jawa Barat','JBR',2,b'1','Provinsi Jawa Barat.',null,current_timestamp(6),current_timestamp(6)),
    ('province','JAWA_TIMUR','Jawa Timur','JTM',3,b'1','Provinsi Jawa Timur.',null,current_timestamp(6),current_timestamp(6)),
    ('province','BANTEN','Banten','BTN',4,b'1','Provinsi Banten.',null,current_timestamp(6),current_timestamp(6)),

    ('city','JAKARTA_PUSAT','Jakarta Pusat','JKP',1,b'1','Kota Jakarta Pusat.',null,current_timestamp(6),current_timestamp(6)),
    ('city','BANDUNG','Bandung','BDG',2,b'1','Kota Bandung.',null,current_timestamp(6),current_timestamp(6)),
    ('city','SURABAYA','Surabaya','SBY',3,b'1','Kota Surabaya.',null,current_timestamp(6),current_timestamp(6)),
    ('city','TANGERANG','Tangerang','TGR',4,b'1','Kota Tangerang.',null,current_timestamp(6),current_timestamp(6)),

    ('premise_type','OWNED','Owned','OWN',1,b'1','Premis milik sendiri.',null,current_timestamp(6),current_timestamp(6)),
    ('premise_type','RENTED','Rented','RNT',2,b'1','Premis sewa.',null,current_timestamp(6),current_timestamp(6)),
    ('premise_type','LEASED','Leased','LSD',3,b'1','Premis lease.',null,current_timestamp(6),current_timestamp(6)),

    ('salutation','MR','Mr.','MR',1,b'1','Sapaan Mr.',null,current_timestamp(6),current_timestamp(6)),
    ('salutation','MS','Ms.','MS',2,b'1','Sapaan Ms.',null,current_timestamp(6),current_timestamp(6)),
    ('salutation','MRS','Mrs.','MRS',3,b'1','Sapaan Mrs.',null,current_timestamp(6),current_timestamp(6)),
    ('salutation','DR','Dr.','DR',4,b'1','Sapaan Dr.',null,current_timestamp(6),current_timestamp(6)),

    ('prospecting_outcome','INTERESTED','Interested','INT',1,b'1','Prospek tertarik.',null,current_timestamp(6),current_timestamp(6)),
    ('prospecting_outcome','NOT_INTERESTED','Not Interested','NIN',2,b'1','Prospek tidak tertarik.',null,current_timestamp(6),current_timestamp(6)),
    ('prospecting_outcome','FOLLOW_UP','Follow Up','FLW',3,b'1','Perlu follow up.',null,current_timestamp(6),current_timestamp(6)),
    ('prospecting_outcome','POSTPONED','Postponed','PST',4,b'1','Prospek ditunda.',null,current_timestamp(6),current_timestamp(6)),

    ('sub_region','ASEAN','ASEAN','ASN',1,b'1','Sub region ASEAN.',null,current_timestamp(6),current_timestamp(6)),
    ('sub_region','APAC','APAC','APC',2,b'1','Sub region APAC.',null,current_timestamp(6),current_timestamp(6)),
    ('sub_region','EUROPE','Europe','EUR',3,b'1','Sub region Eropa.',null,current_timestamp(6),current_timestamp(6)),
    ('sub_region','AMERICAS','Americas','AMR',4,b'1','Sub region Amerika.',null,current_timestamp(6),current_timestamp(6)),
    ('sub_region','DOMESTIC','Domestic','DOM',5,b'1','Sub region domestik.',null,current_timestamp(6),current_timestamp(6)),

    ('continent','ASIA','Asia','AS',1,b'1','Benua Asia.',null,current_timestamp(6),current_timestamp(6)),
    ('continent','EUROPE','Europe','EU',2,b'1','Benua Eropa.',null,current_timestamp(6),current_timestamp(6)),
    ('continent','NORTH_AMERICA','North America','NA',3,b'1','Benua Amerika Utara.',null,current_timestamp(6),current_timestamp(6)),
    ('continent','SOUTH_AMERICA','South America','SA',4,b'1','Benua Amerika Selatan.',null,current_timestamp(6),current_timestamp(6)),
    ('continent','AFRICA','Africa','AF',5,b'1','Benua Afrika.',null,current_timestamp(6),current_timestamp(6)),
    ('continent','OCEANIA','Oceania','OC',6,b'1','Benua Oceania.',null,current_timestamp(6),current_timestamp(6))
on duplicate key update
    item_name = values(item_name),
    legacy_code = values(legacy_code),
    sort_order = values(sort_order),
    active_flag = values(active_flag),
    description = values(description),
    extra_json = values(extra_json),
    updated_at = values(updated_at);
