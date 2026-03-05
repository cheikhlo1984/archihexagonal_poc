# Delivery Time Slot Management — Carrefour

API REST Spring Boot pour la gestion des créneaux de livraison.
Architecture **hexagonale** (Ports & Adapters) · Java 21 · Spring Boot 3.2 · H2 · JWT

---

## Prérequis

| Outil | Version minimale |
|-------|-----------------|
| Java | 21 |
| Maven | 3.9+ (ou utiliser `./mvnw`) |

> **Java 21 sur Windows** : si plusieurs JDK sont installés, préciser `JAVA_HOME` (voir ci-dessous).

---

## Démarrage rapide

### 1. Compiler

```bash
./mvnw compile
```

### 2. Lancer les tests

```bash
./mvnw test
```

### 3. Démarrer l'application

```bash
./mvnw spring-boot:run
```

L'API est disponible sur **http://localhost:8090**

---

## Avec un JAVA_HOME explicite (Windows)

Si `java` en PATH n'est pas en version 21 :

```bash
# Bash / Git Bash
JAVA_HOME="$HOME/.jdks/temurin-21.0.10" ./mvnw spring-boot:run

# PowerShell
$env:JAVA_HOME="$env:USERPROFILE\.jdks\temurin-21.0.10"; .\mvnw.cmd spring-boot:run
```

---

## Console H2 (base de données embarquée)

Accessible pendant l'exécution à l'adresse :
**http://localhost:8090/h2-console**

| Paramètre | Valeur |
|-----------|--------|
| JDBC URL | `jdbc:h2:mem:deliverydb` |
| Username | `sa` |
| Password | *(vide)* |

---

## Utilisation de l'API

### Authentification

#### Créer un compte

```bash
curl -s -X POST http://localhost:8090/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@example.com","password":"secret123"}' | jq .
```

Réponse :
```json
{
  "token": "<JWT>",
  "username": "alice"
}
```

#### Se connecter

```bash
curl -s -X POST http://localhost:8090/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"secret123"}' | jq .
```

> Stocker le token JWT pour les appels suivants :
> ```bash
> TOKEN="<valeur du champ token>"
> ```

---

### Modes de livraison (public)

```bash
curl -s http://localhost:8090/api/v1/delivery-modes | jq .
```

| Mode | Durée | Horaires | Fenêtre | Capacité |
|------|-------|----------|---------|----------|
| `DRIVE` | 30 min | 08h–20h | Aujourd'hui + 7 j | 10 |
| `DELIVERY` | 2h | 08h–22h | Aujourd'hui + 7 j | 5 |
| `DELIVERY_TODAY` | 2h | 10h–22h | Aujourd'hui seul | 3 |
| `DELIVERY_ASAP` | 1h | 08h–22h | 3 prochains créneaux | 2 |

---

### Créneaux disponibles (JWT requis)

```bash
# DRIVE – aujourd'hui (24 créneaux)
curl -s "http://localhost:8090/api/v1/slots?mode=DRIVE&date=$(date +%Y-%m-%d)" \
  -H "Authorization: Bearer $TOKEN" | jq .

# DELIVERY – dans 3 jours
curl -s "http://localhost:8090/api/v1/slots?mode=DELIVERY&date=$(date -d '+3 days' +%Y-%m-%d)" \
  -H "Authorization: Bearer $TOKEN" | jq .

# DELIVERY_ASAP – date ignorée, renvoie les 3 prochains créneaux
curl -s "http://localhost:8090/api/v1/slots?mode=DELIVERY_ASAP" \
  -H "Authorization: Bearer $TOKEN" | jq .

# Détail d'un créneau
curl -s http://localhost:8090/api/v1/slots/1 \
  -H "Authorization: Bearer $TOKEN" | jq .
```

---

### Réservations (JWT requis)

#### Créer une réservation

```bash
curl -s -X POST http://localhost:8090/api/v1/reservations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"slotId": 1}' | jq .
```

