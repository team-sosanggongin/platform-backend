# 1. 사용자용 API 이미지 저장소
resource "aws_ecr_repository" "user_api" {
  name                 = "${var.project_name}-${var.env}-user-api"
  image_tag_mutability = "IMMUTABLE" # 한번 올라간 태그는 덮어쓰기 금지 (보안/안정성)

  image_scanning_configuration {
    scan_on_push = true # 이미지 올릴 때마다 취약점 검사 (무료 범위)
  }

  encryption_configuration {
    encryption_type = "AES256"
  }
}

# 2. 백오피스용 API 이미지 저장소
resource "aws_ecr_repository" "admin_api" {
  name                 = "${var.project_name}-${var.env}-admin-api"
  image_tag_mutability = "IMMUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}

# ---------------------------------------------------------
# ♻️ 수명 주기 정책 (Lifecycle Policy)
# 이미지가 너무 많이 쌓여 비용이 나가는 것을 방지합니다.
# ---------------------------------------------------------

resource "aws_ecr_lifecycle_policy" "user_api_policy" {
  repository = aws_ecr_repository.user_api.name

  policy = jsonencode({
    rules = [{
      rulePriority = 1
      description  = "Keep last 10 images"
      selection = {
        tagStatus     = "any"
        countType     = "imageCountMoreThan"
        countNumber   = 10
      }
      action = {
        type = "expire"
      }
    }]
  })
}

# 백오피스도 동일하게 적용 (재사용 가능하도록 모듈화할 수도 있지만 일단 직관적으로 작성)
resource "aws_ecr_lifecycle_policy" "admin_api_policy" {
  repository = aws_ecr_repository.admin_api.name
  policy     = aws_ecr_lifecycle_policy.user_api_policy.policy
}