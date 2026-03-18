# 클러스터 이름에 ${var.env}를 넣어 자동으로 분리
resource "aws_ecs_cluster" "main" {
  name = "${var.project_name}-${var.env}-cluster"
}

# 사용자용 ECS 서비스
resource "aws_ecs_service" "user_api" {
  name    = "${var.project_name}-${var.env}-user-service"
  cluster = aws_ecs_cluster.main.id

  network_configuration {
    # 사장님이 정하신 전용 서브넷 지정
    subnets          = [aws_subnet.public_api_a.id]
    security_groups  = [aws_security_group.user_api_sg.id]
    assign_public_ip = true
  }
  service_registries {
    registry_arn = aws_service_discovery_service.api.arn
    # container_name과 container_port는 Task Definition에 정의한 것과 일치해야 함
    container_name = "user-api-container"
  }
}

# 백오피스용 ECS 서비스
resource "aws_ecs_service" "admin_api" {
  name    = "${var.project_name}-${var.env}-admin-service"
  cluster = aws_ecs_cluster.main.id

  network_configuration {
    # 백오피스 전용 서브넷 지정
    subnets          = [aws_subnet.public_admin_a.id]
    security_groups  = [aws_security_group.admin_api_sg.id]
    assign_public_ip = true
  }
  service_registries {
    registry_arn = aws_service_discovery_service.api.arn
    # container_name과 container_port는 Task Definition에 정의한 것과 일치해야 함
    container_name = "backoffice-api-container"
  }
}