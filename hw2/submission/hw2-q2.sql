
-- question 2
select C.name as name,
F1.flight_num as f1_flight_num,
F1.origin_city as f1_origin_city,
F1.dest_city as f1_dest_city,
F1.actual_time as f1_actual_time,
F2.flight_num as f2_flight_num,
F2.origin_city as f2_origin_city,
F2.dest_city as f2_dest_city,
F2.actual_time as f2_actual_time,
F1.actual_time + F2.actual_time as actual_time
from FLIGHTS as F1, FLIGHTS as F2, MONTHS as M, CARRIERS as C
where F1.dest_city = F2.origin_city
and F1.month_id = M.mid
and F2.month_id = M.mid
and F1.carrier_id = C.cid
and F2.carrier_id = C.cid
and F1.carrier_id = F2.carrier_id
and F1.origin_city = 'Seattle WA'
and F2.dest_city = 'Boston MA'
and M.month = 'July'
and F1.day_of_month = 15
and F2.day_of_month = 15
and (F1.actual_time + F2.actual_time) < 420;


/*
Result

name|f1_flight_num|f1_origin_city|f1_dest_city|f1_actual_time|f2_flight_num|f2_origin_city|f2_dest_city|f2_actual_time|actual_time
American Airlines Inc.|198|Seattle WA|New York NY|339|84|New York NY|Boston MA|74|413
American Airlines Inc.|198|Seattle WA|New York NY|339|199|New York NY|Boston MA|80|419
American Airlines Inc.|198|Seattle WA|New York NY|339|1443|New York NY|Boston MA|80|419
American Airlines Inc.|198|Seattle WA|New York NY|339|2118|New York NY|Boston MA|0|339
American Airlines Inc.|198|Seattle WA|New York NY|339|2121|New York NY|Boston MA|74|413
American Airlines Inc.|198|Seattle WA|New York NY|339|2122|New York NY|Boston MA|65|404
American Airlines Inc.|198|Seattle WA|New York NY|339|2126|New York NY|Boston MA|60|399
American Airlines Inc.|198|Seattle WA|New York NY|339|2131|New York NY|Boston MA|70|409
American Airlines Inc.|198|Seattle WA|New York NY|339|2136|New York NY|Boston MA|63|402

total row 1472
*/


