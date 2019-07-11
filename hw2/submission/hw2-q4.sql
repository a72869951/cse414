
-- question 4
select distinct C.name as name
from FLIGHTS as F, CARRIERS as C, MONTHS as M
where F.carrier_id = C.cid
and F.month_id = M.mid
group by M.month, F.day_of_month, C.name
having COUNT(*) > 1000;


/*
Result

name
American Airlines Inc.
Comair Inc.
Delta Air Lines Inc.
Envoy Air
ExpressJet Airlines Inc.
ExpressJet Airlines Inc. (1)
JetBlue Airways
Northwest Airlines Inc.
SkyWest Airlines Inc.
Southwest Airlines Co.
US Airways Inc.
United Air Lines Inc.

*/