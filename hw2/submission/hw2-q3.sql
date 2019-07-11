
-- question 3

select W.day_of_week, AVG(F.arrival_delay) as delay
from FLIGHTS as F, WEEKDAYS as W
where F.day_of_week_id = W.did
group by W.day_of_week
order by AVG(F.arrival_delay) DESC
limit 1;

/*
Result:
day_of_week|delay
Friday|14.4725010477787
*/