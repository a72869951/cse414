--Question four

select distinct f3.dest_city as city
from FLIGHTS as f2, FLIGHTS as f3
where f2.dest_city = f3.origin_city
and f2.origin_city = 'Seattle WA'
and f3.dest_city != 'Seattle WA'
and f3.dest_city not in (select distinct f1.dest_city as city
            from FLIGHTS as f1
            where f1.origin_city = 'Seattle WA')
order by f3.dest_city ASC;

/*
result:
1.the number of the query returns
256

2.how long the query took
44s

3.first 20 rows of the results
city
Aberdeen SD
Abilene TX
Adak Island AK
Aguadilla PR
Akron OH
Albany GA
Albany NY
Alexandria LA
Allentown/Bethlehem/Easton PA
Alpena MI
Amarillo TX
Appleton WI
Arcata/Eureka CA
Asheville NC
Ashland WV
Aspen CO
Atlantic City NJ
Augusta GA
Bakersfield CA
Bangor ME
*/
