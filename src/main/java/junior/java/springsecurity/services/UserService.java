package junior.java.springsecurity.services;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import junior.java.springsecurity.controller.dto.CreateUserDTO;
import junior.java.springsecurity.models.Role;
import junior.java.springsecurity.models.User;
import junior.java.springsecurity.repositories.RoleRepository;
import junior.java.springsecurity.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
            RoleRepository roleRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createUser(CreateUserDTO createUserDto) {
        Role basicRole = roleRepository.findByName(Role.Values.BASIC.name());

        var userFromDb = userRepository.findByUsername(createUserDto.username());
        if (userFromDb.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        var newUser = new User();
        newUser.setUsername(createUserDto.username());
        newUser.setPassword(passwordEncoder.encode(createUserDto.password()));
        newUser.setRoles(Set.of(basicRole));

        userRepository.save(newUser);
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }
}