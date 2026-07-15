# MOMO COGNITIVE TEST

## Prerequisites
- Java 8 or higher
- A Java IDE (e.g., IntelliJ IDEA, Eclipse) or a text editor (e.g., Visual Studio Code)
- Java JDK 24 or higher
- Maven 3.9.9 or higher

## Problem statement
As a payment service company, we want to build a software solution that provides
customers with bill payment service.
1. Each customer is able to add fund into his account.
2. He is able to create, delete, update, view and search for a bill of particular service.
3. He is able to pay a valid bill using his available fund.
4. He is able to pay multiple bills of different service providers any time using his
   available fund. Payment would be prioritized for bill with early due dates. 5. He is also
   able to keep track of his bill due dates so that he is able to pay his bills in time.
6. He has an ability to pay multiple and/or different bills at the same time. 7. He
   desires a possibility of scheduled bill payment so that the software solution will
   automatically do bill payment with a schedule that he has configured.
8. He often checks payment transaction history to ensure that there is nothing wrong
   with his fund as well.

### Example
The following is sample of expected output when running your solution:
\$ path/to/your_solution_programm CASH_IN 1000000
Your available balance: 1000000
\$ path/to/your_solution_programm LIST_BILL
Bill No. Type Amount Due Date State PROVIDER 1. ELECTRIC 200000
25/10/2020 NOT_PAID EVN HCMC 2. WATER 175000 30/10/2020 NOT_PAID
SAVACO HCMC 3. INTERNET 800000 30/11/2020 NOT_PAID VNPT

\$ path/to/your_solution_programm PAY 1
Payment has been completed for Bill with id 1.
Your current balance is: 800000
\$ path/to/your_solution_programm PAY 10
Not found a bill with such id
\$ path/to/your_solution_programm PAY 2 3
Not enough fund to proceed with payment.
\$ path/to/your_solution_programm DUE_DATE
Bill No. Type Amount Due Date State PROVIDER 2. WATER 175000
30/10/2020 NOT_PAID SAVACO HCMC 3. INTERNET 800000 30/11/2020
NOT_PAID VNPT

\$ path/to/your_solution_programm SCHEDULE 2 28/10/2020
Payment for bill id 2 is scheduled on 28/10/2020

$ path/to/your_solution_programm LIST_PAYMENT
No. Amount Payment Date State Bill Id
1. 200000 25/10/2020 PROCESSED 1
2. 175000 30/10/2020 PENDING 2
3. 800000 30/11/2020 PENDING 3
   $ path/to/your_solution_programm SEARCH_BILL_BY_PROVIDER VNPT
   Bill No. Type Amount Due Date State PROVIDER 3. INTERNET 800000
   30/11/2020 NOT_PAID VNPT

\$ path/to/your_solution_programm EXIT

## Behavioral Assumptions

- Each user has only one payment account.
- The system supports domestic bill payments in VND currency and only supports a predefined list of providers that are officially partnered with the platform.
- The system allows top-ups up to the account’s permitted limit, for example: a maximum limit of 100,000,000 VND per month.
- Bills from certain services are automatically linked to user accounts via phone numbers. Users can directly view their bills after logging into the system.
- The system supports bill searching by provider (code, name), and by bill identifier or phone number associated with that provider. Searches are case-insensitive.
- Users can view bill details.

## Entity Assumptions

- **User**: `id`, `fullname`, `phone`, `email`, `dob`, `paymentAccount`
- **PaymentAccount**: `id`, `balance`, `status`
- **Bill**: `id`, `type`, `amount`, `dueDate`, `createdDate`, `provider`
- **Provider**: `id`, `code`, `name`, `serviceType` (`INTERNET`, `WATER`, `ELECTRIC`)
- **Payment**: `id`, `amount`, `paymentDate`, `stage` (`PROCESSED`, `SCHEDULED`), `processedAt`

## Available Commands

### CASH_IN
```
CASH_IN <amount>
```
Top up balance to your account. Amount is in VND currency. Subject to monthly limit (100,000,000 VND max).

### LIST_BILL
```
LIST_BILL
```
List all bills linked to your account (via phone number).

### CREATE_BILL
```
CREATE_BILL <type> <amount> <dueDate> <providerCode> <providerName>
```
Create a new bill. 
- `type`: ELECTRIC, WATER, INTERNET, or OTHER
- `amount`: bill amount in VND
- `dueDate`: date in format dd/MM/yyyy
- `providerCode`: short code (e.g., EVN, SAVACO, VNPT)
- `providerName`: full provider name (e.g., EVN HCMC)

Example: `CREATE_BILL ELECTRIC 150000 15/06/2026 EVN EVN HCMC`

### DELETE_BILL
```
DELETE_BILL <billId>
```
Delete a bill by its ID.

### SEARCH_BILL_BY_PROVIDER
```
SEARCH_BILL_BY_PROVIDER <providerCode|providerName>
```
Search bills by provider code or name (case-insensitive).

### SEARCH_BILL_BY_ID
```
SEARCH_BILL_BY_ID <billId>
```
Search for a specific bill by its ID.

### SEARCH_BILL_BY_PHONE
```
SEARCH_BILL_BY_PHONE <phone>
```
List bills linked to a phone number.

### DUE_DATE
```
DUE_DATE
```
List all bills sorted by due date (earliest first).

