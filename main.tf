resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name = "demo-vpc"
  }
}

resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "demo-igw"
  }
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = {
    Name = "demo-public-rt"
  }
}

resource "aws_subnet" "public" {
  for_each                = toset([for i in range(length(var.public_subnets)) : tostring(i)])
  vpc_id                  = aws_vpc.main.id
  cidr_block              = var.public_subnets[tonumber(each.key)]
  availability_zone       = var.azs[tonumber(each.key)]
  map_public_ip_on_launch = true

  tags = {
    Name = "demo-public-${each.key}"
  }
}

resource "aws_route_table_association" "public_assoc" {
  for_each       = aws_subnet.public
  subnet_id      = each.value.id
  route_table_id = aws_route_table.public.id
}
