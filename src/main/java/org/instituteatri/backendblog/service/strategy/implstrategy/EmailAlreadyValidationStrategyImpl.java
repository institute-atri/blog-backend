package org.instituteatri.backendblog.service.strategy.implstrategy;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.infrastructure.exceptions.EmailAlreadyExistsException;
import org.instituteatri.backendblog.repository.UserRepository;
import org.instituteatri.backendblog.service.strategy.interfaces.EmailAlreadyValidationStrategy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailAlreadyValidationStrategyImpl implements EmailAlreadyValidationStrategy {

    private final UserRepository userRepository;

    @Override
    public void validate(String existingEmail, String newEmail, String userIdToExclude) {
        if (!existingEmail.equals(newEmail) && checkIfEmailExists(newEmail, userIdToExclude)) {
            throw new EmailAlreadyExistsException();
        }
    }

    private boolean checkIfEmailExists(String email, String userIdToExclude) {
        User user = (User) userRepository.findByEmail(email);
        return user != null && !user.getId().equals(userIdToExclude);
    }
}
