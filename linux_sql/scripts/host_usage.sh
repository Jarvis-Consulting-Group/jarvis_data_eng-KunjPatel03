#Storing command line arguments
psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

#Check number of arguments
if [ "$#" -ne 5 ]; then
  echo "Illegal number of parameters"
  exit 1
fi

#Save current machine hostname to variable
hostname=$(hostname -f)
#Subquery to find matching id in host_info table
host_id="(SELECT id FROM host_info WHERE hostname='$hostname')";
vmstat_out=$(vmstat --unit M)

#Retrieve usage information into variables
memory_free=$(echo "$vmstat_out" | awk '{print $4}'| tail -n1 | xargs)
cpu_idle=$(echo "$vmstat_out" | awk '{print $15}' | tail -n1 | xargs)
cpu_kernel=$(echo "$vmstat_out" | awk '{print $14}' | tail -n1 | xargs)
disk_io=$(vmstat -d | awk '{print $10}' | grep -o '[0-9]*')
disk_available=$(df -BM / | awk '{print $4}' | grep -o '[0-9]*')
timestamp=$(date +"%Y-%m-%d %H:%M:%S")

#PSQL command that inserts server usage data into host_usage table
insert_stmt="INSERT INTO host_agent.public.host_usage
              ( timestamp, host_id, memory_free, cpu_idle, cpu_kernel, disk_io, disk_available)
              VALUES
              ('$timestamp', $host_id, $memory_free, $cpu_idle, $cpu_kernel, $disk_io, $disk_available);"

#set up environmental variable for pql command
export PGPASSWORD=$psql_password
#Insert date into a database
psql -h "$psql_host" -p "$psql_port" -d "$db_name" -U "$psql_user" -c "$insert_stmt"
exit $?