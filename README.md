# atajo · acortador de enlaces

**atajo** (un camino más corto) es un acortador de enlaces hecho con Spring Boot 3 (Java 21) con **arquitectura hexagonal**, **MongoDB** como persistencia, **Redis** como caché
y frontend en **React** (Vite). El patrón **Abstract Factory** crea la familia de adaptadores de salida.

## Cómo ejecutar

Requisitos: Java 21, Maven, Node 18+ y Docker Desktop.

```bash
# 1. MongoDB (puerto 27018) y Redis (puerto 6379)
docker compose up -d

# 2. Backend → http://localhost:8080
cd backend
mvn spring-boot:run

# 3. Frontend → http://localhost:5173
cd frontend
npm install
npm run dev
```

Todo dentro de Docker (sin Java/Node locales): `docker compose --profile app up -d --build`.

Sin Docker (sin MongoDB ni Redis): `mvn spring-boot:run -Dspring-boot.run.profiles=memoria`.
Así se usa la fábrica en memoria; los datos se pierden al reiniciar.

> El MongoDB de Docker se publica en el **27018** porque en este equipo hay un servicio MongoDB de Windows
> ocupando el 27017. Para usar ese MongoDB local: `MONGODB_URI=mongodb://localhost:27017/acortador`.

Pruebas: `cd backend && mvn test` (no necesitan Docker: usan la fábrica en memoria).

## Funcionalidad

| Módulo | Qué hace |
|---|---|
| **Acortar enlace** | Caja de texto y botón **Acortar**. Acepta cualquier enlace (http, https, ftp…; si no trae esquema se le agrega `https://`). Genera un código Base62 aleatorio de **8 caracteres** (el dominio permite de 8 a 10), lo guarda en MongoDB y muestra el enlace acortado con los botones **Copiar** e **Ir**. |
| **Ver enlaces** | Historial con los datos de MongoDB: `#`, `id` de Mongo, enlace original y enlace acortado. El enlace acortado es un link que redirige; al lado tiene **Copiar** (el ícono se transforma en un check animado) e **Ir** (abre la redirección en otra pestaña). |
| **Indicador de caché** | **1.ª consulta**: los datos vienen de MongoDB y quedan en Redis por 30 s. **2.ª consulta** (dentro de esos 30 s): vienen de Redis y se muestra el contador de segundos decreciendo, que toma el TTL real de Redis (`PTTL`). Al llegar a 0 se indica que la caché expiró. Cada clic en «Ver enlaces» o en «Consultar de nuevo» cuenta como una consulta. |
| **Redirección** | `GET http://localhost:8080/{codigo}` responde **302** hacia el enlace original. El destino se cachea en Redis 10 min. |

Al acortar un enlace nuevo **se invalida la caché del historial**, para que «Ver enlaces» nunca muestre una lista desactualizada.
La consulta siguiente va a MongoDB y la que viene después ya sale de Redis con el contador.

### API

| Método | Ruta | Respuesta |
|---|---|---|
| `POST` | `/api/enlaces` con `{"enlaceOriginal": "..."}` | `201` `{id, enlaceOriginal, codigo, enlaceAcortado, fechaCreacion}` · `400` si el enlace no es válido |
| `GET` | `/api/enlaces` | `{enlaces[], fuente: "MONGODB"\|"REDIS", desdeCache, ttlRestanteMs, ttlCacheMs}` |
| `GET` | `/{codigo}` | `302 Location: <enlace original>` · `404` si no existe |

Documento en MongoDB (colección `enlaces`, índice único en `enlaceAcortado`):

```json
{ "_id": ObjectId("..."), "enlaceOriginal": "https://www.youtube.com/?gl=CO&hl=es-419", "enlaceAcortado": "0440E65L", "fechaCreacion": ISODate("...") }
```

## Arquitectura hexagonal

```
com.example.acortador
├── app/App                                   ← arranque de Spring
├── domain/                                   ← núcleo: no conoce Spring, Mongo, Redis ni HTTP
│   ├── model/        Enlace, CodigoCorto, EnlaceOriginal
│   └── exception/    EnlaceInvalidoException, EnlaceNoEncontradoException
├── application/
│   ├── ports/in/     AcortarEnlaceUseCase, ConsultarEnlacesUseCase, ResolverEnlaceUseCase   (puertos de entrada)
│   ├── ports/out/    EnlaceRepository, EnlaceCache                                         (puertos de salida)
│   │                 InfraestructuraFactory  ← ABSTRACT FACTORY
│   └── service/      EnlaceService           ← implementa los casos de uso; cliente de la fábrica
└── infrastructure/
    ├── web/controllers/   EnlaceController, RedireccionController, ManejadorErrores   (adaptadores de entrada)
    ├── persistence/mongo/ EnlaceRepositoryMongo (MongoTemplate), EnlaceDocument
    ├── cache/redis/       EnlaceCacheRedis (StringRedisTemplate)
    ├── persistence/memoria/, cache/memoria/   adaptadores en memoria
    ├── factory/           MongoRedisInfraestructuraFactory, MemoriaInfraestructuraFactory
    └── config/            InfraestructuraConfig (elige la fábrica al arrancar), AppProperties
```

