pipeline {
    agent any

    tools {
        maven 'Maven-3.9.0'
        jdk 'JDK-17'
    }

    environment {
        // MySQL Configuration
        MYSQL_ROOT_PASSWORD = 'root'
        MYSQL_DATABASE = 'testdb_spring'

        // Docker Configuration
        DOCKER_IMAGE_NAME = 'room-reservation-auth-service'
        DOCKER_IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout([
                        $class: 'GitSCM',
                        branches: [[name: '*/main']],
                        doGenerateSubmoduleConfigurations: false,
                        extensions: [[$class: 'CleanCheckout']],
                        submoduleCfg: [],
                        userRemoteConfigs: [[
                                                    credentialsId: 'git_Jenk',
                                                    url: 'https://github.com/ZakariaRek/room-reservation-system-auth-service'
                                            ]]
                ])
            }
        }

        stage('Start MySQL Container') {
            steps {
                script {
                    sh '''
                        # Create a network if it doesn't exist
                        docker network create app-network || true
                        
                        # Stop and remove any existing MySQL container
                        docker stop mysql-db || true
                        docker rm mysql-db || true
                        
                        # Start MySQL container
                        docker run -d \
                            --name mysql-db \
                            --network app-network \
                            -e MYSQL_ROOT_PASSWORD=root \
                            -e MYSQL_DATABASE=testdb_spring \
                            -p 3306:3306 \
                            mysql:8.0
                        
                        # Wait for MySQL to be ready
                        echo "Waiting for MySQL to be ready..."
                        for i in {1..30}; do
                            if docker exec mysql-db mysqladmin ping -h localhost -u root -proot --silent; then
                                echo "MySQL is ready!"
                                break
                            fi
                            echo "Waiting for MySQL..."
                            sleep 2
                        done
                    '''
                }
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Package Application') {
            steps {
                sh 'mvn package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh """
                        docker build -t ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} .
                        docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ${DOCKER_IMAGE_NAME}:latest
                    """
                }
            }
        }

        stage('Test Docker Container') {
            steps {
                script {
                    sh '''
                        # Run the application container (single line)
                        docker run -d --name auth-service-test --network app-network -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql-db:3306/testdb_spring?useSSL=false&allowPublicKeyRetrieval=true -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=root -p 8083:8083 ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}
                        
                        # Wait for application to start
                        sleep 30
                        
                        # Check logs
                        docker logs auth-service-test
                        
                        # Test if application is running
                        curl -f http://localhost:8083/api/auth/signin || echo "API test failed"
                        
                        # Cleanup
                        docker stop auth-service-test || true
                        docker rm auth-service-test || true
                    '''
                }
            }
        }

        stage('Security Scan with Trivy') {
            steps {
                script {
                    sh """
                        docker run --rm \
                            -v /var/run/docker.sock:/var/run/docker.sock \
                            aquasec/trivy:latest image \
                            --exit-code 0 \
                            --severity HIGH,CRITICAL \
                            ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}
                    """
                }
            }
        }
    }

    post {
        always {
            script {
                // Clean up containers
                sh """
                    docker stop mysql-db || true
                    docker rm mysql-db || true
                    docker network rm app-network || true
                    docker image prune -f || true
                """
            }
            cleanWs()
        }

        success {
            script {
                emailext(
                        subject: "Jenkins Build Success: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                        body: """
                        <h2>Build Success</h2>
                        <p>The build was successful!</p>
                        <ul>
                            <li>Job: ${env.JOB_NAME}</li>
                            <li>Build Number: ${env.BUILD_NUMBER}</li>
                            <li>Docker Image: ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}</li>
                            <li>Build URL: ${env.BUILD_URL}</li>
                        </ul>
                    """,
                        to: 'zakariaest49@gmail.com',
                        mimeType: 'text/html'
                )
            }
        }

        failure {
            script {
                emailext(
                        subject: "Jenkins Build Failed: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                        body: """
                        <h2>Build Failed</h2>
                        <p>The build has failed!</p>
                        <ul>
                            <li>Job: ${env.JOB_NAME}</li>
                            <li>Build Number: ${env.BUILD_NUMBER}</li>
                            <li>Build URL: ${env.BUILD_URL}</li>
                        </ul>
                        <p>Please check the console output for details.</p>
                    """,
                        to: 'zakariaest49@gmail.com',
                        mimeType: 'text/html'
                )
            }
        }
    }
}