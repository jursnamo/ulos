package com.enterprise.ulos.los.service;

import com.enterprise.ulos.los.entity.CustomerEntity;
import com.enterprise.ulos.los.model.CustomerApiModels;
import com.enterprise.ulos.los.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CustomerPortfolioService {

    private final CustomerRepository customerRepository;
    private final JsonSectionMapper jsonSectionMapper;

    public CustomerPortfolioService(CustomerRepository customerRepository, JsonSectionMapper jsonSectionMapper) {
        this.customerRepository = customerRepository;
        this.jsonSectionMapper = jsonSectionMapper;
    }

    public CustomerApiModels.CustomerDetailResponse save(CustomerApiModels.CustomerRequest request) {
        validateRequest(request);

        String cif = request.cifNumber() == null || request.cifNumber().isBlank()
                ? generateCif()
                : request.cifNumber().trim();

        CustomerEntity entity = customerRepository.findByCifNumber(cif).orElseGet(CustomerEntity::new);
        entity.setCifNumber(cif);
        entity.setCompanyName(request.companyName().trim());
        entity.setLegalName(request.legalName());
        entity.setCompanyType(defaultValue(request.companyType(), "Corporate"));
        entity.setRegistrationDate(request.registrationDate() == null ? LocalDate.now() : request.registrationDate());
        entity.setTaxId(request.taxId());
        entity.setSector(request.sector());
        entity.setLocation(request.location());
        entity.setStatus(defaultValue(request.status(), "ACTIVE"));
        entity.setProfileJson(jsonSectionMapper.toJson(request.profileData()));

        return toDetail(customerRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<CustomerApiModels.CustomerSummaryResponse> list() {
        return customerRepository.findAll().stream()
                .sorted(Comparator.comparing(CustomerEntity::getUpdatedAt).reversed())
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerApiModels.CustomerSummaryPageResponse listPage(String keyword, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        String normalizedKeyword = keyword == null ? null : keyword.trim();
        if (normalizedKeyword != null && normalizedKeyword.isBlank()) {
            normalizedKeyword = null;
        }

        Page<CustomerEntity> result = customerRepository.search(
                normalizedKeyword,
                PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "updatedAt"))
        );

        List<CustomerApiModels.CustomerSummaryResponse> items = result.getContent().stream()
                .map(this::toSummary)
                .toList();

        return new CustomerApiModels.CustomerSummaryPageResponse(
                items,
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @Transactional(readOnly = true)
    public CustomerApiModels.CustomerDetailResponse getByCif(String cifNumber) {
        return toDetail(findEntityByCif(cifNumber));
    }

    @Transactional(readOnly = true)
    public CustomerEntity findEntityByCif(String cifNumber) {
        return customerRepository.findByCifNumber(cifNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found: " + cifNumber));
    }

    private CustomerApiModels.CustomerDetailResponse toDetail(CustomerEntity entity) {
        return new CustomerApiModels.CustomerDetailResponse(
                entity.getId(),
                entity.getCifNumber(),
                entity.getCompanyName(),
                entity.getLegalName(),
                entity.getCompanyType(),
                entity.getRegistrationDate(),
                entity.getTaxId(),
                entity.getSector(),
                entity.getLocation(),
                entity.getStatus(),
                jsonSectionMapper.fromJson(entity.getProfileJson()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private void validateRequest(CustomerApiModels.CustomerRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer payload is required");
        }
        if (request.companyName() == null || request.companyName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyName is required");
        }
    }

    private CustomerApiModels.CustomerSummaryResponse toSummary(CustomerEntity entity) {
        return new CustomerApiModels.CustomerSummaryResponse(
                entity.getCifNumber(),
                entity.getCompanyName(),
                entity.getSector(),
                entity.getLocation(),
                entity.getStatus(),
                entity.getUpdatedAt()
        );
    }

    private String generateCif() {
        return "DEB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String defaultValue(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
