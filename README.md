# Guess The World

A lightweight, no-nonsense Wordle-style desktop game built with Java 21, JavaFX, and an embedded SQLite database.

Think fast, pick your 5-letter words wisely, and try to beat the daily limit without exhausting your 5 attempts.

---

## Quick Start

Make sure you have Java 21 and Maven installed.

Run the application:
```bash
mvn javafx:run
```

Run test suite:
```bash
mvn test
```

---

## Default Accounts

Pre-seeded credentials to get started right away:

| Role | Username | Password | Notes |
|---|---|---|---|
| Admin | `AdminUser` | `AdminPassword1$` | Full access to daily reports, player stats, and word pool |
| Player | `PlayerOne` | `PlayerPassword1%` | Ready to play up to 3 words per day |

*New registrations are created as Player accounts.*

---

## Game Rules

- **Daily Quota**: 3 words per player per day. Pace yourself.
- **Attempts**: 5 guesses per word.
- **Feedback**:
  - **Green**: Right letter, right spot.
  - **Yellow / Orange**: Right letter, wrong spot.
  - **Grey**: Not in the word at all.

---

## Architecture

- **UI**: JavaFX (clean light theme, zero clutter)
- **Persistence**: SQLite via JDBC (auto-initialized with 20 starting words)
- **Security**: Salted SHA-256 password hashing
- **Testing**: JUnit 5 test suites covering auth, validation, scoring rules, and report aggregations
