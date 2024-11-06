package com.zpi.amoz.repository;

import com.zpi.amoz.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    @Modifying
    @Transactional
    @Query("UPDATE Company c SET c.isActive = false WHERE c.companyId = :companyId")
    int deactivateCompany(UUID companyId);
}

