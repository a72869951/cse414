# CSE 414 Homework 8: Database Application and Transaction Management

**Objectives:**
To gain experience with database application development and transaction management.
To learn how to use SQL from within Java via JDBC.

**Assignment tools:**
[SQL Server](http://www.microsoft.com/sqlserver) through [Azure SQL](https://azure.microsoft.com/en-us/services/sql-database/) 
and starter code files.

**Additional files (you normally don't need these):**
[SQL Server JDBC Jar files](http://www.microsoft.com/downloads/info.aspx?na=41&srcfamilyid=a737000d-68d0-4531-b65d-da0f2a735707&srcdisplaylang=en&u=http://download.microsoft.com/download/D/6/A/D6A241AC-433E-4CD2-A1CE-50177E8428F0/1033/sqljdbc_3.0.1301.101_enu.tar.gz) 
(the JDBC driver for older versions of Java)

**Assigned date:** May 31st, 2019

**Due date:** June 9th, 2019, at 11:59pm. Turn in your solution using `git`. You have approximately 1.5 weeks for this assignment.

***Warning: This can potentially be a long assignment. There are NO LATE DAYS***

**What to turn in:**

Customer database schema in `createTables.sql`, your completed version of the `Query.java`, 
your previous solution to `QuerySearchOnly.java` (you are allowed to change this code for this homework),
and the test cases that you created with a descriptive name for each case. 

You do not need to turn in your `dbconn.properties` file or any other starter files. 
We will be testing your implementations using the home VM. So make sure your submission works there before turning it in.


## Assignment Details

**Read this whole document before starting this project.** There is a lot of valuable information here, including the Final Comments section at the bottom.

Congratulations, you are opening your own flight booking service, now with user management and booking capabilities!
You will use your solution to the previous homework in `QuerySearchOnly.java` to implement the remaining commands in `Query.java`.
In particular, you will need to add the ability for a user to register and log in, in order for that user to make reservations.

**Note** that the class `Query` defined in `Query.java` extends the `QuerySearchOnly` class which means it will inherit it's methods and variables. This means that all you have to do to integrate your solution from homework 7 is to copy your version of `QuerySearchOnly.java` into the folder containing `Query.java`.

To implement the remaining commands, you may find it useful to design additional database tables.  Please write the code to create these tables in `createTables.sql`, along with the code to clear the contents of these tables (to facilitate grading; don't drop the tables!) in the `clearTables()` method of `Query.java`. This means that your `clearTables()` method should execute some `PreparedStatement` queries that delete all of the data from your created tables (i.e. not `Flights`).

One challenge is that the additional commands may be invoked by multiple users concurrently. This is not a problem with the `search` command alone, because `search` is a read-only command, but it is important in the presence of booking commands that may conflict.
To resolve this challenge, you will need to implement transactions that ensure concurrent commands do not conflict.

As with the previous homework, you may use any classes from the [Java 8 standard JDK](https://docs.oracle.com/javase/8/docs/api/) as that is the supported platform on the home VM. If you like to use any external libraries beyond the JDK and those provided inside the `lib` folder, please check with the staff first.

#### Data Model

Now that we are introducing users and bookings, the data model for this service will be a bit more complicated.

The flight service system consists of the following logical entities.
These entities are *not necessarily database tables*. 
It is up to you to decide what entities to store persistently and create a physical schema design that has the ability to run the operations below, which make use of these entities.

- **Flights / Carriers / Months / Weekdays**: modeled the same way as HW3. We already provide the SQL declaration for Flights commented out in `createTables.sql` that you should use. You might not need to use the other tables but that is entirely up to you.

- **Users**: A user has a username (`varchar`), password (`varchar`), and balance in their account 
(`int`). All usernames should be unique in the system. Each user can have any number of reservations. You may store passwords in plain text (please don't do this in the real world). Usernames and passwords should be case insensitive (this is the default for SQL Server).
You can assume that all usernames and passwords have at most 20 characters.

- **Itineraries**: An itinerary is either a direct flight (consisting of one flight: origin --> destination) or a one-hop flight (consiting of two flights: origin --> stopover city, stopover city --> destination). Itineraries are returned by the search command.

- **Reservations**: A booking for an itinerary, which may consist of one (direct) or two (one-hop) flights.
Each reservation can either be paid or unpaid and has a unique ID.

For this assignment we will assume the following table exists to store the flights from HW3 (it has the same design as HW3 as well): 

```
FLIGHTS (fid int, 
         month_id int,        -- 1-12
         day_of_month int,    -- 1-31 
         day_of_week_id int,  -- 1-7, 1 = Monday, 2 = Tuesday, etc
         carrier_id varchar(7), 
         flight_num int,
         origin_city varchar(34), 
         origin_state varchar(47), 
         dest_city varchar(34), 
         dest_state varchar(46), 
         departure_delay int, -- in mins
         taxi_out int,        -- in mins
         arrival_delay int,   -- in mins
         canceled int,        -- 1 means canceled
         actual_time int,     -- in mins
         distance int,        -- in miles
         capacity int, 
         price int            -- in $             
         )
```

Create other tables or indexes you need for this assignment in `createTables.sql` (see below).


#### Requirements
The following are the functional specifications for the flight service system, to be implemented in `Query.java` (see code for full specification as to what error message to return, etc):

- **create** takes in a new username, password, and initial account balance as input. It creates a new user account with the initial balance. It should return an error if negative, or if the username already exists. Usernames and passwords are checked case-insensitively. You can assume that all usernames and passwords have at most 20 characters.

- **login** takes in a username and password, and checks that the user exists in the database and that the password matches. 

- **search** same as previous homework. You are allowed to update or improve your implementation in this homework. We will attempt to grade your   homework 7 solution as quickly as possible so you will be liable for making fixes to your search method if it did not fully comply with our criteria

- **book** lets a user book an itinerary by providing the itinerary number as returned by a previous search. 
  The user must be logged in to book an itinerary, and must enter a valid itinerary id that was returned in the last search that was performed *within the same login session*. Make sure you make the corresponding changes to the tables in case of a successful booking. Once the user logs out (by quitting the application), logs in (if they previously were not logged in), or performs another search within the same login session, then all previously returned itineraries are invalidated and cannot be booked. 
  
    If booking is successful, then assign a new reservation ID to the booked itinerary. Note that 1) each reservation can contain up to 2 flights (in the case of indirect flights), and 2) each reservation should have a unique ID that incrementally increases by 1 for each successful booking.


- **pay** allows a user to pay for an existing reservation. 
  It first checks whether the user has enough money to pay for all the flights in the given reservation. If successful, it updates the reservation to be paid.


- **reservations** lists all reservations for the user. 
  Each reservation must have ***a unique identifier (which is different for each itinerary) in the entire system***, starting from 1 and increasing by 1 after a reservation has been made. 
  
    There are many ways to implement this. One possibility is to define a "ID" table that stores the next ID to use, and update it each time when a new reservation is made successfully. Make sure you check the case where two different users try to book two different itineraries at the same time!
  
    The user must be logged in to view reservations. The itineraries should be displayed using similar format as that used to display the search results, and they should be shown in increasing order of reservation ID under that username. Cancelled reservations should not be displayed, if you choose to implement cancel for extra credit. 


- **cancel** (optional, only if you like to get up to 10 points extra credit) lets a user to cancel an existing reservation. The user must be logged in to cancel reservations and must provide a valid reservation ID. Make sure you make the corresponding changes to the tables in case of a successful cancellation (e.g., if a reservation is already paid, then the customer should be refunded).


- **quit** leaves the interactive system and logs out the current user (if logged in).


Refer to the Javadoc in `Query.java` for full specification and the expected responses of the commands above. 

**Make sure your code produces outputs in the same formats as prescribed! (see test cases for what to expect)**


### Task 1: Customer database design (6 points)

Your first task is to design and add tables to your flights database. You should decide on the physical layout given the logical data model described above. You can add other tables to your database as well.

**What to turn in**: a single text file called `createTables.sql` with `CREATE TABLE` and any `INSERT` statements (and optionally any `CREATE INDEX` statements) needed to implement the logical data model above. We will test your implementation with the flights table populated with HW3 data using the schema above, and then running your `createTables.sql`. So make sure your file is runnable on SQL Azure through SQL Server Management Studio or their web interface. 

Write a separate script file with `DROP TABLE` or `DELETE FROM` statements; 
it's useful to run it whenever you find a bug in your schema or data (don't turn in this file).
 

### Task 2: Java customer application (80 points)

Your second task is to write the Java application that your customers will use, by completing the starter code. You need to modify only `Query.java`. You do not need to modify `FlightService.java` as we will test your homework by running a grader script. If you do make modifications, make sure we can still test your `Query.java` on the original `FlightService.java`.

**What to turn in**: `Query.java` and, from the previous homework, `QuerySearchOnly.java`

When your application starts, it should show the above menu of options to the user. Your task is to implement the functionality behind these options.

Be sure to use SQL transactions when appropriate. The same user can log in multiple times from different terminals. Different users can also use the same application at the same time from different terminals, and your application should not return inconsistent results (e.g., allowing a user to book an already full flight).


### Task 2A: Implement `clearTables`

Implement this method in `Query.java` to clear the contents of any tables you have created for this assignment (e.g., reservations). However, do not drop any of them and do not delete the contents or drop the `Fights`, `Carrier`, `Weekdays` or `Months` tables. 

After calling this method the database should be in the same state as the beginning, i.e., with the flights table populated and `createTables.sql` called. This method is for running the test harness where each test case is assumed to start with a clean database. You will see how this works after running the test harness.

**`clearTables` should not take more than a minute.** Make sure your database schema is designed with this in mind.


### Task 2B: Transaction management

You must use SQL transactions to guarantee ACID properties: you must set isolation level for your `Connection`, define begin- and end-transaction statements, and insert them in appropriate places in `Query.java`. In particular, you must ensure that the following constraints are always satisfied, even if multiple instances of your application talk to the database at the same time.


*C1*. Each flight should have a maximum capacity that must not be exceeded. Each flight’s capacity is stored in the Flights table as in HW3, and you should have records as to how many seats remain on each flight based on the reservations.

*C2*. A customer may have at most one reservation on any given day, but they can be on more than 1 flight on the same day. (i.e., a customer can have one reservation on a given day that includes two flights, because the reservation is for a one-hop itinerary).

You must use transactions correctly such that race conditions introduced by concurrent execution cannot lead to an inconsistent state of the database. For example, multiple customers may try to book the same flight at the same time. Your properly designed transactions should prevent that.

Design transactions correctly. Avoid including user interaction inside a SQL transaction: that is, don't begin a transaction then wait for the user to decide what she wants to do (why?). The rule of thumb is that transactions need to be as short as possible, but not shorter.

Your `executeQuery` call will throw an `Exception` when an error occurs (e.g., multiple customers try to book the same flight concurrently). Make sure you handle the `Exception` appropriately. For instance, if a seat is still available, the booking should eventually go through (even though you might need to retry due to `Exception`s being thrown). If no seat is available, the booking should be rolled back, etc. 
 
When one uses a DBMS, recall that by default *each statement executes in its own transaction*. As discussed in lecture, to group multiple statements into a transaction, we use
```
BEGIN TRANSACTION
....
COMMIT or ROLLBACK
```
This is the same when executing transactions from Java, by default each SQL statement will be executed as its own transaction. To group multiple statements into one transaction in java, you can do one of three things:

*Approach 1*:

We provide you with three helper methods. So before your first statement in the transaction, simply execute:
```
beginTransaction();
```
When you are done with the transaction, then call:
```
commitTransaction();
```
OR
```
rollbackTransaction();
```

*Approach 2*:

Execute the SQL code for `BEGIN TRANSACTION` and friends directly, using the SQL code we have provided in the starter code (also check out SQL Azure's [transactions documentation](http://msdn.microsoft.com/en-us/library/windowsazure/ee336270.aspx)):

```Java
// When you start the database up
Connection conn = [...]
conn.setAutoCommit(true); // This is the default setting, actually
conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

// In each operation that is to be a multi-statement SQL transaction:
conn.setAutoCommit(false); 
// You MUST do this in order to tell JDBC that you are starting a 
// multi-statement transaction

beginTransactionStatement.executeUpdate();

// ... execute updates and queries.

commitTransactionStatement.executeUpdate();
// OR
rollbackTransactionStatement.executeUpdate();

conn.setAutoCommit(true);  
// To make sure that future statements execute as their own transactions.
```

*Approach 3*:
```Java
// When you start the database up
Connection conn = [...]
conn.setAutoCommit(true); // This is the default setting, actually
conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

// In each operation that is to be a multi-statement SQL transaction:

conn.setAutoCommit(false);
// ... execute updates and queries.
conn.commit();
// OR
conn.rollback();
conn.setAutoCommit(true);
``` 

When auto-commit is set to true, each statement executes in its own transaction. With auto-commit set to false, you can execute many statements within a single transaction. By default, on any new connection to a DB auto-commit is set to true.


### Task 3: Write test cases (14 points)

In the previous homework we asked you to write test case(s) for the search command that you implemented. In this homework, we ask you to write additional test cases for the remaining commands.  

In particular, we will now consider concurrent users interfacing with your database in test cases. The syntax for specifying concurrent test cases is specified below, after a reminder of the general test case format.

To run the JUnit test harness, execute: 

```
./runTests.sh <folder to your java source files> <output folder that you want the compiled class files to be stored> <folder that contains the test cases>
```

For instance, if you are currently in the folder where you downloaded the starter code, then run

```
./runTests.sh . tmp cases
```

This first compiles `*.java` in the input folder you specified (i.e., the current folder aka `.`), deletes and recreates the output folder `./tmp` (make sure you have nothing important in there as it will be erased!!), and then run the test cases in the `cases` directory.

**Note** if you are on Windows you'll need to use the Windows version of the script `runTestsWindows.sh` and run it on a terminal that can execute shell scripts like git bash. It's identical to `runTests.sh` except that the colons `:` are replaced with semicolons `;` in the classpaths.

An example run should look like this (assuming only one test case present):

```bash
$ ./runTests.sh . tmp cases

compiling from  .
added manifest
adding: FlightService.class(in = 4609) (out= 2478)(deflated 46%)
... 
...
JUnit version 4.12
running cases from: cases

running setup
.running test: <folder name>/book_2UsersSameFlight.txt
passed

Time: 8.503

OK (1 test)

```

Run this on the starter code and you will see what the failed test cases print out. For every test case it will either print pass or fail, and for all failed cases it will dump out what the implementation returned, and you can compare it with the expected output in the corresponding case file. 

Each test case file is of the following format:

```sh
[command 1]
[command 2]
...
* 
[expected output line 1]
[expected output line 2]
...
*
# everything following ‘#’ is a comment on the same line
```

The `*` separates between commands and the expected output. To test with multiple concurrent users, simply add more `[command...] * [expected output...]` pairs to the file, for instance: 
 
 ```sh
 [command 1 for user1]
 [command 2 for user1]
 ...
 * 
 [expected output line 1 for user1]
 [expected output line 2 for user1]
 ...
 *
 [command 1 for user2]
 [command 2 for user2]
 ...
 * 
 [expected output line 1 for user2]
 [expected output line 2 for user2]
  ...
 *
 ```
 
Each user is expected to start concurrently in the beginning. If there are multiple output possibilities due to transactional behavior, then separate each group of expected output with `|`. See `book_2UsersSameFlight.txt` for an example.

Your task is to write **at least 1 test case for each of the 6 commands** (you don't need to test `quit`, and 7 if you choose to implement `cancel` for extra credit). Please include the test cases that you wrote for the `search` command. Separate each test case in its own file and name it `<command name>_<some descriptive name for the test case>.txt` and turn them in. It’s fine to turn in test cases for erroneous conditions (e.g., booking on a full flight, logging in with a non-existent username), but at least some of the test cases should do something useful.


### Grading and final comments

- For grading purposes, you can assume that we will only issue only the commands listed above and without spelling errors.

- For Task 2, we will grade your implementation by running the test harness using a number of staff test cases, and assigning points based on how many test cases your implementation passes. Make sure you adhere to the expected output format as specified in `Query.java`. We won’t be able to give you points even if all you miss was a `\n`!

- The starter code is designed to give you a gentle introduction to embedding SQL into Java. Start by running the starter code, examine it and make sure you understand the part that works. You will need to create new tables and slowly add code to the application.

- The completed project has multiple simple SQL queries embedded in the Java code. Some queries are parameterized: a parameter is a constant that is known only at runtime, and therefore appears as a `?` in the SQL code in Java; you already have examples in the starter code.

- The test harness (`Grader.java`) runs each test case and then calls your `clearTables` method after each case (see `clearDB` in `Grader.java`). So make sure that your `clearTables` method reset the contents of the table to whatever it should be as described in Task 2C.

- Make sure you do all error handling in Query.java since we will be testing your code using `FlightService.java` that is included in the starter code.


## Submission Instructions

Add your Java code (`QuerySearchOnly.java` and `Query.java`) and `createTables.sql` that you created to the repo.

**Important**: To remind you, in order for your answers to be added to the git repo, you need to explicitly add each file:

```sh
$ git add Query.java ...
```

**Again, just because your code has been committed on your local machine does not mean that it has been submitted -- it needs to be on GitLab!**

Use the same bash script `turnInHw.sh` (for one last time!) in the hw directory of your repository that commits your changes, deletes any prior tag for the current lab, tags the current commit, and pushes the branch and tag to GitLab. 

If you are using Linux or Mac OSX, you should be able to run the following:

```sh
$ ./turnInHw.sh hw8
```

Like previous assignments, make sure you check the results afterwards to make sure that your file(s) have been committed.
