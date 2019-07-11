-- Write a SQL query that returns only the name and distance of all restaurants within and including 20 minutes of your house. The query should list the restaurants in alphabetical order of names.

select restaurant_name, distance_from_home
from MyRestaurants
where distance_from_home <= 20
order by restaurant_name;