--Question five

select distinct f4.origin_city as city
from Flights as f4
where f4.origin_city not in (select distinct f3.dest_city as city
from FLIGHTS as f2, FLIGHTS as f3
where f2.dest_city = f3.origin_city
and f2.origin_city = 'Seattle WA')
and f4.origin_city not in (select distinct f1.dest_city as city
from FLIGHTS as f1
where f1.origin_city = 'Seattle WA')
order by f4.origin_city asc;

/*
result:
1.the number of the query returns
4(I assume all cities to be the collection of all origin_city)

2.how long the query took
102s

3.first 20 rows of the results
CITY
Devils Lake ND
Hattiesburg/Laurel MS
St. Augustine FL
Victoria TX
*/
