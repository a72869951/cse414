-- Next, you will create a table with attributes of types, integer, varchar, date, and boolean. However, SQLite does not have date and boolean: you will use varchar and int instead. Create a table called MyRestaurants.

create table MyRestaurants (restaurant_name varchar(25), food_type varchar(25), distance_from_home int, date_of_last_visit varchar(25), like_it_or_not int); 

