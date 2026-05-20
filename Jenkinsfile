pipeline{
    agent any

    tools{
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }

    stages{
        stage('Checkout'){
            steps{
                checkout scm
            }
        }

        stage('Build and Test'){
            steps{
                sh "docker build -t expense-tracker:${env.BUILD_NUMBER} ."
            }
        }
    }

    post{
        always{
            junit '**/target/surefire-reports/*.xml'
        }
    }
}