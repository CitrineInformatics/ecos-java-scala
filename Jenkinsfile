pipeline {
    options {
        timeout(time: 1, unit: 'HOURS')
    }

    agent any

    stages {
        stage('Get ecos') {
            steps {
                sh '''
                    git submodule update --init --recursive
                '''
            }
        }
        stage('Tell ECOS to suppress prints') {
            steps {
                sh '''
                    sed -i 's/PRINTLEVEL (2)/PRINTLEVEL (0)/g' ecos/include/glblopts.h
                    sed -i 's/PROFILING (1)/PROFILING (0)/g' ecos/include/glblopts.h
                '''
            }
        }
        stage('Compile and test') {
            steps {
                sh '''
                    export JAVA_HOME="$(readlink -f /usr/lib/jvm/jre)"
                    sbt -Dsbt.log.noformat=true +test
                '''
            }
        }
        stage('Publish to nexus') {
            steps {
                sh 'sbt -Dsbt.log.noformat=true +publish'
            }
        }
    }
}
