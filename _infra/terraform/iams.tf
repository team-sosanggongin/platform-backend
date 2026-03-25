# 1. ECS Task Execution용 IAM (PULL 전용)
resource "aws_iam_user" "ecs_agent" {
  name = "${var.project_name}-${var.env}-ecs-agent"
}

# ECR에서 이미지를 가져오고 로그를 남길 수 있는 표준 정책 연결
resource "aws_iam_user_policy_attachment" "ecs_agent_execution" {
  user       = aws_iam_user.ecs_agent.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}