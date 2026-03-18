variable "project_name" {
  description = "프로젝트 이름"
  type        = string
  default     = "my-service"
}

variable "env" {
  description = "환경 (stg 또는 prod)"
  type        = string
}

variable "vpc_cidr" {
  description = "VPC 전체 대역"
  type        = string
}

variable "subnet_cidrs" {
  description = "각 서브넷들의 CIDR 대역"
  type = object({
    api_a   = string
    api_c   = string
    admin_a = string
    admin_c = string
    db_a    = string
    db_c    = string
  })
}

# variables.tf

variable "container_cpu" {
  description = "ECS Task CPU units (256, 512, 1024 ...)"
  type        = number
}

variable "container_memory" {
  description = "ECS Task Memory in MiB (512, 1024, 2048 ...)"
  type        = number
}

variable "service_desired_count" {
  description = "Number of tasks to run"
  type        = number
}