Réponse :
```json
{
  "id": 1,
  "timeSlotId": 1,
  "username": "alice",
  "createdAt": "2026-02-25T10:30:00",
  "status": "CONFIRMED",
  "_links": {
    "self":   { "href": "http://localhost:8090/api/v1/reservations/1" },
    "cancel": { "href": "http://localhost:8090/api/v1/reservations/1" },
    "slot":   { "href": "http://localhost:8090/api/v1/slots/1" }
  }
}
```

#### Lister mes réservations

```bash
curl -s http://localhost:8090/api/v1/reservations \
  -H "Authorization: Bearer $TOKEN" | jq .
```

#### Détail d'une réservation

```bash
curl -s http://localhost:8090/api/v1/reservations/1 \
  -H "Authorization: Bearer $TOKEN" | jq .
```

#### Annuler une réservation

```bash
curl -s -X DELETE http://localhost:8090/api/v1/reservations/1 \
  -H "Authorization: Bearer $TOKEN"
# → HTTP 204 No Content
```

---

## Gestion des erreurs (RFC 7807)

Toutes les erreurs suivent le format `ProblemDetail` :

```json
{
  "type":   "https://delivery.carrefour.com/errors/slot-not-available",
  "title":  "Conflict",
  "status": 409,
  "detail": "Time slot 5 has no available capacity"
}
```

| Code HTTP | Situation |
|-----------|-----------|
| `400` | Paramètre invalide, date hors fenêtre |
| `401` | Token absent ou invalide |
| `403` | Tentative d'annuler la réservation d'un autre utilisateur |
| `404` | Créneau / réservation / utilisateur introuvable |
| `409` | Créneau complet, utilisateur déjà existant, réservation déjà annulée |

---

## Architecture hexagonale

```
com.carrefour.delivery/
│
├── domain/                     ← Cœur métier (0 dépendance framework)
│   ├── model/                  DeliveryMode, TimeSlot, Reservation, User …
│   ├── port/
│   │   ├── in/                 Use cases (interfaces driving ports)
│   │   └── out/                Repos & Token (interfaces driven ports)
│   ├── service/                Implémentations métier
│   └── exception/              Exceptions domaine
│
└── infrastructure/             ← Adaptateurs Spring/JPA/Security
    ├── persistence/            Entités JPA · Spring Data · Mappers · Adapters
    ├── web/                    Contrôleurs REST HATEOAS · DTOs records
    └── security/               JWT · Filtre · SecurityConfig
```

### Principes appliqués

- **Domaine isolé** : aucune annotation Spring dans `domain/`
- **Génération lazy** : les créneaux sont créés en base au premier appel par mode + date
- **Locking pessimiste** : `PESSIMISTIC_WRITE` sur `findByIdWithLock` — garantit l'absence de double réservation
- **TokenPort** : le domaine génère les tokens sans connaître JWT (implémenté dans l'infra)
- **HATEOAS** : chaque réponse inclut des liens `_links` vers les ressources liées

---

## Tests

```bash
# Tous les tests (39)
./mvnw test

# Un seul test unitaire
./mvnw test -Dtest=TimeSlotDomainServiceTest

# Une seule méthode
./mvnw test -Dtest=TimeSlotDomainServiceTest#drive_generatesCorrectNumberOfSlots
```

| Suite | Tests | Type |
|-------|-------|------|
| `ReservationDomainServiceTest` | 8 | Unitaire (Mockito) |
| `TimeSlotDomainServiceTest` | 10 | Unitaire (Mockito) |
| `JwtServiceTest` | 4 | Unitaire |
| `SecurityIntegrationTest` | 4 | Intégration Spring Boot |
| `IntegrationTest` | 13 | Intégration end-to-end |

---

## Configuration

Fichier : `src/main/resources/application.yml`

```yaml
server:
  port: 8090          # Port HTTP

app:
  jwt:
    secret: ...       # Clé HMAC-SHA256 (Base64, 256 bits minimum)
    expiration-ms: 86400000  # Durée du token : 24h
```
