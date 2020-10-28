package ecommerce.system.api.services.implementations;

import ecommerce.system.api.models.CredentialsModel;
import ecommerce.system.api.models.TokenModel;
import ecommerce.system.api.models.UserModel;
import ecommerce.system.api.repositories.IUserRepository;
import ecommerce.system.api.services.IAuthenticationService;
import ecommerce.system.api.tools.JwtHandler;
import ecommerce.system.api.tools.SHAEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Service
public class AuthenticationService implements IAuthenticationService {

    private final IUserRepository userRepository;
    private final JwtHandler jwtHandler;
    private final SHAEncoder shaEncoder;

    @Autowired
    public AuthenticationService(
            IUserRepository userRepository,
            JwtHandler jwtHandler,
            SHAEncoder shaEncoder) {

        this.userRepository = userRepository;
        this.jwtHandler = jwtHandler;
        this.shaEncoder = shaEncoder;
    }

    @Override
    public TokenModel authenticateUser(CredentialsModel credentials) throws NoSuchAlgorithmException {

        String encodedPassword = this.shaEncoder.encode(credentials.getPassword());
        credentials.setPassword(encodedPassword);

        UserModel user = this.userRepository.getUserByCredentials(credentials);

        if (user != null) {

            String token = this.jwtHandler.getToken(user.getEmail());
            LocalDateTime expirationDate = this.jwtHandler.getExpirationFromToken(token);

            return new TokenModel(token, expirationDate);
        }

        return null;
    }

    @Override
    public boolean isLoggedUser(int userId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UserModel user = this.userRepository.getUserByEmail(email);

        if (user == null || !user.isActive() || user.getUserId() != userId) {
            return false;
        }

        return true;
    }
}