### PAY
```
PAY <billId1> [billId2] [billId3] ...
```
Pay one or more bills. Payment is processed in order of earliest due date first. Requires sufficient balance.

### SCHEDULE
```
SCHEDULE <billId> <scheduledDate>
```
Schedule a bill payment for a future date (format: dd/MM/yyyy). Payment will be automatically processed on that date if balance is available.

### LIST_PAYMENT
```
LIST_PAYMENT
```
View payment history and scheduled payments.

### EXIT
```
EXIT
```
Exit the application. Prints "Goodbye!".

## Testing

Quick steps to run unit tests and exercise the CLI locally, with a step-by-step CLI scenario you can follow interactively.

### Option 1: Using Maven Wrapper (Recommended - no global Maven needed)

1) Build the project and run tests

```bash
./mvnw clean test
./mvnw package
```

On Windows:
```bash
mvnw.cmd clean test
mvnw.cmd package
```

2) Start the interactive CLI

```bash
java -cp target/classes com.billpayment.Main
```

### Option 2: Using Global Maven (requires Maven 3.9.9+ installed)

1) Build the project and run tests

```bash
mvn clean test
mvn package
```

2) Start the interactive CLI

```bash
java -cp target/classes com.billpayment.Main
```

3) Step-by-step commands to run inside the CLI (type each command and press Enter)

- Top up balance

```
CASH_IN 1000000
```
Expected: prints available balance (e.g. "Your available balance: 1000000").

- List seeded bills

```
LIST_BILL
```
Expected: shows seeded bills in labeled format, for example:

```
id: 1 | type: ELECTRIC | amount: 200000 | due: 25/10/2026 | status: NOT_PAID | provider: EVN (EVN HCMC)
id: 2 | type: WATER    | amount: 175000 | due: 30/10/2026 | status: NOT_PAID | provider: SAVACO (SAVACO HCMC)
id: 3 | type: INTERNET | amount: 800000 | due: 30/11/2026 | status: NOT_PAID | provider: VNPT (VNPT)
```

- Pay a single bill

```
PAY 1
```
Expected: confirmation ("Payment has been completed for Bill with id 1.") and updated balance.

- Inspect payment history

```
LIST_PAYMENT
```
Expected: contains payment records in labeled format, for example:

```
id: 12345678 | billId: 1 | amount: 200000 | date: 25/10/2026 | state: PROCESSED
```

- Try paying a non-existent bill

```
PAY 10
```
Expected: prints a "not found" message.

- Attempt multi-pay which may fail if balance insufficient

```
PAY 2 3
```
Expected: if balance is insufficient, prints an insufficient-fund message and no bill states are mutated.

- Schedule a payment

```
SCHEDULE 2 28/10/2026
```
Expected: prints scheduling confirmation and `LIST_PAYMENT` shows a SCHEDULED entry.

- Search bills by provider

```
SEARCH_BILL_BY_PROVIDER VNPT
```
Expected: lists VNPT bills (case-insensitive search).

- Search bills by bill ID

```
SEARCH_BILL_BY_ID 2
```
Expected: displays the specific bill with id 2.

- View bills sorted by due date

```
DUE_DATE
```
Expected: displays all bills sorted by due date (earliest first).

- Create a new bill

```
CREATE_BILL ELECTRIC 150000 15/06/2026 EVN HCMC
```
Expected: bill is created and added to the system, a new bill ID is assigned and printed.

- List bills again to see newly created bill

```
LIST_BILL
```
Expected: includes the newly created bill in the list.

- Delete a bill

```
DELETE_BILL 4
```
Expected: confirmation that bill 4 has been deleted (assuming bill ID 4 was the newly created one).

- Verify bill is deleted

```
SEARCH_BILL_BY_ID 4
```
Expected: "not found" message since the bill was deleted.

- Exit

```
EXIT
```

4) Non-interactive scripted run (example)

Using Maven Wrapper:
```bash
printf "CASH_IN 1000000\nLIST_BILL\nPAY 1\nLIST_PAYMENT\nPAY 10\nPAY 2 3\nSCHEDULE 2 28/10/2026\nLIST_PAYMENT\nSEARCH_BILL_BY_PROVIDER VNPT\nEXIT\n" | ./mvnw clean package -q && java -cp target/classes com.billpayment.Main
```

Using global Maven:
```bash
printf "CASH_IN 1000000\nLIST_BILL\nPAY 1\nLIST_PAYMENT\nPAY 10\nPAY 2 3\nSCHEDULE 2 28/10/2026\nLIST_PAYMENT\nSEARCH_BILL_BY_PROVIDER VNPT\nEXIT\n" | mvn clean package -q && java -cp target/classes com.billpayment.Main
```

Notes:
- The CLI uses a seeded default phone `0901000001` when started from `Main`.
- CLI date format is `dd/MM/yyyy` (e.g., `28/10/2026`).

## Coverage Report

Test coverage report is available in the `htmlReport` folder. Open `index.html` in a browser to view detailed coverage statistics.

| Metric | Coverage      |
|--------|---------------|
| Class | 100% (1/1)    |
| Method | 100% (20/20)  |
| Branch | 87.5% (21/24) |
| Line | 100% (70/70)  |

Total test cases: 29 tests
