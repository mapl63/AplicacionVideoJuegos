package com.example.aplicacionvideojuegos.rest.users.services;

// ==========================
// IMPORTS DE LAS CLASES DEL PROYECTO
// ==========================

import com.example.aplicacionvideojuegos.rest.users.dto.UserInfoResponse; // DTO que devuelve info ampliada del usuario
import com.example.aplicacionvideojuegos.rest.users.dto.UserRequest;      // DTO que recibimos al crear/actualizar usuario
import com.example.aplicacionvideojuegos.rest.users.dto.UserResponse;     // DTO que devolvemos al guardar/actualizar
import com.example.aplicacionvideojuegos.rest.users.exceptions.UserNameOrEmailExists; // Excepción si ya existe usuario
import com.example.aplicacionvideojuegos.rest.users.exceptions.UserNotFound;          // Excepción si no existe usuario
import com.example.aplicacionvideojuegos.rest.users.mappers.UsersMapper;  // Mapper para convertir Entity <-> DTO
import com.example.aplicacionvideojuegos.rest.users.models.User;          // Entidad User
import com.example.aplicacionvideojuegos.rest.users.repositories.UsersRepository; // Repository de usuarios
import com.example.aplicacionvideojuegos.rest.videoJuegos.repositories.VideoJuegosRepository; // Repository de videojuegos

// ==========================
// IMPORTS DE TESTING
// ==========================

import org.junit.jupiter.api.Test;                     // Anotación para indicar método de test
import org.junit.jupiter.api.extension.ExtendWith;    // Permite usar extensiones (Mockito)
import org.mockito.InjectMocks;                       // Inyecta mocks en el service real
import org.mockito.Mock;                              // Crea objetos falsos (mocks)
import org.mockito.Spy;                               // Objeto real pero espiado por Mockito
import org.mockito.junit.jupiter.MockitoExtension;    // Extensión que activa Mockito en JUnit 5

import org.springframework.data.domain.Page;          // Tipo Page de Spring Data
import org.springframework.data.domain.PageImpl;      // Implementación concreta de Page
import org.springframework.data.domain.Pageable;      // Interfaz de paginación
import org.springframework.data.jpa.domain.Specification; // Especificaciones para filtros dinámicos

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*; // Métodos de aserción
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// ======================================================
// ACTIVAMOS MOCKITO PARA ESTA CLASE DE TEST
// ======================================================
@ExtendWith(MockitoExtension.class)
class UsersServiceImplTest {

    // ======================================================
    // OBJETOS DE PRUEBA REUTILIZABLES
    // ======================================================

    // DTO que simulamos que llega desde el controlador
    private final UserRequest userRequest = UserRequest
            .builder()
            .username("marius")
            .email("marius@test.com")
            .build();

    // Entidad simulada que devolvería la base de datos
    private final User user = User
            .builder()
            .id(99L)
            .username("marius")
            .email("marius@test.com")
            .build();

    /*
        Repository → Falso (@Mock)
        VideoJuegosRepository → Falso (@Mock)
        Mapper → Real pero espiado (@Spy)
        Service → Real con mocks inyectados (@InjectMocks)
     */

    // Simula la base de datos de usuarios
    @Mock
    private UsersRepository usersRepository;

    // Simula la base de datos de videojuegos
    @Mock
    private VideoJuegosRepository videoJuegosRepository;

    // Usamos el mapper real pero Mockito puede interceptarlo si queremos
    @Spy
    private UsersMapper usersMapper;

    // Creamos el service real e inyectamos los mocks automáticamente
    @InjectMocks
    private UsersServiceImpl usersService;

