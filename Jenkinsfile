pipeline {
    agent any

    environment {
        NAUKRI_USER = credentials('naukri_user')
        NAUKRI_PASS = credentials('naukri_pass')
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/purna17217/DailyNaukari-Update'
            }
        }

        stage('Setup Maven') {
            steps {
                echo "Using Maven"
            }
        }

        stage('Install Dependencies') {
            steps {
                sh "mvn clean install -DskipTests"
            }
        }

        stage('Run TestNG Automation') {
            steps {
                sh "mvn test"
            }
        }

    }

    post {
        always {
            archiveArtifacts artifacts: '**/*.png', allowEmptyArchive: true
            junit 'target/surefire-reports/*.xml'
        }
    }
}
