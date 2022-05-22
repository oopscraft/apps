package org.oopscraft.apps.core.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

   private final UserRepository userRepository;

    public void saveUser(User user) {
        User one = userRepository.findById(user.getId()).orElse(null);
        if(one == null) {
            one = new User();
            one.setId(user.getId());
        }
        one.setName(user.getName());
        one.setEmail(user.getEmail());
        userRepository.saveAndFlush(user);
    }

}
