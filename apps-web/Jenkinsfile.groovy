pipeline {
    agent any
    parameters {
        string(name: 'PROFILE', defaultValue: params.PROFILE ?: 'dev', description: 'profile')
        string(name: 'GRADLE_BUILD_OPTION', defaultValue: params.GRADLE_BUILD_OPTION ?: '--stacktrace', description: 'gradle build option')
        string(name: 'DOCKER_HOST', defaultValue: params.DOCKER_HOST ?: '___.dockerhub.io', description: 'docker host')
        credentials(credentialType: 'com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl',
                name: 'DOCKER_CREDENTIALS',
                defaultValue: params.DOCKER_CREDENTIALS ?: '___',
                description: 'Docker credentials')
        string(name: 'DOCKER_REPOSITORY', defaultValue: params.DOCKER_REPOSITORY ?: '___/___', description: 'docker repository')
        string(name: 'SERVICE_PORT', defaultValue: params.SERVICE_PORT, description: 'service port')
    }
    stages {
        stage("build") {
            environment {
                DOCKER_CREDENTIALS = credentials('DOCKER_CREDENTIALS')
            }
            steps {
                cleanWs()
                checkout scm
                sh "./gradlew :apps-web:build -x test --refresh-dependencies -DincludeSubmodule=true ${GRADLE_BUILD_OPTION}"
                sh '''
                    # docker builds and push
                    cd apps-web
                    echo ${DOCKER_CREDENTIALS_PSW} | sudo docker login --username ${DOCKER_CREDENTIALS_USR} --password-stdin ${DOCKER_HOST}
                    sudo docker rmi $(sudo docker images ${DOCKER_REPOSITORY} -q) || true
                    sudo docker build -t ${DOCKER_HOST}/${DOCKER_REPOSITORY}:${PROFILE} .
                    sudo docker push ${DOCKER_HOST}/${DOCKER_REPOSITORY}:${PROFILE}
                '''.stripIndent()
            }
        }
        stage("deploy") {
            steps {
                sh '''
                    cat <<EOF | kubectl apply -f -
                    apiVersion: apps/v1
                    kind: Deployment
                    metadata:
                      name: apps-web 
                    spec:
                      selector:
                        matchLabels:
                          app: apps-web 
                      replicas: 2 
                      template:
                        metadata:
                          labels:
                            app: apps-web
                        spec:
                          containers:
                          - name: apps-web
                            image: "${DOCKER_HOST}/${DOCKER_REPOSITORY}:${PROFILE}"
                            imagePullPolicy: Always
                            ports:
                            - containerPort: 8080
                            env:
                            - name: SPRING_PROFILES_ACTIVE
                              value: "${PROFILE}"
                    ---
                    apiVersion: v1
                    kind: Service
                    metadata:
                      name: apps-web
                    spec:
                      type: LoadBalancer 
                      selector:
                        app: apps-web
                      ports:
                        - protocol: TCP
                          port: ${SERVICE_PORT} 
                          targetPort: 8080
                    EOF
                    
                    # print status
                    kubectl get pods,services,ingress
                '''.stripIndent()
                sh("kubectl rollout restart deployment/apps-web")
           }
        }
    }
}