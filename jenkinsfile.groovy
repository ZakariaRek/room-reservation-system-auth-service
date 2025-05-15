pipeline {
    agent any

    tools {
        maven 'Maven-3.9.0' // Make sure this matches your Jenkins Maven installation name
        jdk 'JDK-17'       // Make sure this matches your Jenkins JDK installation name
    }

    environment {
        // MySQL Configuration
        MYSQL_ROOT_PASSWORD = 'root'
        MYSQL_DATABASE = 'testdb_spring'

        // Application Configuration
        SPRING_DATASOURCE_URL = 'jdbc:mysql://localhost:3306/testdb_spring?useSSL=false&allowPublicKeyRetrieval=true'
        SPRING_DATASOURCE_USERNAME = 'root'
        SPRING_DATASOURCE_PASSWORD = ''
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
                                                    credentialsId: 'git_Jenk', // Configure this in Jenkins
                                                    url: 'https://github.com/ZakariaRek/room-reservation-system-auth-service'
                                            ]]
                ])
            }
        }

        stage('Setup MySQL') {
            steps {
                script {
                    // Using Docker to run MySQL for tests
                    sh '''
                        docker run -d \
                            --name mysql-test-${BUILD_NUMBER} \
                            -e MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD} \
                            -e MYSQL_DATABASE=${MYSQL_DATABASE} \
                            -p 3306:3306 \
                            mysql:8.0
                        
                        # Wait for MySQL to be ready
                        sleep 30
                        
                        # Check if MySQL is ready
                        for i in {1..30}; do
                            if docker exec mysql-test-${BUILD_NUMBER} mysqladmin ping -h localhost -u root -proot --silent; then
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

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
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
            // Cleanup MySQL container
            sh 'docker stop mysql-test-${BUILD_NUMBER} || true'
            sh 'docker rm mysql-test-${BUILD_NUMBER} || true'

            // Clean workspace
            cleanWs()
        }

        success {
            script {
                // Send notification on success
                emailext(
                        subject: "Jenkins Build Success: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                        body: """
                        <h2>Build Success</h2>
                        <p>The build was successful!</p>
                        <ul>
                            <li>Job: ${env.JOB_NAME}</li>
                            <li>Build Number: ${env.BUILD_NUMBER}</li>
                            <li>Branch: ${env.BRANCH_NAME}</li>
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
                // Send notification on failure
                emailext(
                        subject: "Jenkins Build Failed: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                        body: """
                        <h2>Build Failed</h2>
                        <p>The build has failed!</p>
                        <ul>
                            <li>Job: ${env.JOB_NAME}</li>
                            <li>Build Number: ${env.BUILD_NUMBER}</li>
                            <li>Branch: ${env.BRANCH_NAME}</li>
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