-- Question three

select f2.origin_city as origin_city, cast((select count(*) as number
                                            from FLIGHTS as f1
                                            where f1.actual_time < 180
                                            and f1.origin_city = f2.origin_city
                                            group by f1.origin_city) as float) * 100 /count(*) as percentage
from FLIGHTS as f2
group by f2.origin_city;

/*
result:
1.the number of the query returns
327

2.how long the query took
26s

3.first 20 rows of the results
origin_city percentage

Dothan AL	100
Toledo OH	99.83471074380165
Peoria IL	99.86648865153538
Yuma AZ	100
Bakersfield CA	82.97546012269939
Ontario CA	88.44147715418319
Daytona Beach FL	97.54601226993866
Laramie WY	100
Victoria TX	100
North Bend/Coos Bay OR	100
Erie PA	100
Guam TT	
Columbus GA	100
Wichita Falls TX	100
Juneau AK	99.72375690607734
Hartford CT	87.05277722870133
Hattiesburg/Laurel MS	100
Myrtle Beach SC	99.25428784489188
Arcata/Eureka CA	99.57264957264957
Kotzebue AK	98.70967741935483

*/