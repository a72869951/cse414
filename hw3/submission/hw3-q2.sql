-- Question two

select distinct f1.origin_city as city
from FLIGHTS as f1
where f1.origin_city not in (select distinct f.origin_city as city
from FLIGHTS as f
where f.actual_time >= 180)
order by f1.origin_city;

/*
result:
1.the number of the query returns
109

2.how long the query took
14s

3.first 20 rows of the results
Aberdeen SD
Abilene TX
Alpena MI
Ashland WV
Augusta GA
Barrow AK
Beaumont/Port Arthur TX
Bemidji MN
Bethel AK
Binghamton NY
Brainerd MN
Bristol/Johnson City/Kingsport TN
Butte MT
Carlsbad CA
Casper WY
Cedar City UT
Chico CA
College Station/Bryan TX
Columbia MO
Columbus GA

*/

