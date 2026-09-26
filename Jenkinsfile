pipeline {
    agent {
        label 'java'
    }

    stages {

        stage('Environment') {
            steps {
                sh '''
                    echo "=== HOSTNAME ==="
                    hostname

                    echo "=== USER ==="
                    whoami

                    echo "=== WORKSPACE ==="
                    pwd

                    echo "=== JAVA ==="
                    java -version
                '''
            }
        }

        stage('Checkout') {
            steps {
                sh '''
                    echo "=== GIT STATUS ==="
                    git status

                    echo "=== CURRENT COMMIT ==="
                    git log -1 --oneline
                '''
            }
        }
    }
}