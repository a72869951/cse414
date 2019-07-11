-- Question one
select distinct f2.origin_city, f2.dest_city, f2.actual_time as time
from FLIGHTS as f2, (select f.origin_city, MAX(actual_time) as maximum
from FLIGHTS as f group by f.origin_city) as f1
where f2.origin_city = f1.origin_city
and f1.maximum = f2.actual_time
order by f2.origin_city, f2.dest_city asc;

/*
result:
1.the number of the query returns
334

2.how long the query took
31s

3.first 20 rows of the results
origin_city     dest_city       time
Aberdeen SD	Minneapolis MN	106
Abilene TX	Dallas/Fort Worth TX	111
Adak Island AK	Anchorage AK	471
Aguadilla PR	New York NY	368
Akron OH	Atlanta GA	408
Albany GA	Atlanta GA	243
Albany NY	Atlanta GA	390
Albuquerque NM	Houston TX	492
Alexandria LA	Atlanta GA	391
Allentown/Bethlehem/Easton PA	Atlanta GA	456
Alpena MI	Detroit MI	80
Amarillo TX	Houston TX	390
Anchorage AK	Barrow AK	490
Appleton WI	Atlanta GA	405
Arcata/Eureka CA	San Francisco CA	476
Asheville NC	Chicago IL	279
Ashland WV	Cincinnati OH	84
Aspen CO	Los Angeles CA	304
Atlanta GA	Honolulu HI	649
Atlantic City NJ	Fort Lauderdale FL	212
*/