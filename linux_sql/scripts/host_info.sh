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

#Save machine statistics and current machine hostname to variables
hostname=$(hostname -f)
lscpu_out=$(lscpu)

#Retrieve hardware specification variables
cpu_number=$(echo "$lscpu_out"  | grep -E "^CPU\(s\):" | awk -F ':' '{print $2}' | xargs)
cpu_architecture=$(echo "$lscpu_out"  | grep -E "Architecture:" | awk -F ':' '{print $2}' | xargs)
cpu_model=$(echo "$lscpu_out"  | grep -E "Model name:" | awk -F ':' '{print $2}' | xargs)
cpu_mhz=$(echo "$lscpu_out"  | grep -E "CPU MHz:" | awk -F ':' '{print $2}' | xargs)
l2_cache=$(echo "$lscpu_out"  | grep -E "L2 cache:" | awk -F ':' '{print $2}' | xargs)
total_mem=$(free -m | awk '/^Mem:/ {print $2}')
timestamp=$(date +"%Y-%m-%d %H:%M:%S")

#Insert command to insert hardware info data into host_info table
insert_stmt="INSERT INTO host_agent.public.host_info
              (hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, l2_cache, timestamp, total_mem)
              VALUES
              ('$hostname', $cpu_number, '$cpu_architecture', '$cpu_model', $cpu_mhz, '$l2_cache', '$timestamp', $total_mem);"

#set up environment variable for pql command
export PGPASSWORD=$psql_password
#Insert date into a database
psql -h "$psql_host" -p "$psql_port" -d "$db_name" -U "$psql_user" -c "$insert_stmt"
exit $?