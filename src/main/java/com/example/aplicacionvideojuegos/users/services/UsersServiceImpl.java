package com.example.aplicacionvideojuegos.users.services;

import com.example.aplicacionvideojuegos.users.dto.UserInfoResponse;
import com.example.aplicacionvideojuegos.users.dto.UserRequest;
import com.example.aplicacionvideojuegos.users.dto.UserResponse;
import com.example.aplicacionvideojuegos.users.exceptions.UserNameOrEmailExists;
import com.example.aplicacionvideojuegos.users.exceptions.UserNotFound;
import com.example.aplicacionvideojuegos.users.mappers.UsersMapper;
import com.example.aplicacionvideojuegos.users.models.User;
import com.example.aplicacionvideojuegos.users.repositories.UsersRepository;
import com.example.aplicacionvideojuegos.videoJuegos.repositories.VideoJuegosRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"users"})
public class UsersServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;
    private final VideoJuegosRepository videoJuegosRepository;

    @Override
    public Page<UserResponse> findAll(
            Optional<String> username,
            Optional<String> email,
            Optional<Boolean> isDeleted,
            Pageable pageable
    ) {
        log.info("Buscando todos los usuarios con username: {} y borrados: {}", username, isDeleted);

        Specification<User> specUsernameUser = (root, query, criteriaBuilder) ->
                username.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> specEmailUser = (root, query, criteriaBuilder) ->
                email.map(e -> criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + e.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> criterio = Specification.allOf(
                specUsernameUser,
                specEmailUser,
                specIsDeleted
        );

        return usersRepository.findAll(criterio, pageable).map(usersMapper::toUserRsponse);

    }

    @Override
    @Cacheable(key = "#id")
    public UserInfoResponse findById(Long id) {
        log.info("Buscando usuario por id: {}", id);
        var user = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFound(id));

        var videoJuegos = videoJuegosRepository.findByUsuarioId(id).stream().map(p -> p.getNombre()).toList();

        return usersMapper.toUserInfoResponse(user, videoJuegos);
    }

    @Override
    @CachePut(key = "#result.id")
    public UserResponse save(UserRequest userRequest) {
        log.info("Guardando usuario: {}", userRequest);

        usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(userRequest.getUsername(), userRequest.getEmail())
                .ifPresent(u -> {
                    throw new UserNameOrEmailExists("Ya existe un usuario con el mismo username o email");
                });

        return usersMapper.toUserRsponse(usersRepository.save(usersMapper.toUserCreated(userRequest)));
    }

    @Override
    @CachePut(key = "#id")
    public UserResponse update(Long id, UserRequest userRequest) {
        log.info("Actualizando usuario con id: {}", id);

        usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));

        usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(userRequest.getUsername(), userRequest.getEmail())
                .ifPresent(u -> {
                    if (!u.getId().equals(id)) {
                        System.out.println("usuario encontrado: " + u.getId() + ", Mi id: " + id);
                        throw new UserNameOrEmailExists("Ya existe un usuario con el mismo username o email");
                    }
                });

        return usersMapper.toUserRsponse(usersRepository.save(usersMapper.toUserUpdated(userRequest, id)));
    }

    @Override
    @CacheEvict(key = "#id")
    @Transactional
    public void deleteById(Long id) {
        log.info("Borrando usuario por id: {}", id);

        User user = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFound(id));

        if (videoJuegosRepository.findByUsuarioId(id).isEmpty()) {
            // Si hay tarjetas, lo marcamos como borrado lógico
            log.info("Borrado lógico de usuario por id: {}", id);
            usersRepository.updateIsDeleteToTrueById(id);
        }else{
            // Si no hay tarjetas, lo borramos físicamente
            log.info("Borrado físico de usuario por id: {}", id);
            usersRepository.delete(user);
        }
    }

    public List<User> findAllActiveUsers(){
        log.info("Buscando todos los usuarios activos");
        return usersRepository.findAllByIsDeletedFalse();
    }

}
