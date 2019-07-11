
-- question 6
select C.name as carrier, MAX(F.price) as max_price
from FLIGHTS as F, CARRIERS as C
where F.carrier_id = C.cid
and ((F.origin_city = 'Seattle WA' and F.dest_city = 'New York NY') or (F.dest_city = 'Seattle WA' and F.origin_city = 'New York NY'))
group by C.name;



/*
Result:

carrier,max_price
"American Airlines Inc.",991
"Delta Air Lines Inc.",999
"JetBlue Airways",996
*/
