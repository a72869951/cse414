-- Write a SQL query that returns all restaurants in your table. Experiment with a few of SQLite'soutput formats and show the command you use to format the output along with your query:



-- print the results in comma-separated form
.mode list
.separator ,
select * from MyRestaurants;

-- print the results in list form, delimited by "|"
.separator |
select * from MyRestaurants;


-- print the results in column form, and make each column have width 15
.mode column
.width 15 15 15 15 15

-- for each of the formats above, try printing/not printing the column headers with the results

-- header on
.header on

.mode list
.separator ,
select * from MyRestaurants;

.separator |
select * from MyRestaurants;

.mode column
.width 15 15 15 15 15
select * from MyRestaurants;

-- header off
.header off

.mode list
.separator ,
select * from MyRestaurants;

.separator |
select * from MyRestaurants;

.mode column
.width 15 15 15 15 15
select * from MyRestaurants;
