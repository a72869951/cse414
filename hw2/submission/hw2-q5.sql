
-- question 5
select C.name, AVG(F.canceled) as percent
from FLIGHTS as F, CARRIERS as C
where F.carrier_id = C.cid
and F.origin_city = 'Seattle WA'
group by C.name
having AVG(F.canceled) > 0.005

/*
Result:

name|percent
ExpressJet Airlines Inc.|0.032258064516129
Frontier Airlines Inc.|0.00840336134453781
JetBlue Airways|0.0100250626566416
Northwest Airlines Inc.|0.014336917562724
SkyWest Airlines Inc.|0.00728291316526611
United Air Lines Inc.|0.00983767830791933
*/