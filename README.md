# Rio Online

Java Swing desktop app for managing Rio Online school links and saved access details.

## Setup

1. Create the MySQL database locally by running `database/schema.sql`.
2. Copy `config/database.properties.example` to `config/database.properties`.
3. Update `config/database.properties` with your local database username and password.
4. Open the project in NetBeans and run it.

`config/database.properties` is ignored by Git, so your real database password should not be committed.

You can also override database settings with system properties or environment variables:

- `db.url` / `DB_URL`
- `db.username` / `DB_USERNAME`
- `db.password` / `DB_PASSWORD`
## Usage

- Use `Previous`, `Current`, and `Next` to move through timetable dates.
- Click the date at the top of the main window to choose a specific date.
- Use row `Copy` buttons, double-click rows, or the saved-content dropdown to copy text to the clipboard.