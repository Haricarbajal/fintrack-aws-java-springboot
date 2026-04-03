# finTrack - Plan de Desarrollo Profesional

## Sobre este proyecto
App de gestion financiera personal. El objetivo es construir un backend profesional, atractivo para reclutadores, integrando Java, Spring Boot, AWS e IA.

## Modo de trabajo
- Estefano es quien escribe el codigo. Yo explico paso a paso y guio.
- NO escribir codigo por el. Explicar el "por que" y el "como" para que aprenda.
- Comunicacion en espanol.
- Ir paso a paso, un tema a la vez, asegurando que entienda antes de avanzar.

## Estado actual del proyecto
- Auth completo: registro, login, JWT, BCrypt
- CRUD de transacciones (crear, leer, actualizar, eliminar)
- Seguridad: aislamiento por usuario con JWT
- Modelos: User, Transaction, TipoTransaccion (INGRESO/GASTO)
- DTOs: LoginRequest, RegisterRequest, AuthResponse
- MySQL con JPA/Hibernate

## Plan de mejoras - Fases

### FASE 1: Backend profesional (fundamentos)
Objetivo: que el codigo se vea limpio, robusto y con buenas practicas.

- [x] 1.1 Validacion de entrada (@Valid, @NotBlank, @Email, @Min en DTOs) ✓ COMPLETADO
- [x] 1.2 Excepciones personalizadas (ResourceNotFoundException, DuplicateResourceException, etc.) ✓ COMPLETADO
- [x] 1.3 Manejo global de errores (@ControllerAdvice + @ExceptionHandler) ✓ COMPLETADO
- [ ] 1.4 DTOs de respuesta para Transaction (no exponer entidades directamente)
- [ ] 1.5 Logging con SLF4J (@Slf4j en services y controllers)
- [ ] 1.6 Configuracion CORS (preparar para frontend)
- [ ] 1.7 Variables de entorno para secretos (JWT secret, DB password) - no hardcodear

### FASE 2: Funcionalidades avanzadas
Objetivo: features que demuestran dominio de Spring Boot.

- [ ] 2.1 Filtrado de transacciones (por fecha, categoria, tipo, rango de monto)
- [ ] 2.2 Paginacion y ordenamiento (Pageable, Page<T>)
- [ ] 2.3 Endpoint de reportes/resumen (totales por categoria, balance mensual)
- [ ] 2.4 Gestion de categorias (entidad propia, CRUD, validacion)
- [ ] 2.5 Transacciones recurrentes (gastos fijos mensuales)
- [ ] 2.6 Exportacion a CSV

### FASE 3: Testing profesional
Objetivo: demostrar que sabe testear (MUY valorado por reclutadores).

- [ ] 3.1 Unit tests para services (JUnit 5 + Mockito)
- [ ] 3.2 Integration tests para repositories (@DataJpaTest)
- [ ] 3.3 Integration tests para controllers (@WebMvcTest)
- [ ] 3.4 Test de seguridad (endpoints protegidos, tokens invalidos)

### FASE 4: Integracion con IA (Claude API)
Objetivo: diferenciarse del resto de candidatos.

- [ ] 4.1 Categorizacion automatica de transacciones con IA
- [ ] 4.2 Analisis inteligente de gastos (patrones, alertas)
- [ ] 4.3 Resumen financiero mensual generado por IA
- [ ] 4.4 Sugerencias de ahorro personalizadas

### FASE 5: AWS (infraestructura cloud)
Objetivo: demostrar que sabe desplegar y usar servicios cloud.

- [ ] 5.1 Dockerizar la aplicacion (Dockerfile + docker-compose)
- [ ] 5.2 RDS para MySQL en produccion
- [ ] 5.3 Despliegue en EC2 o ECS (Fargate)
- [ ] 5.4 S3 para almacenar exports/reportes generados
- [ ] 5.5 Secrets Manager para credenciales
- [ ] 5.6 CloudWatch para logs y monitoreo

### FASE 6: DevOps y CI/CD
Objetivo: flujo de trabajo profesional completo.

- [ ] 6.1 GitHub Actions para CI (build + tests automaticos en cada push)
- [ ] 6.2 CD automatico a AWS
- [ ] 6.3 Perfiles de Spring (dev, staging, prod)
- [ ] 6.4 Documentacion API con Swagger/OpenAPI

## Progreso actual
- Fase actual: FASE 1
- Ultimo paso completado: 1.3 Manejo global de errores (2026-04-03)
- Proximo paso: 1.4 DTOs de respuesta para Transaction (no exponer entidades directamente)
- Estado: 1.3 completado, listo para implementar DTOs de respuesta

## Contexto para retomar sesion
Cuando Estefano vuelva y diga que quiere continuar con finTrack:
1. Recordarle que estamos en FASE 1, paso 1.4 (DTOs de respuesta para Transaction)
2. Ya tiene GlobalExceptionHandler con handlers para ResourceNotFoundException (404), DuplicateResourceException (409) y UnauthorizedException (401)
3. AuthController ya esta limpio: register lanza DuplicateResourceException, login lanza UnauthorizedException, ambos retornan ResponseEntity<AuthResponse>
4. Necesita crear TransactionResponse DTO en com.hari.finTrack.dto para no exponer la entidad Transaction directamente en los endpoints
5. Motivo: la entidad Transaction tiene el campo User completo, que no debe exponerse al cliente (password, etc.)
6. NO escribir el codigo por el, solo explicar y que el lo haga

### Lo que se hizo en 1.1:
- Dependencia spring-boot-starter-validation agregada en pom.xml
- RegisterRequest: @Email @NotBlank en email, @NotBlank en nombre, @NotBlank @Size(min=8) en password
- LoginRequest: @Email @NotBlank en email, @NotBlank en password
- AuthController: @Valid en @RequestBody de register y login
- TransactionController: @Valid solo en @RequestBody (POST y PUT), NO en @AuthenticationPrincipal
- UserPrincipal: limpio, sin anotaciones de validacion (no aplican porque no viene del cliente)

### Lo que se hizo en 1.2:
- Paquete com.hari.finTrack.exception creado
- ResourceNotFoundException (extends RuntimeException) con constructor(String message)
- DuplicateResourceException (extends RuntimeException) con constructor(String message)
- TransactionService: reemplazados 2 IllegalArgumentException por ResourceNotFoundException
- TransactionController: getUser() y 3 catch cambiados a ResourceNotFoundException
- DuplicateResourceException aun no se usa (se usara en 1.3 con @ControllerAdvice)

### Lo que se hizo en 1.3:
- GlobalExceptionHandler creado en com.hari.finTrack.exception con @ControllerAdvice
- Handlers para: ResourceNotFoundException (404), DuplicateResourceException (409), UnauthorizedException (401)
- Cada handler devuelve ResponseEntity<Map<String, String>> con JSON limpio {error, status}
- UnauthorizedException creada (extends RuntimeException)
- AuthController.register: ahora lanza DuplicateResourceException (ya no devuelve ResponseEntity manual)
- AuthController.login: ahora lanza UnauthorizedException con .orElseThrow() (ya no usa .orElse con string)
- Ambos metodos de AuthController retornan ResponseEntity<AuthResponse> (sin wildcard <?>)
- TransactionController: ya estaba limpio desde 1.2, sin try/catch

## Notas
- Actualizar este archivo al completar cada paso
- Cada paso se trabaja con explicacion + el usuario escribe el codigo
