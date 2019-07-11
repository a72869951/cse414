-- Write a SQL query that returns all restaurants that you like, but have not visited since more than 3 months ago.

select restaurant_name
from MyRestaurants
where like_it_or_not = 1 and date_of_last_visit < date('now', '-3 month');
