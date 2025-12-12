package com.example.aplicacionvideojuegos.auth.services.users;



import com.example.aplicacionvideojuegos.auth.repositories.AuthUsersRepository;
import com.example.aplicacionvideojuegos.users.exceptions.UserNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("userDetailsService")
public class AuthUsersServiceImpl  implements AuthUsersService {

    private final AuthUsersRepository authUsersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFound {
        return authUsersRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("Usuario con username: " + username + " no encontrado"));
    }
}
