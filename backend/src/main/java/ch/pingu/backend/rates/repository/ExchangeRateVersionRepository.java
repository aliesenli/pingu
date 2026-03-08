package ch.pingu.backend.rates.repository;

import ch.pingu.backend.rates.model.ExchangeRateVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ExchangeRateVersionRepository extends JpaRepository<ExchangeRateVersion, String> {
    Optional<ExchangeRateVersion> findByActiveTrue();

    @Modifying
    @Query("UPDATE ExchangeRateVersion e SET e.active = false")
    void deactivateAll();
}
