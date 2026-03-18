# 1. ECR DKR 엔드포인트 - ECR.DKR 엔드포인트 (VPC <-> 레지스트리 연결을 위한 엔드포인트)
resource "aws_vpc_endpoint" "ecr_dkr" {
  vpc_id              = aws_vpc.main.id
  service_name        = "com.amazonaws.ap-northeast-2.ecr.dkr"
  vpc_endpoint_type   = "Interface"
  private_dns_enabled = true
  subnet_ids          = [aws_subnet.public_api_a.id] # ECS가 떠 있는 서브넷
  security_group_ids  = [aws_security_group.vpc_endpoint_sg.id]
}

# 2. ECR API 엔드포인트 (인증 및 API 호출용)
resource "aws_vpc_endpoint" "ecr_api" {
  vpc_id              = aws_vpc.main.id
  service_name        = "com.amazonaws.ap-northeast-2.ecr.api"
  vpc_endpoint_type   = "Interface"
  private_dns_enabled = true
  subnet_ids          = [aws_subnet.public_api_a.id]
  security_group_ids  = [aws_security_group.vpc_endpoint_sg.id]
}

# 3. S3 Gateway 엔드포인트 (ECR의 실제 레이어 파일은 S3에 저장되어 있음)
resource "aws_vpc_endpoint" "s3" {
  vpc_id            = aws_vpc.main.id
  service_name      = "com.amazonaws.ap-northeast-2.s3"
  vpc_endpoint_type = "Gateway"
  route_table_ids   = [aws_route_table.public.id] # 라우팅 테이블에 자동 추가
}