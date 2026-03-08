package ch.pingu;

import ch.pingu.domain.model.User;
import ch.pingu.domain.service.AuthenticationService;
import ch.pingu.domain.service.CurrencyConversionService;
import ch.pingu.domain.service.TransactionService;
import ch.pingu.infrastructure.repository.ExchangeRateRepository;
import ch.pingu.infrastructure.repository.TransactionRepository;
import ch.pingu.infrastructure.repository.UserRepository;

/**
 * Application context for dependency injection
 */
public class AppContext {
    
    private static AppContext instance;
    
    private final UserRepository userRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final TransactionRepository transactionRepository;
    private final AuthenticationService authenticationService;
    private final CurrencyConversionService currencyConversionService;
    private final TransactionService transactionService;
    
    private User currentUser;
    
    private AppContext() {
        String projectRoot = System.getProperty("user.dir");
        
        // Try to determine the correct path based on where we're running from
        String dataDir;
        if (projectRoot.endsWith("presentation")) {
            dataDir = projectRoot + "/../infrastructure/src/main/resources/data";
        } else {
            dataDir = projectRoot + "/infrastructure/src/main/resources/data";
        }
        
        this.userRepository = new UserRepository(dataDir + "/users.json");
        this.exchangeRateRepository = new ExchangeRateRepository(dataDir + "/exchange-rates.json");
        this.transactionRepository = new TransactionRepository(dataDir + "/transactions.json");
        
        this.authenticationService = new AuthenticationService();
        this.currencyConversionService = new CurrencyConversionService();
        this.transactionService = new TransactionService();
    }
    
    public static AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }
    
    public UserRepository getUserRepository() {
        return userRepository;
    }
    
    public ExchangeRateRepository getExchangeRateRepository() {
        return exchangeRateRepository;
    }
    
    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }
    
    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }
    
    public CurrencyConversionService getCurrencyConversionService() {
        return currencyConversionService;
    }
    
    public TransactionService getTransactionService() {
        return transactionService;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public void logout() {
        this.currentUser = null;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
