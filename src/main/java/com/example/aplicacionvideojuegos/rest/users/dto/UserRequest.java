

package com.example.aplicacionvideojuegos.rest.users.dto;

import com.example.aplicacionvideojuegos.rest.users.models.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

// ======================================================
// 🔵 DTO UserRequest
// ======================================================
// ENTRADA (crear/editar) UserInfoResponse
//Mostrar usuario + información adicional
// ======================================================

@Data // 👈 Genera getters, setters, toString, equals, hashCode automáticamente
@Builder // 👈 Permite crear objetos con patrón Builder
@NoArgsConstructor // 👈 Constructor vacío necesario para Spring
@AllArgsConstructor // 👈 Constructor con todos los campos
public class UserRequest {

    @NotBlank(message = "Username no puede estar vacío")
    // 👈 Campo obligatorio (no puede ser null ni vacío)
    private String nombre;

    @NotBlank(message = "Apellidos no puede estar vacío")
    // 👈 Campo obligatorio
    private String apellidos;

    @NotBlank(message = "Username no puede estar vacío")
    // 👈 Campo obligatorio
    private String username;

    @Email(regexp = ".*@.*\\..*", message = "Email debe ser válido")
    @NotBlank(message = "Email no puede estar vacío")
    // 👈 Debe tener formato de email válido y no puede estar vacío
    private String email;

    @NotBlank(message = "Password no puede estar vacío")
    @Length(min = 5, message = "Password debe tener al menos 5 caracteres")
    @Size(min = 5, message = "Password debe tener al menos 5 caracteres")
    // 👈 Password obligatoria con mínimo 5 caracteres
    private String password;

    @Builder.Default
    // 👈 Si no se especifica rol, por defecto será USER
    private Set<Role> roles = Set.of(Role.USER);

    @Builder.Default
    // 👈 Indica si el usuario está eliminado lógicamente (soft delete)
    private boolean isDeleted = false;
}