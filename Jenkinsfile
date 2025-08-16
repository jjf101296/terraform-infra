pipeline {
    agent any

    stages {
        stage('Terraform Init') {
            steps {
                sh 'terraform init -reconfigure -backend-config=bucket=my-terraform-state-bucket-john -backend-config=key=terraform.tfstate -backend-config=region=ap-south-1'
            }
        }

        stage('Terraform Plan') {
            steps {
                sh 'terraform plan -out=tfplan'
            }
        }

        stage('Terraform Apply') {
            steps {
                sh 'terraform apply -auto-approve tfplan'
            }
        }
    }
}
