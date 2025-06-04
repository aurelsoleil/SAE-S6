package sae.semestre.six.insurance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sae.semestre.six.insurance.entity.InsuranceHistory;

public interface InsuranceHistoryRepository extends JpaRepository<InsuranceHistory, Long> {
}