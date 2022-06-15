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
                sh "./gradlew :apps-web:build -x test ${GRADLE_BUILD_OPTION}"
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
                                #image: 192.168.0.2:9996/apps-web:dev
                                image: ${DOCKER_HOST}/${DOCKER_REPOSITORY}:${PROFILE}
                                ports:
                                - containerPort: 8080
                                env:
                                - name: SPRING_PROFILES_ACTIVE
                                  value: "local"
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
                              port: 10000 
                              targetPort: 8080
                    EOF
                '''



//                sh '''
//                    cd apps-web
//                    kubectl apply -f ./Deployment.yml
//                    kubectl get pods,services
//                    kubectl port-forward --address 0.0.0.0 service/apps-web 10000:10000 || true
//                '''.stripIndent()
           }
        }
    }
}