pipeline {
    agent any
    parameters {
        string(name: 'GRADLE_EXTRA_OPTION', defaultValue: params.GRADLE_EXTRA_OPTION ?:'--stacktrace', description:'gradle extra option')
        string(name: 'PROJECT_VERSION', defaultValue: params.PROJECT_VERSION ?:'1.0.0-SNAPSHOT')
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
                cleanWs()
                checkout scm
                sh "./gradlew publish -x test -DprojectVersion=1.0.0-SNAPSHHT -DmavenPublishUrl=${MAVEN_PUBLISH_URL} -DmavenUsername=${MAVEN_CREDENTIALS_USR} -DmavenPassword=${MAVEN_CREDENTIALS_PSW} ${GRADLE_EXTRA_OPTION}"
            }
        }
    }
}

