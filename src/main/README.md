# GitHub Repository Proxy

Usługa REST pobierająca szczegóły repozytoriów z GitHub API
oraz zarządzająca ich lokalnymi kopiami w bazie danych.

## Stack

- Java 21, Spring Boot 4.1.1
- Spring Cloud OpenFeign — klient GitHub API
- Spring Data JPA + H2 (in-memory)
- MapStruct — mapowanie DTO ↔ encja
- JUnit 5, Mockito

## Uruchomienie

```bash
mvn spring-boot:run
```

Aplikacja startuje na `localhost:8080`.
Konsola H2: `localhost:8080/h2-console`

## Endpointy

| Metoda | Ścieżka | Źródło danych | Opis |
|---|---|---|---|
| GET | `/repositories/{owner}/{repository-name}` | GitHub API | szczegóły repozytorium |
| POST | `/repositories/{owner}/{repository-name}` | GitHub API → baza | zapis lokalnej kopii |
| GET | `/local/repositories/{owner}/{repository-name}` | baza | odczyt lokalnej kopii |
| PUT | `/repositories/{owner}/{repository-name}` | baza | aktualizacja lokalnej kopii |

### GET /repositories/{owner}/{repository-name}

Pobiera dane bezpośrednio z GitHub API przez klienta Feign. Nic nie zapisuje.

**200 OK**
```json
{
  "fullName": "SamuelDawid/medical-clinic",
  "description": "...",
  "cloneUrl": "https://github.com/SamuelDawid/medical-clinic.git",
  "stars": 0,
  "createdAt": "2026-07-13T12:04:30Z"
}
```

**404 Not Found** — repozytorium nie istnieje na GitHubie
**502 Bad Gateway** — GitHub zwrócił nieoczekiwany błąd

### POST /repositories/{owner}/{repository-name}

Pobiera repozytorium z GitHuba i zapisuje jego kopię w lokalnej bazie.
Żądanie nie zawiera ciała — dane pochodzą z GitHuba, nie od klienta.

**201 Created** — zapisano
**404 Not Found** — repozytorium nie istnieje na GitHubie
**409 Conflict** — kopia lokalna już istnieje

### GET /local/repositories/{owner}/{repository-name}

Odczyt z lokalnej bazy, bez odpytywania GitHuba.

**200 OK**
**404 Not Found** — brak lokalnej kopii

### PUT /repositories/{owner}/{repository-name}
Aktualizuje lokalną kopię danymi z ciała żądania. Update jest **częściowy** —
pola pominięte w body (null) zachowują dotychczasowe wartości.
Rozważane alternatywy:
- **Pełne zastąpienie zasobu** — zgodne ze ścisłą semantyką PUT, gdzie pominięte
  pole oznacza wyzerowanie wartości. Odrzucone, bo wymuszałoby przesyłanie
  kompletu pól przy zmianie jednego z nich. Formalnie moje rozwiązanie
  odpowiada semantyce PATCH, ale ścieżkę i metodę narzuca specyfikacja zadania.
- **Odświeżenie danych z GitHuba** — odrzucone, bo PUT dublowałby wtedy POST,
  a klient nie miałby wpływu na treść aktualizacji.

**200 OK**
**404 Not Found** — brak lokalnej kopii

## Obsługa błędów

Błędy z GitHub API są tłumaczone przez `ErrorDecoder` na wyjątki domenowe
dziedziczące po `GitHubClientException`, a następnie mapowane
na odpowiedzi HTTP w `@RestControllerAdvice`.

```json
{
  "status": 404,
  "message": "Repository not found",
  "timestamp": "2026-09-03T10:15:30Z"
}
```