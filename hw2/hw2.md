# CSE 414 Homework 2: Basic SQL Queries
 
**Objectives:** To create and import databases and to practice simple SQL queries using SQLite.

**Assignment tools:** [SQLite 3](https://www.sqlite.org/), the flights dataset hosted in `hw2` directory
on gitlab.
(Reminder: To extract the content of a tar file, run the following command in the terminal of your VM, after navigating to the directory containing `flights-small.tar.gz`: 
`tar zxvf flights-small.tar.gz`)

**Assigned date:** Monday, April 15, 2019

**Due date:** Monday, April 15, 2019. You have 1 week for this assignment. Turn in your solution using the provided script. 

**Questions:** Make sure your post them on [Piazza](https://piazza.com/washington/spring2019/cse414). 

**What to turn in:**

`create-tables.sql` and `hw2-q1.sql`, `hw2-q2.sql`, etc (see below).

**Resources:** 

## Obtaining Changes
After you have set the upstream master correctly (see HW1 for details), run `git pull upstream master` in your repository directory and it will pull the HW2 contents (including this file) into your repository. We will announce on Piazza if we have pushed major changes after releasing the HW (and if so you should pull again).

## Assignment Details

In this homework, you will write several SQL queries on a relational flights database. 
The data in this database is abridged from the [Bureau of Transportation Statistics](http://www.transtats.bts.gov/DL_SelectFields.asp?Table_ID=236&DB_Short_Name=On-Time) 
The database consists of four tables regarding a subset of flights that took place in 2015:

```SQL
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
         
CARRIERS (cid varchar(7), name varchar(83))
MONTHS (mid int, month varchar(9))
WEEKDAYS (did int, day_of_week varchar(9))
```

(FYI All data except for the capacity and price columns are real.) 
We leave it up to you to decide how to declare these tables and translate their types to sqlite. 
But make sure that your relations include all the attributes listed above.

In addition, make sure you impose the following constraints to the tables above:
- The primary key of the `FLIGHTS` table is `fid`.
- The primary keys for the other tables are `cid`, `mid`, and `did` respectively. Other than these, *do not assume any other attribute(s) is a key / unique across tuples.*
- `Flights.carrier_id` references `Carriers.cid`
- `Flights.month_id` references `Months.mid`
- `Flights.day_of_week_id` references `Weekdays.did`

We provide the flights database as a set of plain-text data files in the linked 
`.tar.gz` archive. Each file in this archive contains all the rows for the named table, one row per line.

In this homework, you need to do two things:
1. import the flights dataset into SQLite
2. run SQL queries to answer a set of questions about the data.


### IMPORTING THE FLIGHTS DATABASE (20 points)

To import the flights database into SQLite, you will need to run sqlite3 with a new database file.
for example `sqlite3 hw2.db`. Then you can run `CREATE TABLE` statement to create the tables, 
choosing appropriate types for each column and specifying all key constraints as described above:

```
CREATE TABLE table_name ( ... );
```

Currently, SQLite does not enforce foreign keys by default. 
To enable foreign keys use the following command. 
The command will have no effect if you installed your own version of SQLite was not compiled with foreign keys enabled. 
In that case do not worry about it (i.e., you will need to enforce foreign key constraints yourself as 
you insert data into the table).

```
PRAGMA foreign_keys=ON;
```

Then, you can use the SQLite `.import` command to read data from each text file into its table after setting the input data to be in CSV (comma separated value) form:

```
.mode csv
.import filename tablename
```

See examples of `.import` statements in the section notes, and also look at the SQLite 
documentation or sqlite3's help online for details.

Put all the code for this part (four `create table` statements and four `.import` statements) 
into a file called `create-tables.sql` inside the `hw2/submission` directory.


### Writing SQL QUERIES (80 points, 10 points each)

**HINT: You should be able to answer all the questions below with SQL queries that do NOT contain any subqueries!**

For each question below, write a single SQL query to answer that question.
Put each of your queries in a separate `.sql` file as in HW1, i.e., `hw2-q1.sql`, `hw2-q2.sql`, etc.
Add a comment in each file indicating the number of rows in the query result.

**Important: The predicates in your queries should correspond to the English descriptions. For example, if a question asks you to find flights by Alaska Airlines Inc., the query should 
include a predicate that checks for that specific name as opposed to checking for the matching carrier ID. Same for predicates over months, weekdays, etc.**

**Also, make sure you name the output columns as indicated! Do not change the output column names / return more or fewer columns!**

In the following questions below flights **include canceled flights as well, unless otherwise noted.** 
Also, when asked to output times you can report them in minutes and don’t need to do minute-hour conversion.

If a query uses a `GROUP BY` clause, make sure that all attributes in your `SELECT` clause for that query 
are either grouping keys or aggregate values. SQLite will let you select other attributes but that is wrong
as we discussed in lectures. Other database systems would reject the query in that case. 


1. (10 points) List the distinct flight numbers of all flights from Seattle to Boston by Alaska Airlines Inc. on Mondays. 
Also notice that, in the database, the city names include the state. So Seattle appears as 
Seattle WA.  
   Name the output column `flight_num`.    
   [Hint: Output relation cardinality: 3 rows]

2. (10 points) Find all itineraries from Seattle to Boston on July 15th. Search only for itineraries that have one stop (i.e., flight 1: Seattle -> [somewhere], flight2: [somewhere] -> Boston).
Both flights must depart on the same day (same day here means the date of flight) and must be with the same carrier. It's fine if the landing date is different from the departing date (i.e., in the case of an overnight flight). You don't need to check whether the first flight overlaps with the second one since the departing and arriving time of the flights are not provided. 
 
    The total flight time (`actual_time`) of the entire itinerary should be fewer than 7 hours 
(but notice that `actual_time` is in minutes). 
   For each itinerary, the query should return the name of the carrier, the first flight number, 
the origin and destination of that first flight, the flight time, the second flight number, 
the origin and destination of the second flight, the second flight time, and finally the total flight time. 
Only count flight times here; do not include any layover time.

    Name the output columns `name` as the name of the carrier, `f1_flight_num`, `f1_origin_city`, `f1_dest_city`, `f1_actual_time`, `f2_flight_num`, `f2_origin_city`, `f2_dest_city`, `f2_actual_time`, and `actual_time` as the total flight time. List the output columns in this order.
    [Output relation cardinality: 1472 rows]


3. (10 points) Find the day of the week with the longest average arrival delay. 
Return the name of the day and the average delay.   
   Name the output columns `day_of_week` and `delay`, in that order. (Hint: consider using `LIMIT`. Look up what it does!)   
   [Output relation cardinality: 1 row]


4. (10 points) Find the names of all airlines that ever flew more than 1000 flights in one day 
(i.e., a specific day/month, but not any 24-hour period). 
Return only the names of the airlines. Do not return any duplicates 
(i.e., airlines with the exact same name).    
   Name the output column `name`.   
   [Output relation cardinality: 12 rows]


5. (10 points) Find all airlines that had more than 0.5 percent of their flights out of Seattle be canceled. 
Return the name of the airline and the percentage of canceled flight out of Seattle. 
Order the results by the percentage of canceled flights in ascending order.    
   Name the output columns `name` and `percent`, in that order.   
   [Output relation cardinality: 6 rows]


6. (10 points) Find the maximum price of tickets between Seattle and New York, NY 
(i.e. Seattle to New York or New York to Seattle).
Show the maximum price for each airline separately.
   Name the output columns `carrier` and `max_price`, in that order.
   [Output relation cardinality: 3 rows]

7. (10 points) Find the total capacity of all direct flights that fly between Seattle and San Francisco, CA on July 10th 
(i.e. Seattle to San Francisco or San Francisco to Seattle).
   Name the output column `capacity`.
   [Output relation cardinality: 1 row]
   
   
8. (10 points) Compute the total departure delay of each airline 
across all flights.
   Name the output columns `name` and `delay`, in that order.
   [Output relation cardinality: 22 rows]


### Programming style 
Remember to adhere to the SQL programming style from HW1. We repeat this below for your reference.

To encourage good SQL programming style please follow these two simple style rules:

- Give explicit names to all tables referenced in the `FROM` clause.
For instance, instead of writing:
   ``` 
   select * from flights, carriers where carrier_id = cid
   ```
   write
   ```
   select * from flights as F, carriers as C where F.carrier_id = C.cid
   ```
   (notice the `as`) so that it is clear which table you are referring to.


- Similarly, reference to all attributes must be qualified by the table name.
Instead of writing:
   ```
   select * from flights where fid = 1
   ```
   write
   ```
   select * from flights as F where F.fid = 1
   ```
   This will be useful when you write queries involving self joins in later assignments.
   
To help you check on whether your query is compliant with the above, use the [Cosette tool](http://cosette.cs.washington.edu/) developed by the [UW Database research group](http://db.cs.washington.edu/). Cosette is built to do more than syntax checking. To use Cosette, sign up for an account using your *UW email*, read through the brief tutorial, and put your query as one of the input queries (make sure you declare the input table schemas correctly). If your query contains stylistic errors, you will get a "Syntax Error" in the results pane. Otherwise, it will return whether the two input queries are equivalent or not.

Note: Cosette currently can only check the syntax for read queries (i.e., no inserts, updates, or deletes).


## Submission Instructions
Answer each of the queries above and put your SQL query in a separate file. 
Call them `hw2-q1.sql`, `hw2-q2.sql`, etc. Make sure you name the files exactly as is. Put your 
`.sql` files inside `hw2/submission` (along with your `create-tables.sql` from 
the first part of this assignment).
 	
Like HW1, you may submit your code multiple times; we will use the latest version you submit that 
arrives before the deadline. 

**Important**: To remind you, in order for your answers to be added to the git repo, 
you need to explicitly add each file:

```sh
$ git add hw2-q1.sql hw2-q2.sql ...
```

**Again, just because your code has been committed on your local machine does not mean that it has been 
submitted -- it needs to be on GitLab!**

Use the same bash script `turnIn_Hw2.sh` in the root level directory of your repository that 
commits your changes, deletes any prior tag for the current lab, tags the current commit,
and pushes the branch and tag to GitLab. 

If you are using the Linux VM or Mac OSX, you should be able to run the following:

```sh
$ ./turnIn_Hw2.sh 
```

You should see something like the following output:

```sh
$ ./turnIn_Hw2.sh
[master b155ba0] Lab 2
 1 file changed, 1 insertion(+)
Deleted tag 'hw2' (was b26abd0)
To git@gitlab.cs.washington.edu:cse414-2019sp/cse414-[your CSE username].git
 - [deleted]         hw2
Counting objects: 11, done.
Delta compression using up to 4 threads.
Compressing objects: 100% (4/4), done.
Writing objects: 100% (6/6), 448 bytes | 0 bytes/s, done.
Total 6 (delta 3), reused 0 (delta 0)
To git@gitlab.cs.washington.edu:cse414-2019sp/cse414-[your CSE username].git
   ae31bce..b155ba0  master -> master
Counting objects: 1, done.
Writing objects: 100% (1/1), 152 bytes | 0 bytes/s, done.
Total 1 (delta 0), reused 0 (delta 0)
To git@gitlab.cs.washington.edu:cse414-2019sp/cse414-[your CSE username].git
 * [new tag]         hw2 -> hw2
```


#### Final Word of Caution!

Git is a distributed version control system. This means everything operates offline until you run `git pull` or `git push`. This is a great feature.

The bad thing is that you may **forget to `git push` your changes**. This is why we strongly, strongly suggest that you **check GitLab to be sure that what you want us to see matches up with what you expect**.  As a second sanity check, you can re-clone your repository in a different directory to confirm the changes:

```sh
$ git clone git@gitlab.cs.washington.edu:cse414-2019sp/cse414-[your CSE username].git confirmation_directory
$ cd confirmation_directory
$ # ... make sure everything is as you expect ...
