-- question six

with subFlight as (select distinct f1.carrier_id
from FLIGHTS as f1
where f1.origin_city = 'Seattle WA'
and f1.dest_city = 'San Francisco CA')
select c1.name as carrier
from CARRIERS as c1, subFlight as sub
where c1.cid = sub.carrier_id
order by c1.name asc;

/*
result:
1.the number of the query returns
4

2.how long the query took
6

3.first 20 rows of the results
CARRIER
Alaska Airlines Inc.
SkyWest Airlines Inc.
United Air Lines Inc.
Virgin America
*/
