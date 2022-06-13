pipeline {
    agent any
    parameters {
        string(name: 'STAGE', defaultValue: params.STAGE ?: 'dev', description: 'stage')
        string(name: 'GRADLE_EXTRA_OPTION', defaultValue: params.GRADLE_EXTRA_OPTION ?: '--init-script init.gradle --stacktrace', description: 'gradle extra option')
        string(name: 'DOCKER_HOST', defaultValue: params.DOCKER_HOST ?: '__.docker.io', description: 'Docker host')
        string(name: 'DOCKER_IMAGE', defaultValue: params.DOCKER_IMAGE ?: '___', description: 'Docker image repository')
        credentials(credentialType: 'com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl',
                name: 'DOCKER_CREDENTIALS',
                defaultValue: params.DOCKER_CREDENTIALS ?: '___',
                description: 'Docker credentials')
    }
    stages {
        stage('publish') {
            environment {
                DOCKER_CREDENTIALS = credentials('DOCKER_CREDENTIALS')
            }
            steps {
                cleanWs()
                checkout scm
                sh "./gradlew :apps-web:build -x test ${GRADLE_EXTRA_OPTION}"
                sh '''        
                    # docker builds and push
                    cd apps-web
                    echo ${DOCKER_CREDENTIALS_PSW} | sudo docker login --username ${DOCKER_CREDENTIALS_USR} --password-stdin ${DOCKER_HOST}
                    sudo docker rmi $(sudo docker images ${DOCKER_IMAGE} -q) || true
                    sudo docker build -t ${DOCKER_HOST}/${DOCKER_IMAGE}:${STAGE} .
                    sudo docker push ${DOCKER_HOST}/${DOCKER_IMAGE}:${STAGE}
                '''.stripIndent()
            }
        }
    }
}