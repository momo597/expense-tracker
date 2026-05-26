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

        stage('Unit Testing'){
            steps{
                sh "mvn clean test"
            }
        }

         stage('Integration Testing'){
            environment{
                DOCKER_HOST = 'tcp://host.docker.internal:2375'
                TESTCONTAINERS_HOST_OVERRIDE = 'host.docker.internal'
                TESTCONTAINERS_RYUK_DISABLED = 'true'
            }
            steps{
                sh "mvn verify -DskipSurefireTests"
            }
        }

        stage('Docker Build'){
            environment{
                DOCKER_HOST = 'tcp://host.docker.internal:2375'
            }
            steps{
                script{
                    docker.build("expense-tracker:${env.BUILD_NUMBER}")
                }
            }
        }
    }

    post{
        always{
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml, **/target/failsafe-reports/*.xml'
        }
    }
}