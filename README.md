# Smart Expense Tracker

## Project Overview

Smart Expense Tracker is a desktop application designed to help users manage personal finances by tracking income, expenses, and budgets. Built as part of a university course project, it provides a user-friendly interface for authenticated users to record financial transactions, view summaries, and monitor recent activity. The application ensures data integrity through robust validation, secure user authentication, and persistent storage in a MySQL database. As of now, the project includes core functionality for user management, transaction tracking, and dashboard summaries, with budget management partially implemented.

## Features

### Implemented Features
- **User Authentication**:
  - Login system with username and password validation.
  - Passwords are masked (displayed as `****`) in the profile view for security.
  - Session management to track logged-in users across application pages.
- **Dashboard**:
  - Displays a welcome message with the logged-in user’s username.
  - Shows financial summaries: total income, total expenses, and current balance.
  - Lists up to three recent transactions (mixed income and expense records) with details like amount, category/source, and date.
- **Income Management**:
  - Add income with amount, source (selected via dropdown from `IncomeSource` enum), and description.
  - View all incomes in a table with columns for ID, amount, source, date, and description.
  - Delete income records with error handling for database issues.
- **Expense Management**:
  - Add expenses with amount, category (selected via dropdown from `ExpenseCategory` enum), and description.
  - View all expenses in a table with columns for ID, amount, category, date, and description.
  - Delete expense records with error handling.
- **Profile Management**:
  - View and update user details (username and password) with validation.
  - Secure password input using a password field.
- **Logging**:
  - Comprehensive logging of user actions (e.g., login, adding transactions) and errors to `logs.log` using a custom logger.
- **UI Design**:
  - Dark-themed interface with consistent styling (Roboto font, `#2B2D42` background, `#206dc5` buttons).
  - Responsive layout with tables, forms, and navigation buttons across all pages.
  - Error messages displayed in the UI for user feedback.

### Pending Features
- **Budget Management**:
  - Add, view, and delete budgets (amount, category, period, description) using a `BudgetPeriod` enum.
  - Full implementation of `AddBudgetController` pending.
- **Edit Functionality**:
  - Edit existing income, expense, and budget records via table buttons.
- **Budget Alerts**:
  - Notifications for expenses exceeding budget limits.
- **Data Visualization**:
  - Charts (e.g., pie chart for expense categories, line chart for trends).
- **User Settings**:
  - Configurable currency (currently fixed to `₹`).
  - Theme toggling (dark/light modes).

## Technologies

- **Java 21**: Core programming language for application logic.
- **JavaFX**: Framework for building the graphical user interface.
- **JDBC**: Database connectivity for MySQL integration.
- **MySQL**: Relational database for persistent storage of users, incomes, expenses, and budgets.
- **Maven**: Build tool for dependency management and project configuration.
- **Custom Logger**: Logs actions and errors to `logs.log` for debugging and auditing.

## Project Structure

```
Smart-Expense-Tracker/
├── src/
│   ├── main/
│   │   ├── java/com/bengregory/expensetracker/
│   │   │   ├── controller/
│   │   │   │   ├── DashboardController.java
│   │   │   │   ├── AddBudgetController.java
│   │   │   │   ├── AddIncomeController.java
│   │   │   │   ├── AddExpenseController.java
│   │   │   │   ├── ProfileController.java
│   │   │   │   ├── LoginController.java
│   │   │   │   ├── RegisterController.java
│   │   │   ├── dao/
│   │   │   │   ├── UserDAO.java
│   │   │   │   ├── IncomeDAO.java
│   │   │   │   ├── ExpenseDAO.java
│   │   │   │   ├── BudgetDAO.java
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── Income.java
│   │   │   │   ├── Expense.java
│   │   │   │   ├── Budget.java
│   │   │   ├── service/
│   │   │   │   ├── UserService.java
│   │   │   │   ├── IncomeService.java
│   │   │   │   ├── ExpenseService.java
│   │   │   │   ├── BudgetService.java
│   │   │   ├── util/
│   │   │   │   ├── CustomLogger.java
│   │   │   │   ├── DatabaseConnection.java
│   │   │   │   ├── SessionManager.java
│   │   │   │   ├── PasswordUtil.java
│   │   │   │   ├── ValidationUtil.java
│   │   │   │   ├── InvalidInputException.java
│   │   │   │   ├── DatabaseException.java
│   │   ├── resources/com/bengregory/expensetracker/view/
│   │   │   ├── dashboard.fxml
│   │   │   ├── add_income.fxml
│   │   │   ├── add_expense.fxml
│   │   │   ├── add_budgets.fxml
│   │   │   ├── profile.fxml
│   │   │   ├── login.fxml
│   │   │   ├── register.fxml
│   │   │   ├── styles.css
│   ├── test/
│   │   ├── java/com/bengregory/expensetracker/
│   │   │   ├── dao/
│   │   │   │   ├── UserDAOTest.java
│   │   │   │   ├── IncomeDAOTest.java
│   │   │   │   ├── ExpenseDAOTest.java
│   │   │   │   ├── BudgetDAOTest.java
│   │   │   ├── util/
│   │   │   │   ├── DatabaseConnectionTest.java
├── pom.xml
├── logs.log
├── README.md
```

