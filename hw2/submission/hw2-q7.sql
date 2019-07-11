
-- question 7
select F.capacity as capacity
from FLIGHTS as F, MONTHS as M
where F.month_id = M.mid
and ((F.origin_city = 'Seattle WA' and F.dest_city = 'San Francisco CA') or (F.dest_city = 'Seattle WA' and F.origin_city = 'San Francisco CA'))
and M.month = 'July'
and F.day_of_month = 10;


/*
Result

capacity
680
*/


