package com.platform.sosangongin.cases.employment;

import com.platform.sosangongin.domains.business.Business;
import com.platform.sosangongin.domains.business.BusinessRepository;
import com.platform.sosangongin.domains.employment.*;
import com.platform.sosangongin.errors.AccessDeniedException;
import com.platform.sosangongin.errors.AlreadyResignedException;
import com.platform.sosangongin.errors.EntityNotFoundException;
import com.platform.sosangongin.errors.EntityType;
import com.platform.sosangongin.errors.HasAssignedRoleException;
import com.platform.sosangongin.errors.InvalidEmploymentStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmploymentUseCase {

    private final BusinessRepository businessRepository;
    private final EmploymentRepository employmentRepository;
    private final EmploymentRoleRepository employmentRoleRepository;

    @Transactional(readOnly = true)
    public EmployeeListResult listEmployees(UUID userId, UUID businessId, EmployeeListSearchRequest req) {
        Business business = loadAndAuthorize(userId, businessId);
        PageRequest pageable = PageRequest.of(req.getPage(), req.getSize());

        Page<EmployeeVo> employees;
        if ("ALL".equalsIgnoreCase(req.getStatus())) {
            employees = employmentRepository.findByBusiness(business, pageable)
                    .map(e -> EmployeeVo.from(e, employmentRoleRepository.findByEmployment(e)));
        } else {
            EmploymentStatus status = parseStatus(req.getStatus());
            employees = employmentRepository.findByBusinessAndStatus(business, status, pageable)
                    .map(e -> EmployeeVo.from(e, employmentRoleRepository.findByEmployment(e)));
        }
        // TODO: N+1 문제 개선 필요 — @EntityGraph 또는 batch fetch 적용 예정

        return EmployeeListResult.builder()
                .employees(employees)
                .build();
    }

    @Transactional(readOnly = true)
    public RoleUsageResult checkRoleUsage(UUID userId, UUID businessId, Long employmentId) {
        Business business = loadAndAuthorize(userId, businessId);
        Employment employment = loadEmployment(employmentId, business);

        List<EmploymentRole> employmentRoles = employmentRoleRepository.findByEmployment(employment);
        List<EmployeeRoleVo> roles = employmentRoles.stream()
                .map(er -> EmployeeRoleVo.from(er.getRole()))
                .toList();

        return RoleUsageResult.builder()
                .hasAssignedRoles(!roles.isEmpty())
                .roles(roles)
                .build();
    }

    @Transactional
    public ResignResult resign(UUID userId, UUID businessId, Long employmentId) {
        Business business = loadAndAuthorize(userId, businessId);
        Employment employment = loadEmployment(employmentId, business);

        if (employment.getStatus() == EmploymentStatus.RESIGNED) {
            throw new AlreadyResignedException(employmentId);
        }

        List<EmploymentRole> employmentRoles = employmentRoleRepository.findByEmployment(employment);
        if (!employmentRoles.isEmpty()) {
            throw new HasAssignedRoleException(employmentId);
        }

        employment.changeStatus(EmploymentStatus.RESIGNED);

        return ResignResult.builder()
                .employmentId(employment.getId())
                .userName(employment.getUser().getName())
                .status(employment.getStatus())
                .resignedAt(employment.getUpdatedAt())
                .build();
    }

    private Business loadAndAuthorize(UUID userId, UUID businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new EntityNotFoundException(businessId, EntityType.BUSINESS, "business not found"));
        if (!business.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException(businessId, EntityType.BUSINESS, "해당 사업체의 사장만 접근 가능합니다");
        }
        return business;
    }

    private Employment loadEmployment(Long employmentId, Business business) {
        return employmentRepository.findByIdAndBusiness(employmentId, business)
                .orElseThrow(() -> new EntityNotFoundException(employmentId, EntityType.EMPLOYMENT, "employment not found"));
    }

    private EmploymentStatus parseStatus(String status) {
        try {
            return EmploymentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidEmploymentStatusException(status);
        }
    }
}