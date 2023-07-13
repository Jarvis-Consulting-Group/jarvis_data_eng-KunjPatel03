-- Insert sample data in host_info talbe
INSERT INTO host_agent.public.host_info (id, hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, l2_cache, "timestamp", total_mem)
    VALUES
        (1, 'jrvs-remote-desktop-centos7-6.us-central1-a.c.spry-framework-236416.internal', 1, 'x86_64', 'Intel(R) Xeon(R) CPU @ 2.30GHz', 2300, 256, '2019-05-29 17:49:53.000', 601324)
        ,(2, 'noe1', 1, 'x86_64', 'Intel(R) Xeon(R) CPU @ 2.30GHz', 2300, 256, '2019-05-29 17:49:53.000', 601324)
        ,(3, 'noe2', 1, 'x86_64', 'Intel(R) Xeon(R) CPU @ 2.30GHz', 2300, 256, '2019-05-29 17:49:53.000', 601324);

-- Insert sample data in host_usage table
INSERT INTO host_agent.public.host_usage ("timestamp", host_id, memory_free, cpu_idle, cpu_kernel, disk_io, disk_available)
    VALUES
        ('2019-05-29 15:00:00.000', 1, 300000, 90, 4, 2, 3)
        ,('2019-05-29 15:01:00.000', 1, 200000, 90, 4, 2, 3);