Las dependencias siempre apuntan hacia adentro: `infrastructure → application → domain`.
`EnlaceService` no tiene anotaciones de Spring; se registra como bean en `InfraestructuraConfig`.

### Flujo de una petición (equivalente al esquema de referencia)

```
Ida:     Cliente ──GET /api/enlaces──▶ EnlaceController ──▶ EnlaceService
                                                             │ 1) EnlaceCache.obtenerListado()   → EnlaceCacheRedis  → Redis
                                                             │ 2) si no hay caché:
                                                             │    EnlaceRepository.listar()      → EnlaceRepositoryMongo → MongoDB
                                                             │    EnlaceCache.guardarListado()   → Redis (TTL 30 s)
Vuelta:  Cliente ◀──JSON (fuente, ttlRestanteMs)── EnlaceController ◀── ConsultaEnlaces ◀──┘
```

**Arranque (una sola vez, no en cada petición):** `InfraestructuraConfig` crea la fábrica concreta según el perfil
activo y se la pasa a `EnlaceService`, que le pide sus dos productos (repositorio y caché).

## Patrón Abstract Factory

**Intención (GoF):** proveer una interfaz para crear *familias de objetos relacionados* sin especificar sus clases concretas.

En este proyecto la familia son los **adaptadores de salida** que el núcleo necesita, y que deben ser compatibles
entre sí: un repositorio permanente y una caché temporal.

| Rol del patrón | Clase |
|---|---|
| **AbstractFactory** | `application/ports/out/InfraestructuraFactory` → `crearRepositorio()`, `crearCache()` |
| **AbstractProduct** | `EnlaceRepository`, `EnlaceCache` (puertos de salida) |
| **ConcreteFactory 1** | `MongoRedisInfraestructuraFactory` → familia real |
| **ConcreteProduct 1** | `EnlaceRepositoryMongo`, `EnlaceCacheRedis` |
| **ConcreteFactory 2** | `MemoriaInfraestructuraFactory` → familia en memoria |
| **ConcreteProduct 2** | `EnlaceRepositoryMemoria`, `EnlaceCacheMemoria` |
| **Client** | `EnlaceService`: solo conoce las interfaces |

```
               «interface» InfraestructuraFactory
               + crearRepositorio(): EnlaceRepository
               + crearCache(): EnlaceCache
                      ▲                     ▲
     MongoRedisInfraestructuraFactory   MemoriaInfraestructuraFactory
        │ crea            │ crea           │ crea                │ crea
 EnlaceRepositoryMongo  EnlaceCacheRedis  EnlaceRepositoryMemoria  EnlaceCacheMemoria
        ╰── implementan EnlaceRepository / EnlaceCache (puertos de salida) ──╯

 EnlaceService(InfraestructuraFactory) ── usa ──▶ EnlaceRepository, EnlaceCache
```

**¿Por qué en los puertos?** En hexagonal, los puertos de salida son justamente los "productos abstractos": contratos que
el núcleo define y que la infraestructura implementa. Poner la fábrica abstracta junto a ellos (en `application/ports/out`)
mantiene la regla de dependencias: el núcleo declara *qué* necesita y la infraestructura decide *con qué* se construye.

**¿Qué se gana?**
- **Consistencia de la familia:** nunca se mezcla, por ejemplo, un repositorio en memoria con una caché Redis que guarde
  datos de otra base. Cada fábrica entrega piezas que funcionan juntas.
- **Cambiar toda la infraestructura en un solo punto:** el perfil de Spring (`memoria` o el por defecto) elige la fábrica
  y el núcleo no se toca. Agregar, por ejemplo, PostgreSQL + Memcached sería crear otra fábrica concreta.
- **Pruebas sin Docker:** `EnlaceServiceTest` y `AcortadorApiTest` usan `MemoriaInfraestructuraFactory`.

**Relación con Spring:** Spring hace la inyección de dependencias (crea la fábrica y el servicio), y la fábrica encapsula
cómo se arma la familia de adaptadores. Los dos se complementan: la configuración solo elige *qué* fábrica usar.

## Decisiones de diseño

- **Redis no rompe la app:** si Redis cae, `EnlaceCacheRedis` registra una advertencia y responde como "no hay caché",
  y la app sigue funcionando contra MongoDB.
- **TTL real:** el contador del frontend parte de `ttlRestanteMs`, leído de Redis con `PTTL`, no de un valor fijo.
- **Formato propio en la caché:** Redis guarda un JSON propio (`EnlaceCacheado`), no la entidad de dominio.
- **Consultas sin duplicados:** el frontend consulta desde el clic, no desde un `useEffect`. Así el doble montaje de
  `StrictMode` no genera una segunda consulta que activaría la caché en la primera visita.
- **Código corto:** `SecureRandom` + Base62, con verificación de que no exista y un índice único en MongoDB como respaldo.
