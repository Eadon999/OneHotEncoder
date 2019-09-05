SELECT device_id,act_time,gender,ip_provi,ip_city,brand
FROM
(SELECT device_id,hour(l_act_t) as act_time,ip_provi,phone_type as brand,gender,ip_city,abs(datediff(l_act_t,'2019-08-29 00:00:01.613')) as act_day
FROM dm_profile.user_dim_profile
where day = 20190829) t1
where act_day <=3;