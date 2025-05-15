pipeline {
    agent any

    tools {
        maven 'Maven-3.9.0'
        jdk 'JDK-17'
    }

    environment {
        // MySQL Configuration (using docker-compose MySQL on port 3307)
        MYSQL_ROOT_PASSWORD = 'root'
        MYSQL_DATABASE = 'testdb_spring'
        MYSQL_HOST = 'mysql-db'

        // Application Configuration
        SPRING_DATASOURCE_URL = 'jdbc:mysql://mysql-db:3307/testdb_spring?useSSL=false&allowPublicKeyRetrieval=true'
        SPRING_DATASOURCE_USERNAME = 'root'
        SPRING_DATASOURCE_PASSWORD = ''

        // Docker Configuration
        DOCKER_REGISTRY = '' // Add your registry if needed
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

        stage('Build with Maven') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    // Use the MySQL from docker-compose
                    sh '''
                        # Update datasource URL to use docker-compose MySQL
                        export SPRING_DATASOURCE_URL='jdbc:mysql://mysql-db:3307/testdb_spring?useSSL=false&allowPublicKeyRetrieval=true'
                        
                        # Run tests
                        mvn test
                    '''
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
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

        stage('Security Scan with Trivy') {
            steps {
                script {
                    sh """
                        # Use Trivy from docker-compose to scan the image
                        docker run --rm \
                            --network jenkins-network \
                            -v /var/run/docker.sock:/var/run/docker.sock \
                            aquasec/trivy:latest image \
                            --exit-code 0 \
                            --severity HIGH,CRITICAL \
                            ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}
                    """
                }
            }
        }

        stage('Test Docker Container') {
            steps {
                script {
                    sh """
                        # Run the container on the same network as MySQL
                        docker run -d \
                            --name test-app-${BUILD_NUMBER} \
                            --network jenkins-network \
                            -e SPRING_DATASOURCE_URL='jdbc:mysql://mysql-db:3307/testdb_spring?useSSL=false&allowPublicKeyRetrieval=true' \
                            -e SPRING_DATASOURCE_USERNAME=root \
                            -e SPRING_DATASOURCE_PASSWORD=root \
                            -p 8081:8083 \
                            ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}
                        
                        # Wait for application to start
                        sleep 30
                        
                        # Test if application is running
                        curl http://localhost:8081/api/auth/signin || true
                        
                        # Stop test container
                        docker stop test-app-${BUILD_NUMBER}
                        docker rm test-app-${BUILD_NUMBER}
                    """
                }
            }
        }

        stage('Push to Registry') {
            when {
                branch 'main'
            }
            steps {
                script {
                    if (env.DOCKER_REGISTRY) {
                        withCredentials([usernamePassword(credentialsId: 'docker-registry-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                            sh """
                                echo ${DOCKER_PASS} | docker login ${DOCKER_REGISTRY} -u ${DOCKER_USER} --password-stdin
                                docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}
                                docker push ${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}
                                docker push ${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:latest
                            """
                        }
                    }
                }
            }
        }

        stage('Code Quality Analysis') {
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                    sh 'mvn checkstyle:checkstyle'
                }
            }
        }
    }

    post {
        always {
            script {
                // Clean up any test containers
                sh """
                    docker rm -f test-app-${BUILD_NUMBER} || true
                    docker image prune -f || true
                """
            }

            // Clean workspace
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