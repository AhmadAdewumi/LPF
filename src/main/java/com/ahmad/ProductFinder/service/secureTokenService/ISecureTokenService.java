package com.ahmad.ProductFinder.service.secureTokenService;

import com.ahmad.ProductFinder.models.SecureToken;

public interface ISecureTokenService {
    SecureToken createToken();
    void saveSecureToken(SecureToken secureToken);
    SecureToken findByToken(String token);
    void removeToken(SecureToken token);
}
