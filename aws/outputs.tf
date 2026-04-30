output "instance_public_ip" {
  value = aws_instance.web_server.public_ip
}

output "private_key" {
  value     = tls_private_key.ec2_key.private_key_pem
  sensitive = true
}