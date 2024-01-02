pipeline {
    options {
        timeout(time: 1, unit: 'HOURS')
    }

    agent any

    environment {
        JAVA_HOME = sh(script: 'readlink -f /usr/lib/jvm/jre', returnStdout: true).trim()
    }

    stages {
        stage('Build Container') {
            steps {
                sh 'docker build --no-cache -t ecos-java-scala .'
            }
        }


        stage('Test') {
            steps {
                sh 'docker run --rm ecos-java-scala:latest sbt -Dsbt.log.noformat=true +test'
            }
        }

        stage('Publish to nexus') {
            steps {
                sh 'docker run --rm ecos-java-scala:latest sbt -Dsbt.log.noformat=true +publish'
            }
        }
    }
}
