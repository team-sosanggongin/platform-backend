resource "aws_apigatewayv2_api" "main" {
  name          = "${var.project_name}-${var.env}-gw"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_vpc_link" "main" {
  name               = "${var.project_name}-${var.env}-vpc-link"
  security_group_ids = [aws_security_group.api_gateway.id]
  subnet_ids         = [aws_subnet.public_api_a.id]
}

resource "aws_security_group" "api_gateway" { ... }

resource "aws_apigatewayv2_integration" "api" { ... }
resource "aws_apigatewayv2_route" "default" { ... }
resource "aws_apigatewayv2_stage" "main" { ... }