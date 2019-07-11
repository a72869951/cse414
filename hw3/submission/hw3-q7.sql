-- Question seven

select distinct c.name as carrier
from CARRIERS as c, FLIGHTS as f
where c.cid = f.carrier_id
and f.dest_city = 'San Francisco CA'
and f.origin_city = 'Seattle WA'
order by c.name asc;

/*
result:
1.the number of the query returns
4

2.how long the query took
6s

3.first 20 rows of the results

CARRIER
Alaska Airlines Inc.
SkyWest Airlines Inc.
United Air Lines Inc.
Virgin America

