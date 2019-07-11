
-- question 1
select distinct flight_num as flight_num
from FLIGHTS as F, CARRIERS as C, WEEKDAYS as W
where F.carrier_id = C.cid
and W.did = F.day_of_week_id
and F.origin_city = 'Seattle WA' 
and F.dest_city = 'Boston MA' 
and C.name = 'Alaska Airlines Inc.'
and W.day_of_week = 'Monday';

/*
Result:

flight_num
12
24
734
*/