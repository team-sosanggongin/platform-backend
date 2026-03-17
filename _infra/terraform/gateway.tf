# ==========================================
# 7. API Gateway (HTTP API)
# ==========================================
# HTTP API 생성 (REST API보다 저렴하고 빠름)
resource "aws_apigatewayv2_api" "main" {
  name          = "${var.project_name}-${var.env}-gw"
  protocol_type = "HTTP"
}

# API Gateway용 보안 그룹 (이후 ECS 보안 그룹에서 이 SG만 허용하도록 설정)
resource "aws_security_group" "api_gateway" {
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

# VPC Link: API Gateway가 VPC 내부 서브넷으로 들어오기 위한 통로
resource "aws_apigatewayv2_vpc_link" "main" {
  name               = "${var.project_name}-${var.env}-vpc-link"
  security_group_ids = [aws_security_group.api_gateway.id]
  subnet_ids         = [aws_subnet.public_api_a.id] # Single-AZ 정책에 맞춰 a존만 연결
}

# ==========================================
# 8. API Gateway Integration & Route
# ==========================================
# 모든 요청($default)을 ECS 서비스(Cloud Map)로 전달하는 설정
resource "aws_apigatewayv2_integration" "api" {
  api_id           = aws_apigatewayv2_api.main.id
  integration_type = "HTTP_PROXY"
  integration_method = "ANY"
  connection_type  = "VPC_LINK"
  connection_id    = aws_apigatewayv2_vpc_link.main.id

  # Cloud Map을 통해 등록된 서비스의 엔드포인트 지정
  integration_uri  = aws_service_discovery_service.api.arn
  payload_format_version = "1.0"
}

resource "aws_apigatewayv2_route" "default" {
  api_id    = aws_apigatewayv2_api.main.id
  route_key = "$default" # 모든 경로 요청을 수용
  target    = "integrations/${aws_apigatewayv2_integration.api.id}"
}

# API 스테이지 생성 및 자동 배포
resource "aws_apigatewayv2_stage" "main" {
  api_id      = aws_apigatewayv2_api.main.id
  name        = "$default"
  auto_deploy = true
}