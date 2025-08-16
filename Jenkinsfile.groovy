pipeline {
    agent any

    environment {
        AWS_DEFAULT_REGION = 'ap-south-1'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/jjf101296/terraform-infra.git'
            }
        }

        stage('Terraform Init') {
            steps {
                dir('terraform') {
                    sh '''
                      terraform init -reconfigure \
                        -backend-config="bucket=my-terraform-state-bucket-john" \
                        -backend-config="key=terraform.tfstate" \
                        -backend-config="region=ap-south-1"
                    '''
                }
            }
        }

        stage('Validate') {
            steps {
                dir('terraform') {
                    sh 'terraform validate'
                }
            }
        }

        stage('Plan') {
            steps {
                dir('terraform') {
                    sh 'terraform plan -out=tfplan'
                }
            }
        }

        stage('Approval') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    input message: 'Do you want to apply these changes?', ok: 'Yes, Apply'
                }
            }
        }

        stage('Apply') {
            steps {
                dir('terraform') {
                    sh 'terraform apply -auto-approve tfplan'
                }
            }
        }
    }
}
