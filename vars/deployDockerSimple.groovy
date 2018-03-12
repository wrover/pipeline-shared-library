/**
 * Выполняет простой вид деплоя через SSH на машину с установленным Docker.
 *
 * @param imageTag  имя образа
 * @param version   мапа, которую отдает buildVersion
 * @param dockerRegistry    имя реестра
 * @param target    мапа с описанием целевого хоста. Пример:
 *
 *
 */
def call(imageTag, version, dockerRegistry, target) {

    def path = imageTag
    def credentials = target['credentials']
    def host = target['hostname']
    def user = target['username']
    def file = target['file']

    def imageVersion

    if (version.isSnapshot)
        imageVersion = 'snapshot'
    else
        imageVersion = 'latest'

    sshagent(credentials: [credentials]) {

        sh "ssh -o StrictHostKeyChecking=no ${user}@${host} \"uname -a\""

        try {
            sh "ssh -o StrictHostKeyChecking=no ${user}@${host} \"docker-compose -f ~/${path}/${file} down\""
        } catch (err) {
            // it's normal during first-time deployment
            echo err.toString()
        }

        // не смог сходу придумать такой вариант, чтобы работало и 0 всегда возвращало
        //sh "ssh -o StrictHostKeyChecking=no ${user}@${host} \"[ ! -d ~/${path} ] || mkdir -pv ~/${path}\""
        sh "ssh -o StrictHostKeyChecking=no ${user}@${host} \"rm -rf ~/${path}; mkdir -pv ~/${path}\""

        sh "scp -o StrictHostKeyChecking=no ${file} ${user}@${host}:~/${path}/"

        sh "ssh -o StrictHostKeyChecking=no ${user}@${host} \"docker pull ${dockerRegistry}/${imageTag}:${imageVersion}\""

        sh "ssh -o StrictHostKeyChecking=no ${user}@${host} \"docker tag ${dockerRegistry}/${imageTag}:${imageVersion} ${dockerRegistry}/${imageTag}:deploy\""

        sh "ssh -o StrictHostKeyChecking=no ${user}@${host} \"docker-compose -f ~/${path}/${file} up -d\""
    }
}