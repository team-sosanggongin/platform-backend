# API Gateway용 보안 그룹 (이후 ECS 보안 그룹에서 이 SG만 허용하도록 설정)
resource "aws_security_group" "api_gateway_sg" {
  name        = "${var.project_name}-${var.env}-apigw-sg"
  vpc_id      = aws_vpc.main.id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project_name}-${var.env}-apigw-sg" }
}

# 사용자 REST API 전용 보안 그룹
resource "aws_security_group" "user_api_sg" {
  name        = "${var.project_name}-${var.env}-user-api-sg"
  description = "Allow traffic only from API Gateway VPC Link"
  vpc_id      = aws_vpc.main.id

  # Ingress: API Gateway의 VPC Link를 통해서만 들어오는 80, 443 허용
  ingress {
    from_port       = 80
    to_port         = 80
    protocol        = "tcp"
    # 실제 운영 환경에서는 API Gateway 전용 SG ID를 참조합니다.
    security_groups = [aws_security_group.api_gateway_sg.id]
  }

  ingress {
    from_port       = 443
    to_port         = 443
    protocol        = "tcp"
    security_groups = [aws_security_group.api_gateway_sg.id]
  }

  # Egress: S3, ECR, 외부 API(결제, 인증 등) 호출을 위해 전체 허용
  # NAT가 없으므로 이 설정이 있어야 서버가 직접 인터넷으로 나갑니다.
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-${var.env}-user-api-sg"
  }
}

# 백오피스 전용 보안 그룹
resource "aws_security_group" "admin_api_sg" {
  name        = "${var.project_name}-${var.env}-admin-sg"
  description = "Allow traffic only from API Gateway"
  vpc_id      = aws_vpc.main.id

  # Ingress: API Gateway(또는 VPC Link)로부터 오는 트래픽만 허용
  ingress {
    from_port       = 80
    to_port         = 80
    protocol        = "tcp"
    # 중요: API Gateway가 사용하는 보안 그룹 ID를 지정하거나,
    # VPC Link가 배치된 서브넷의 CIDR 대역만 허용합니다.
    security_groups = [aws_security_group.api_gateway_sg.id]
  }

  ingress {
    from_port       = 443
    to_port         = 443
    protocol        = "tcp"
    security_groups = [aws_security_group.api_gateway_sg.id]
  }

  # Egress: 외부 통신(S3, DB 등)을 위해 모든 방향 허용
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

//ECS ENDPOINT 보안그룹 설정 -> 백오피스, rest API에서 INGRESS 요청에 대한 OKAY
resource "aws_security_group" "vpc_endpoint_sg" {
  name   = "${var.project_name}-${var.env}-vpce-sg"
  vpc_id = aws_vpc.main.id

  ingress {
    from_port       = 443
    to_port         = 443
    protocol        = "tcp"
    security_groups = [
      aws_security_group.user_api_sg.id,
      aws_security_group.admin_api_sg.id
    ]
  }
}