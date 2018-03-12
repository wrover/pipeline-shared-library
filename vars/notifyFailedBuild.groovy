def call() {
    mail body: "project build error is here: ${env.BUILD_URL}",
            from: 'jenkins@beas.ru',
            replyTo: 'to@wildrover.ru',
            subject: 'project build failed',
            to: 'ilya.rodionov@gmail.com'
}