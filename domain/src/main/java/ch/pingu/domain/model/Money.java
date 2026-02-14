package ch.pingu.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object representing a monetary amount with currency
 */
public class Money {
    
    private final BigDecimal amount;
    private final Currency currency;
    
    public Money(BigDecimal amount, Currency currency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be null or negative");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }
    
    public Money(double amount, Currency currency) {
        this(BigDecimal.valueOf(amount), currency);
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public Currency getCurrency() {
        return currency;
    }
    
    public Money convert(Currency targetCurrency, BigDecimal exchangeRate) {
        if (this.currency == targetCurrency) {
            return this;
        }
        BigDecimal convertedAmount = amount.multiply(exchangeRate);
        return new Money(convertedAmount, targetCurrency);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 && currency == money.currency;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
    
    @Override
    public String toString() {
        return String.format("%s %.2f", currency.getCode(), amount);
    }
}
