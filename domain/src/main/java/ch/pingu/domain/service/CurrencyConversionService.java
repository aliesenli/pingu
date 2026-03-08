package ch.pingu.domain.service;

import ch.pingu.domain.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Domain service for currency conversion operations
 */
public class CurrencyConversionService {
    
    public Money convert(Money source, Currency targetCurrency, ExchangeRateVersion rateVersion) {
        if (source.getCurrency() == targetCurrency) {
            return source;
        }
        
        BigDecimal rate = calculateExchangeRate(
            source.getCurrency(),
            targetCurrency,
            rateVersion
        );
        
        return source.convert(targetCurrency, rate);
    }
    
    public BigDecimal calculateExchangeRate(Currency source, Currency target, 
                                           ExchangeRateVersion rateVersion) {
        if (source == target) {
            return BigDecimal.ONE;
        }
        
        Currency baseCurrency = rateVersion.getBaseCurrency();
        
        if (source == baseCurrency) {
            Double rate = rateVersion.getRate(target);
            if (rate == null) {
                throw new IllegalArgumentException("Exchange rate not found for " + target);
            }
            return BigDecimal.valueOf(rate).setScale(6, RoundingMode.HALF_UP);
        }
        
        if (target == baseCurrency) {
            Double rate = rateVersion.getRate(source);
            if (rate == null) {
                throw new IllegalArgumentException("Exchange rate not found for " + source);
            }
            return BigDecimal.ONE.divide(
                BigDecimal.valueOf(rate), 
                6, 
                RoundingMode.HALF_UP
            );
        }
        
        Double sourceRate = rateVersion.getRate(source);
        Double targetRate = rateVersion.getRate(target);
        
        if (sourceRate == null || targetRate == null) {
            throw new IllegalArgumentException(
                "Exchange rates not found for conversion from " + source + " to " + target
            );
        }
        
        return BigDecimal.valueOf(targetRate)
            .divide(BigDecimal.valueOf(sourceRate), 6, RoundingMode.HALF_UP);
    }
    
    public double getExchangeRateAsDouble(Currency source, Currency target, 
                                         ExchangeRateVersion rateVersion) {
        return calculateExchangeRate(source, target, rateVersion).doubleValue();
    }
}
