package ch.pingu;

import ch.pingu.domain.model.User;
import ch.pingu.domain.service.AuthenticationService;
import io.github.cdimascio.dotenv.Dotenv;
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
    private String jwtToken;

    private String baseUrl;

    private AppContext() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.baseUrl = dotenv.get("PINGU_API_URL", "");
        this.userRepository = new UserRepository(baseUrl);
        this.exchangeRateRepository = new ExchangeRateRepository(baseUrl);
        this.transactionRepository = new TransactionRepository(baseUrl);
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
    
    public String getBaseUrl() {
        return baseUrl;
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

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public void logout() {
        this.currentUser = null;
        this.jwtToken = null;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null && jwtToken != null;
    }
}
