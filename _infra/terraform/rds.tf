resource "aws_db_subnet_group" "db_subnet_group" {
  name       = "${var.project_name}-${var.env}-db-subnet-group"
  subnet_ids = [aws_subnet.private_db_a.id, aws_subnet.private_db_c.id]
}