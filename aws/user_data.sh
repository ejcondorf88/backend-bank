#!/bin/bash
set -e

echo "======================================"
echo "Instalando Docker y dependencias..."
echo "======================================"

# Update system
sudo dnf update -y

# Install Git
sudo dnf install -y git

# Install Docker
sudo dnf install -y docker
sudo systemctl enable docker
sudo systemctl start docker

# Install Docker Compose (standalone version)
sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.6/docker-compose-$(uname -s)-$(uname -m)" \
-o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Install Docker Buildx
sudo mkdir -p /usr/local/lib/docker/cli-plugins
sudo curl -L "https://github.com/docker/buildx/releases/download/v0.17.1/buildx-v0.17.1.linux-amd64" \
-o /usr/local/lib/docker/cli-plugins/docker-buildx
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-buildx

# Add ec2-user to docker group
sudo usermod -aG docker ec2-user

echo "======================================"
echo "Clonando repositorio..."
echo "======================================"

# Clone the repository
cd /home/ec2-user
# Extract directory name from repo_url
REPO_DIR=$(basename "${repo_url}" .git)

if [ -d "$REPO_DIR" ]; then
    echo "Directorio ya existe, actualizando..."
    cd "$REPO_DIR"
    sudo -u ec2-user git fetch
    sudo -u ec2-user git checkout "${repo_branch}"
    sudo -u ec2-user git pull
else
    sudo -u ec2-user git clone "${repo_url}"
    cd "$REPO_DIR"
    sudo -u ec2-user git checkout "${repo_branch}"
fi

# Setup environment file
if [ ! -f ".env" ]; then
    sudo -u ec2-user cp .env.example .env
fi

# Fix permissions
sudo chown -R ec2-user:ec2-user /home/ec2-user/"$REPO_DIR"

# Remove version line from docker-compose.yml to avoid warning
sudo sed -i '/^version:/d' docker-compose.yml

echo "======================================"
echo "Verificando instalación..."
echo "======================================"
docker --version
docker-compose --version
docker buildx version

echo "======================================"
echo "Iniciando contenedores..."
echo "======================================"

# Build and start containers
# Using docker-compose.yml since .prod.yml might not exist
sudo docker-compose up -d --build

echo "======================================"
echo "Verificando estado de contenedores..."
echo "======================================"
sleep 10
sudo docker-compose ps

echo "======================================"
echo "¡Instalación completada!"
echo "======================================"
echo ""
echo "IMPORTANTE: Cierra sesión y vuelve a entrar para usar Docker sin sudo:"
echo "  exit"
echo ""
echo "Servicios disponibles en:"
echo "  - API Gateway: http://$(curl -s ifconfig.me):8080"
echo "  - ms-customer: http://$(curl -s ifconfig.me):8081"
echo "  - ms-account: http://$(curl -s ifconfig.me):8082"
echo "  - RabbitMQ Management: http://$(curl -s ifconfig.me):15672"
echo ""
echo "Para ver logs: sudo docker-compose logs -f"
echo "Para ver estado: sudo docker-compose ps"
echo "======================================"