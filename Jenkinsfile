pipeline {
    agent any

    triggers {
        pollSCM('H/5 * * * *')
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    environment {
        CI_IMAGE = 'idcard-management-ci'
        TEST_PROFILE = 'test'
        FAILURE_TO = 'samnangalex123@gmail.com'
        FAILURE_CC = 'srengty@gmail.com'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify DB Profiles') {
            steps {
                script {
                    if (isUnix()) {
                        sh '''
                            mkdir -p jenkins-output
                            grep -q 'jdbc:mysql' src/main/resources/application.properties
                            grep -q 'jdbc:sqlite' src/test/resources/application-test.properties
                            {
                              echo 'Production DB: MySQL'
                              grep 'spring.datasource.url' src/main/resources/application.properties
                              echo 'Test DB: SQLite'
                              grep 'spring.datasource.url' src/test/resources/application-test.properties
                            } | tee jenkins-output/db-profile-check.log
                        '''
                    } else {
                        powershell '''
                            New-Item -ItemType Directory -Force -Path jenkins-output | Out-Null
                            $prodIsMysql = Select-String -Path src/main/resources/application.properties -Pattern 'jdbc:mysql' -Quiet
                            if (-not $prodIsMysql) { throw 'Production datasource is not MySQL' }
                            $testIsSqlite = Select-String -Path src/test/resources/application-test.properties -Pattern 'jdbc:sqlite' -Quiet
                            if (-not $testIsSqlite) { throw 'Test datasource is not SQLite' }
                            @(
                              'Production DB: MySQL'
                              (Select-String -Path src/main/resources/application.properties -Pattern 'spring.datasource.url').Line
                              'Test DB: SQLite'
                              (Select-String -Path src/test/resources/application-test.properties -Pattern 'spring.datasource.url').Line
                            ) | Tee-Object -FilePath jenkins-output/db-profile-check.log
                        '''
                    }
                }
            }
        }

        stage('Prepare Maven Builder') {
            steps {
                script {
                    if (isUnix()) {
                        sh '''
                            docker build -t "$CI_IMAGE" . > jenkins-output/docker-build.log 2>&1
                            cat jenkins-output/docker-build.log
                        '''
                    } else {
                        powershell '''
                            docker build -t $env:CI_IMAGE . 2>&1 | Tee-Object -FilePath jenkins-output/docker-build.log
                            if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
                        '''
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh '''
                            docker run --rm \
                              -v "$PWD:/workspace" \
                              -v "$PWD/.m2-jenkins:/root/.m2" \
                              -w /workspace \
                              "$CI_IMAGE" \
                              mvn -B clean package -DskipTests \
                              > jenkins-output/build-output.log 2>&1
                            cat jenkins-output/build-output.log
                        '''
                    } else {
                        powershell '''
                            docker run --rm `
                              -v "${env:WORKSPACE}:/workspace" `
                              -v "${env:WORKSPACE}\\.m2-jenkins:/root/.m2" `
                              -w /workspace `
                              $env:CI_IMAGE `
                              mvn -B clean package -DskipTests `
                              2>&1 | Tee-Object -FilePath jenkins-output/build-output.log
                            if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
                        '''
                    }
                }
            }
        }

        stage('Test With SQLite') {
            steps {
                script {
                    if (isUnix()) {
                        sh '''
                            docker run --rm \
                              -e SPRING_PROFILES_ACTIVE="$TEST_PROFILE" \
                              -v "$PWD:/workspace" \
                              -v "$PWD/.m2-jenkins:/root/.m2" \
                              -w /workspace \
                              "$CI_IMAGE" \
                              mvn -B test \
                              > jenkins-output/test-output.log 2>&1
                            cat jenkins-output/test-output.log
                        '''
                    } else {
                        powershell '''
                            docker run --rm `
                              -e SPRING_PROFILES_ACTIVE=$env:TEST_PROFILE `
                              -v "${env:WORKSPACE}:/workspace" `
                              -v "${env:WORKSPACE}\\.m2-jenkins:/root/.m2" `
                              -w /workspace `
                              $env:CI_IMAGE `
                              mvn -B test `
                              2>&1 | Tee-Object -FilePath jenkins-output/test-output.log
                            if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
                        '''
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Deploy With Ansible') {
            steps {
                script {
                    if (isUnix()) {
                        sh '''
                            ansible-playbook -i ansible/inventory.ini ansible/web-deploy.yml \
                              > jenkins-output/ansible-deploy-output.log 2>&1
                            cat jenkins-output/ansible-deploy-output.log
                        '''
                    } else {
                        powershell '''
                            $wslWorkspace = (wsl wslpath -a "$env:WORKSPACE").Trim()
                            wsl bash -lc "cd '$wslWorkspace' && ansible-playbook -i ansible/inventory.ini ansible/web-deploy.yml" `
                              2>&1 | Tee-Object -FilePath jenkins-output/ansible-deploy-output.log
                            if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
                        '''
                    }
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts allowEmptyArchive: true, artifacts: 'target/*.jar,target/surefire-reports/**,jenkins-output/**,artifacts/backup.sql'
        }

        failure {
            emailext(
                subject: "Build failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
Build failed for ${env.JOB_NAME} #${env.BUILD_NUMBER}.

Project: ${env.JOB_NAME}
Build URL: ${env.BUILD_URL}

The build log is attached.
""",
                to: "${env.FAILURE_TO}, cc:${env.FAILURE_CC}",
                recipientProviders: [developers(), culprits()],
                attachLog: true,
                compressLog: true
            )
        }
    }
}
