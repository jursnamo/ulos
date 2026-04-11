package com.enterprise.ulos.los.service;

import com.enterprise.ulos.los.entity.MasterPortfolioLookupEntity;
import com.enterprise.ulos.los.model.MasterDataApiModels;
import com.enterprise.ulos.los.repository.MasterFacilityRepository;
import com.enterprise.ulos.los.repository.MasterPortfolioLookupRepository;
import com.enterprise.ulos.los.repository.MasterProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MasterDataService {

    private final MasterProductRepository masterProductRepository;
    private final MasterFacilityRepository masterFacilityRepository;
    private final MasterPortfolioLookupRepository masterPortfolioLookupRepository;
    private final JsonSectionMapper jsonSectionMapper;

    public MasterDataService(
            MasterProductRepository masterProductRepository,
            MasterFacilityRepository masterFacilityRepository,
            MasterPortfolioLookupRepository masterPortfolioLookupRepository,
            JsonSectionMapper jsonSectionMapper
    ) {
        this.masterProductRepository = masterProductRepository;
        this.masterFacilityRepository = masterFacilityRepository;
        this.masterPortfolioLookupRepository = masterPortfolioLookupRepository;
        this.jsonSectionMapper = jsonSectionMapper;
    }

    public List<MasterDataApiModels.ProductResponse> products() {
        return masterProductRepository.findAll().stream()
                .filter(product -> product.isActive())
                .map(product -> new MasterDataApiModels.ProductResponse(
                        product.getProductCode(),
                        product.getProductName(),
                        product.getCategory()
                ))
                .toList();
    }

    public List<MasterDataApiModels.FacilityResponse> facilities() {
        return masterFacilityRepository.findAllByOrderByProductCodeAscFacilityCodeAsc().stream()
                .filter(facility -> facility.isActive())
                .map(facility -> new MasterDataApiModels.FacilityResponse(
                        facility.getFacilityCode(),
                        facility.getFacilityName(),
                        facility.getProductCode(),
                        Arrays.stream(facility.getSegmentCodes().split(",")).map(String::trim).toList(),
                        facility.isRevolving(),
                        facility.isFunded(),
                        facility.isNonFunded(),
                        jsonSectionMapper.fromJson(facility.getFieldMappingJson())
                ))
                .toList();
    }

    public List<MasterDataApiModels.PortfolioLookupResponse> portfolioLookups(String masterType, boolean includeInactive) {
        if (masterType == null || masterType.isBlank()) {
            return (includeInactive
                    ? masterPortfolioLookupRepository.findAllByOrderByMasterTypeAscSortOrderAscItemNameAsc()
                    : masterPortfolioLookupRepository.findAllByActiveTrueOrderByMasterTypeAscSortOrderAscItemNameAsc())
                    .stream()
                    .map(this::toPortfolioLookup)
                    .toList();
        }
        return (includeInactive
                ? masterPortfolioLookupRepository.findAllByMasterTypeIgnoreCaseOrderBySortOrderAscItemNameAsc(masterType.trim())
                : masterPortfolioLookupRepository.findAllByMasterTypeIgnoreCaseAndActiveTrueOrderBySortOrderAscItemNameAsc(masterType.trim()))
                .stream()
                .map(this::toPortfolioLookup)
                .toList();
    }

    public MasterDataApiModels.PortfolioLookupPageResponse portfolioLookupsPage(
            String masterType,
            boolean includeInactive,
            String keyword,
            int page,
            int size
    ) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.min(Math.max(size, 10), 200);
        PageRequest pageRequest = PageRequest.of(
                normalizedPage,
                normalizedSize,
                Sort.by(Sort.Order.asc("sortOrder"), Sort.Order.asc("itemName"))
        );

        Page<MasterPortfolioLookupEntity> result = masterPortfolioLookupRepository.search(
                masterType == null ? null : masterType.trim(),
                includeInactive,
                keyword == null ? "" : keyword.trim(),
                pageRequest
        );

        List<MasterDataApiModels.PortfolioLookupResponse> items = result.getContent().stream()
                .map(this::toPortfolioLookup)
                .toList();

        return new MasterDataApiModels.PortfolioLookupPageResponse(
                items,
                result.getTotalElements(),
                result.getTotalPages(),
                normalizedPage,
                normalizedSize
        );
    }

    @Transactional
    public MasterDataApiModels.PortfolioLookupResponse upsertPortfolioLookup(MasterDataApiModels.PortfolioLookupUpsertRequest request) {
        validatePortfolioLookupRequest(request);

        String normalizedType = request.masterType().trim().toLowerCase();
        String normalizedCode = request.code().trim().toUpperCase();

        MasterPortfolioLookupEntity entity = masterPortfolioLookupRepository
                .findByMasterTypeIgnoreCaseAndItemCodeIgnoreCase(normalizedType, normalizedCode)
                .orElseGet(MasterPortfolioLookupEntity::new);

        entity.setMasterType(normalizedType);
        entity.setItemCode(normalizedCode);
        entity.setItemName(request.name().trim());
        entity.setLegacyCode(trimToNull(request.legacyCode()));
        entity.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        entity.setActive(request.active() == null || request.active());
        entity.setDescription(trimToNull(request.description()));
        entity.setExtraJson(jsonSectionMapper.toJson(request.extraData()));

        return toPortfolioLookup(masterPortfolioLookupRepository.save(entity));
    }

    @Transactional
    public void deletePortfolioLookup(String masterType, String code) {
        if (masterType == null || masterType.isBlank() || code == null || code.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "masterType and code are required");
        }
        MasterPortfolioLookupEntity entity = masterPortfolioLookupRepository
                .findByMasterTypeIgnoreCaseAndItemCodeIgnoreCase(masterType.trim(), code.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Portfolio lookup not found"));
        masterPortfolioLookupRepository.delete(entity);
    }

    private MasterDataApiModels.PortfolioLookupResponse toPortfolioLookup(MasterPortfolioLookupEntity entity) {
        return new MasterDataApiModels.PortfolioLookupResponse(
                entity.getMasterType(),
                entity.getItemCode(),
                entity.getItemName(),
                entity.getLegacyCode(),
                entity.getSortOrder(),
                entity.isActive(),
                entity.getDescription(),
                jsonSectionMapper.fromJson(entity.getExtraJson())
        );
    }

    private void validatePortfolioLookupRequest(MasterDataApiModels.PortfolioLookupUpsertRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payload is required");
        }
        if (request.masterType() == null || request.masterType().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "masterType is required");
        }
        if (request.code() == null || request.code().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "code is required");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
