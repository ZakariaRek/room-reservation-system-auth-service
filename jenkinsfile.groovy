pipeline {
    agent {
        docker {
            image 'maven:3.8.5-openjdk-17'
            args '-v /root/.m2:/root/.m2'
        }
    }
    
    environment {
        DOCKER_REGISTRY = 'zakariarekhla' // Change to your Docker registry
        DOCKER_IMAGE_BACKEND = "${DOCKER_REGISTRY}/reservation-backend"
        DOCKER_IMAGE_TAG = "${env.BUILD_NUMBER}"
        DOCKERHUB_CREDENTIALS = credentials('dockerhub') // Configure this in Jenkins credentials
    }
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/TEST-*.xml'
                }
            }
        }
        
//        stage('SonarQube Analysis') {
//            steps {
//                withSonarQubeEnv('SonarQube') {
//                    sh 'mvn sonar:sonar'
//                }
//            }
//        }
//
        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE_BACKEND}:${DOCKER_IMAGE_TAG} ."
                    sh "docker tag ${DOCKER_IMAGE_BACKEND}:${DOCKER_IMAGE_TAG} ${DOCKER_IMAGE_BACKEND}:latest"
                }
            }
        }
        
        stage('Push Docker Image') {
            steps {
                script {
                    sh "echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin"
                    sh "docker push ${DOCKER_IMAGE_BACKEND}:${DOCKER_IMAGE_TAG}"
                    sh "docker push ${DOCKER_IMAGE_BACKEND}:latest"
                }
            }
        }
        
//        stage('Deploy to Development') {
//            when {
//                branch 'develop'
//            }
//            steps {
//                script {
//                    // Deploy to development environment using docker-compose
//                    sh 'scp docker-compose.yml user@dev-server:/path/to/deployment/'
//                    sh 'ssh user@dev-server "cd /path/to/deployment && docker-compose pull && docker-compose up -d"'
//                }
//            }
//        }
        
//        stage('Deploy to Production') {
//            when {
//                branch 'main'
//            }
//            steps {
//                // Production deployment requires manual approval
//                input message: 'Deploy to production?', ok: 'Yes'
//                script {
//                    // Deploy to production environment
//                    sh 'scp docker-compose.yml user@prod-server:/path/to/deployment/'
//                    sh 'ssh user@prod-server "cd /path/to/deployment && docker-compose pull && docker-compose up -d"'
//                }
//            }
//        }
    }
    
    post {
        always {
            // Clean up Docker images
            sh "docker rmi ${DOCKER_IMAGE_BACKEND}:${DOCKER_IMAGE_TAG}"
            sh "docker rmi ${DOCKER_IMAGE_BACKEND}:latest"
        }
        success {
            echo 'Build and deployment successful!'
        }
        failure {
            echo 'Build or deployment failed!'
            // You can add notification steps here (email, Slack, etc.)
        }
    }
}