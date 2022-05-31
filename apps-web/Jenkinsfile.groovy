pipeline {
    agent any
    parameters {
        string(name: 'STAGE', defaultValue: params.STAGE ?:'dev', description: 'stage')
        string(name: 'DOCKER_PUBLISH_URL', defaultValue: params.MAVEN_PUBLISH_URL ?:'http://___/repository/maven-snapshot/', description: 'Maven publish URL')
        credentials(credentialType: 'com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl',
                name: 'DOCKER_CREDENTIALS',
                defaultValue: params.DOCKER_CREDENTIALS ?:'___',
                description: 'Docker credentials')
    }
    stages {
        stage('publish') {
            environment {
                DOCKER_CREDENTIALS = credentials('DOCKER_CREDENTIALS')
            }
            steps {
                sh "./gradlew build -x test --stacktrace"
            }
        }
    }
}