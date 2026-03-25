project_name = "sosangongin"
env          = "stg"
vpc_cidr     = "10.22.0.0/16"

subnet_cidrs = {
  api_a   = "10.22.0.0/20"
  api_c   = "10.22.16.0/20"
  admin_a = "10.22.32.0/24"
  admin_c = "10.22.33.0/24"
  db_a    = "10.22.34.0/24"
  db_c    = "10.22.35.0/24"
}

container_cpu         = 256   # 0.25 vCPU
container_memory      = 512   # 0.5 GB
service_desired_count = 1     # 딱 1대만 띄움