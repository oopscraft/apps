pipeline {
    agent any
    parameters {
        string(name: 'STAGE', defaultValue: params.STAGE ?: 'dev', description: 'stage')
        string(name: 'PROFILE', defaultValue: params.PROFILE ?: 'dev', description: 'profile')
        string(name: 'GRADLE_BUILD_OPTION', defaultValue: params.GRADLE_BUILD_OPTION ?: '--init-script init.gradle --stacktrace', description: 'gradle build option')
        string(name: 'DOCKER_HOST', defaultValue: params.DOCKER_HOST ?: '___.dockerhub.io', description: 'docker host')
        credentials(credentialType: 'com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl',
                name: 'DOCKER_CREDENTIALS',
                defaultValue: params.DOCKER_CREDENTIALS ?: '___',
                description: 'Docker credentials')
        string(name: 'DOCKER_REPOSITORY', defaultValue: params.DOCKER_REPOSITORY ?: '___/___', description: 'docker repository')
    }
    stages {
        stage("build") {
            environment {
                DOCKER_CREDENTIALS = credentials('DOCKER_CREDENTIALS')
            }
            steps {
                cleanWs()
                checkout scm
                sh "./gradlew :apps-batch:build -x test -DincludeSubmodule=true ${GRADLE_BUILD_OPTION}"
                sh '''
                    # docker builds and push
                    cd apps-batch
                    echo ${DOCKER_CREDENTIALS_PSW} | sudo docker login --username ${DOCKER_CREDENTIALS_USR} --password-stdin ${DOCKER_HOST}
                    sudo docker rmi $(sudo docker images ${DOCKER_REPOSITORY} -q) || true
                    sudo docker build -t ${DOCKER_HOST}/${DOCKER_REPOSITORY}:${PROFILE} .
                    sudo docker push ${DOCKER_HOST}/${DOCKER_REPOSITORY}:${PROFILE}
                '''.stripIndent()
            }
        }
    }
}