    // ======================================================
    // 🔵 TEST findAll CON FILTRO
    // ======================================================
    @Test
    public void testFindAll_WithNameFilter_ReturnsFilteredUsers(){

        // 1️⃣ ARRANGE
        // Simulamos que el repository devuelve una página con 1 usuario
        List<User> users = Arrays.asList(new User());
        Page<User> page = new PageImpl<>(users);

        when(usersRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        // 2️⃣ ACT
        // Ejecutamos el método real del service
        Page<UserResponse> result = usersService.findAll(
                Optional.of("Marius"), Optional.empty(), Optional.empty(), Pageable.unpaged());

        // 3️⃣ ASSERT
        // Comprobamos que no es null y que devuelve 1 elemento
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        // 4️⃣ VERIFY
        // Verificamos que se llamó al repository
        verify(usersRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    // ======================================================
    // 🔵 TEST findById CORRECTO
    // ======================================================
    @Test
    public void testFindById(){

        Long userId = 1L;

        // Simulamos que el usuario existe
        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(user));

        // Simulamos que no tiene videojuegos
        when(videoJuegosRepository.findByUsuarioId(userId))
                .thenReturn(List.of());

        // Ejecutamos el método
        UserInfoResponse result = usersService.findById(userId);

        // Comprobamos que devuelve datos correctos
        assertNotNull(result);
        assertEquals("marius", result.getUsername());
        assertEquals("marius@test.com", result.getEmail());

        // Verificamos llamadas
        verify(usersRepository).findById(userId);
        verify(videoJuegosRepository).findByUsuarioId(userId);
    }

    // ======================================================
    // 🔵 TEST findById LANZA EXCEPCIÓN
    // ======================================================
    @Test
    public void testFindById_UserNotFound_ThrowsUserNotFound(){

        Long userId = 1L;

        // Simulamos que no existe
        when(usersRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Comprobamos que lanza excepción
        assertThrows(UserNotFound.class,
                () -> usersService.findById(userId));

        verify(usersRepository).findById(userId);
    }

    // ======================================================
    // 🔵 TEST save CORRECTO
    // ======================================================
    @Test
    public void testSave_ValidUserRequest_ReturnsUserResponse(){

        // No existe usuario con mismo username/email
        when(usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(
                anyString(), anyString()))
                .thenReturn(Optional.empty());

        // Simulamos guardado correcto
        when(usersRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponse result = usersService.save(userRequest);

        assertNotNull(result);
        assertEquals("marius", result.getUsername());

        verify(usersRepository)
                .findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(anyString(), anyString());
        verify(usersRepository).save(any(User.class));
    }

    // ======================================================
    // 🔵 TEST save DUPLICADO
    // ======================================================
    @Test
    public void testSave_DuplicateUsernameOrEmail_ThrowsUserNameOrEmailExists(){

        when(usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(
                anyString(), anyString()))
                .thenReturn(Optional.of(user));

        assertThrows(UserNameOrEmailExists.class,
                () -> usersService.save(userRequest));
    }

    // ======================================================
    // 🔵 TEST update CORRECTO
    // ======================================================
    @Test
    public void testUpdate_ValidUserRequest_ReturnsUserResponse(){

        Long userId = 1L;

        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(
                anyString(), anyString()))
                .thenReturn(Optional.empty());

        when(usersRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponse result = usersService.update(userId, userRequest);

        assertNotNull(result);
        assertEquals("marius", result.getUsername());

        verify(usersRepository).findById(userId);
        verify(usersRepository)
                .findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(anyString(), anyString());
        verify(usersRepository).save(any(User.class));
    }

    // ======================================================
    // 🔵 TEST deleteById BORRADO FÍSICO
    // ======================================================
    @Test
    public void testDeleteById_PhisicalDelete() {

        Long userId = 1L;

        // Usuario existe
        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(user));

        // No tiene videojuegos
        when(videoJuegosRepository.existsByUsuarioId(userId))
                .thenReturn(false);

        usersService.deleteById(userId);

        // Se debe borrar físicamente
        verify(usersRepository).delete(user);
        verify(videoJuegosRepository).existsByUsuarioId(userId);
    }

    // ======================================================
    // 🔵 TEST deleteById BORRADO LÓGICO
    // ======================================================
    @Test
    public void testDeleteById_LogicalDelete(){

        Long userId = 1L;

        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(user));

        // Tiene videojuegos
        when(videoJuegosRepository.existsByUsuarioId(userId))
                .thenReturn(true);

        usersService.deleteById(userId);

        // Se marca como borrado lógico
        verify(usersRepository)
                .updateIsDeleteToTrueById(userId);

        verify(videoJuegosRepository)
                .existsByUsuarioId(userId);
    }
}