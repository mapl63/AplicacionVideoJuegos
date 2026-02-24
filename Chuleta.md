# 🔐 SEGURIDAD – MI PROYECTO

## 📌 JWT

JWT (JSON Web Token)  
👉 Es el token de autenticación  
👉 Se genera cuando el usuario hace login  
👉 Se envía en cada petición en el header:

Authorization: Bearer <token>

👉 Permite identificar al usuario sin usar sesión (STATELESS)

---

## 📂 config/auth  → MOTOR

Clases que tengo:

- SecurityConfig
- JwtAuthenticationFilter
- LoginSuccessHandler

---

## 1️⃣ SecurityConfig

👉 Configura toda la seguridad  
👉 Define qué rutas están protegidas  
👉 Activa JWT  
👉 Hace la aplicación STATELESS  
👉 Añade el JwtAuthenticationFilter

---

## 2️⃣ JwtAuthenticationFilter

👉 Se ejecuta en cada petición  
👉 Lee el header Authorization  
👉 Extrae el JWT  
👉 Lo valida  
👉 Si es correcto → mete el usuario en el SecurityContext

---

## 3️⃣ LoginSuccessHandler

👉 Se ejecuta cuando el login web es correcto  
👉 Solo afecta al login por formulario (no al JWT REST)

---

## 📂 rest/auth  → LOGIN REST

👉 Recibe usuario y contraseña  
👉 Si son correctos → genera JWT  
👉 Devuelve el token

---

# 🔥 FLUJO REAL

1) Login → rest/auth
2) Devuelve JWT
3) Petición con Authorization: Bearer
4) JwtAuthenticationFilter valida
5) Si es válido → entra al controller
6) @PreAuthorize controla roles

---

# 📌 RESUMEN FINAL

JWT = token de autenticación  
SecurityConfig = configura seguridad  
JwtAuthenticationFilter = valida token  
LoginSuccessHandler = login web  
rest/auth = genera el token  