## Setup Instructions

1. **Prerequisites**:
   - Java 21 JDK
   - MySQL Server
   - Maven
   - IDE (e.g., IntelliJ IDEA, Eclipse)
   - Scene Builder
   

2. **Clone Repository**:
   ```bash
   git clone https://github.com/BenGJ10/Smart-Expense-Tracker.git
   cd Smart-Expense-Tracker
   ```

3. **Database Setup**:
   - Start MySQL server (e.g., `brew services start mysql` on macOS).
   - Create a database named `expense_tracker_db`.
   - Execute the following SQL to create tables:
     ```sql
     CREATE TABLE users (
         id INT AUTO_INCREMENT PRIMARY KEY,
         username VARCHAR(50) NOT NULL UNIQUE,
         password VARCHAR(255) NOT NULL
     );
     
     CREATE TABLE incomes (
         id INT AUTO_INCREMENT PRIMARY KEY,
         user_id INT,
         amount DOUBLE NOT NULL,
         source VARCHAR(50) NOT NULL,
         date DATETIME NOT NULL,
         description VARCHAR(255),
         FOREIGN KEY (user_id) REFERENCES users(id)
     );
     
     CREATE TABLE expenses (
         id INT AUTO_INCREMENT PRIMARY KEY,
         user_id INT,
         amount DOUBLE NOT NULL,
         category VARCHAR(50) NOT NULL,
         date DATETIME NOT NULL,
         description VARCHAR(255),
         FOREIGN KEY (user_id) REFERENCES users(id)
     );
     
     CREATE TABLE budgets (
         id INT AUTO_INCREMENT PRIMARY KEY,
         user_id INT,
         amount DOUBLE NOT NULL,
         category VARCHAR(50) NOT NULL,
         period VARCHAR(50) NOT NULL,
         description VARCHAR(255),
         FOREIGN KEY (user_id) REFERENCES users(id)
     );
     ```

4. **Make properties file**:
   - Add MySQL credentials (e.g. `jdbc:mysql://localhost:3306/expense_tracker`, username, password) inside `db.properties`(create if not present).

5. **Build and Run**:
   ```bash
   mvn clean install
   mvn javafx:run
   ```

6. **Usage**:
   - Log in with a registered username and password.
   - Navigate to dashboard to view summaries and recent activity.
   - Use “Add Income” or “Add Expense” to record transactions with source/category dropdowns.
   - View or update user details in the profile page.
   - Check `logs.log` for action and error logs.

## Current Status

- Fully functional user authentication, income, expense, and profile management.
- Dashboard with financial summaries and recent activity (mixed income/expense transactions).
- Partial budget management (UI and service in place, controller pending).
- Robust error handling, input validation, and logging.
- Dark-themed, responsive UI with dropdowns for enums (`IncomeSource`, `ExpenseCategory`).

### Next Steps
- Implement edit functionality for income, expense, and budget tables.
- Add budget alerts for overspending.
- Introduce charts for data visualization.
- Enhance profile with user settings (e.g., currency, theme).
- Write comprehensive unit tests and documentation.

## Contributing

This project is developed as part of a university course. Contributions are not open, but feedback is welcome via the repository’s issues section.

## License

This project is for educational purposes and not distributed under a specific license.