provider "aws" {
  region = "ap-northeast-2" # 서울 리전
}

# ==========================================
# 1. VPC 생성
# ==========================================
resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true # Cloud Map 및 RDS 연결을 위해 필수
  enable_dns_hostnames = true # 퍼블릭 IP 할당 컨테이너를 위해 필수

  tags = {
    Name = "${var.project_name}-${var.env}-vpc"
  }
}

# ==========================================
# 2. Internet Gateway (무료 인터넷 출입구)
# ==========================================
resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.project_name}-${var.env}-igw"
  }
}

# ==========================================
# 3. Subnets 생성
# ==========================================
# [Public] 사장님/직원 API용 서브넷 (NAT 없이 직접 인터넷 통신)
resource "aws_subnet" "public_api_a" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = var.subnet_cidrs.api_a
  availability_zone       = "ap-northeast-2a"
  map_public_ip_on_launch = true # Fargate 컨테이너에 자동 Public IP 부여

  tags = { Name = "${var.project_name}-${var.env}-public-api-a" }
}

resource "aws_subnet" "public_api_c" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = var.subnet_cidrs.api_c
  availability_zone       = "ap-northeast-2c"
  map_public_ip_on_launch = true

  tags = { Name = "${var.project_name}-${var.env}-public-api-c" }
}

# [Public] 백오피스 API용 서브넷
resource "aws_subnet" "public_admin_a" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = var.subnet_cidrs.admin_a
  availability_zone       = "ap-northeast-2a"
  map_public_ip_on_launch = true

  tags = { Name = "${var.project_name}-${var.env}-public-admin-a" }
}

resource "aws_subnet" "public_admin_c" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = var.subnet_cidrs.admin_c
  availability_zone       = "ap-northeast-2c"
  map_public_ip_on_launch = true

  tags = { Name = "${var.project_name}-${var.env}-public-admin-c" }
}

# [Private] 데이터베이스(RDS)용 서브넷 (인터넷 차단)
resource "aws_subnet" "private_db_a" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = var.subnet_cidrs.db_a
  availability_zone = "ap-northeast-2a"

  tags = { Name = "${var.project_name}-${var.env}-private-db-a" }
}

resource "aws_subnet" "private_db_c" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = var.subnet_cidrs.db_c
  availability_zone = "ap-northeast-2c"

  tags = { Name = "${var.project_name}-${var.env}-private-db-c" }
}

# ==========================================
# 4. Route Tables & Associations (길 안내)
# ==========================================
# 퍼블릭 라우팅 테이블 (모든 외부 트래픽을 IGW로 보냄)
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = { Name = "${var.project_name}-${var.env}-rt-public" }
}

# 라우팅 테이블에 퍼블릭 서브넷들 연결
resource "aws_route_table_association" "api_a" {
  subnet_id      = aws_subnet.public_api_a.id
  route_table_id = aws_route_table.public.id
}
resource "aws_route_table_association" "api_c" {
  subnet_id      = aws_subnet.public_api_c.id
  route_table_id = aws_route_table.public.id
}
resource "aws_route_table_association" "admin_a" {
  subnet_id      = aws_subnet.public_admin_a.id
  route_table_id = aws_route_table.public.id
}
resource "aws_route_table_association" "admin_c" {
  subnet_id      = aws_subnet.public_admin_c.id
  route_table_id = aws_route_table.public.id
}
