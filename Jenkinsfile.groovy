pipeline {
    agent any
    parameters {
        string(name: 'STAGE', defaultValue: params.STAGE ?:'dev', description: 'stage')
        string(name: 'MAVEN_PUBLISH_URL', defaultValue: params.MAVEN_PUBLISH_URL ?:'http://___/repository/maven-snapshot/', description: 'Maven publish URL')
        credentials(credentialType: 'com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl',
                name: 'MAVEN_CREDENTIALS',
                defaultValue: params.MAVEN_CREDENTIALS ?:'___',
                description: 'Maven credentials')
    }
    stages {
        stage('publish') {
            environment {
                MAVEN_CREDENTIALS = credentials('MAVEN_CREDENTIALS')
            }
            steps {
                sh "./gradlew --init-script init.gradle publish --stacktrace -PmavenPublishUrl=${MAVEN_PUBLISH_URL} -PmavenUsername=${MAVEN_CREDENTIALS_USR} -PmavenPassword=${MAVEN_CREDENTIALS_PSW}"
            }
        }
    }
}