-- add all your SQL setup statements here. 

-- You can assume that the following base table has been created with data loaded for you when we test your submission 
-- (you still need to create and populate it in your instance however),
-- although you are free to insert extra ALTER COLUMN ... statements to change the column 
-- names / types if you like.

-- 1.Create table Capacities
CREATE TABLE Capacities
(
	fid int NOT NULL PRIMARY KEY,
	capacity int
)

--2.Create table Reservations
CREATE TABLE Reservations
(   rid int NOT NULL PRIMARY KEY,
	fid1 int,
	fid2 int,
	paid int,
	cost int,
	username varchar(30)
)

--3.Create table Users
CREATE TABLE Users
(
	username varchar(30) NOT NULL PRIMARY KEY,
	password varchar(30),
	balance int
)