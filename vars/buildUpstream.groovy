/**
 * Собирает все upstream pipelines
 *
 * @param upstreamPipelines список upstream pipeline
 * @return
 */
def call(upstreamPipelines) {

    def params = []
    upstreamPipelines.each { dep ->
        def item = [:]
        item.$class = 'BooleanParameterDefinition'
        item.defaultValue = dep != 'dummy'
        item.name = dep
        item.description = "$dep / ${env.BRANCH_NAME}"
        params.add(item)
    }

    def buildDeps = [:]

    try {
        timeout(time: 60, unit: 'SECONDS') {
            buildDeps = input(id: 'BuildDeps', message: 'Shell I build depended components?',
                    parameters: params)
        }
    } catch (err) { // timeout reached or input false
    }

    upstreamPipelines.each { dep ->
        if (buildDeps[dep])
            stage("Build $dep") {
                // собирает одноименную ветку
                build(job: "/${dep}/${env.BRANCH_NAME}",
                        parameters: [booleanParam(name: 'RUN_AS_DEP', value: true)])
            }
    }
}