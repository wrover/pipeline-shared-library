/**
 * Выполняет сборку всех downstream pipeline.
 * Сборка выполняется по очереди для каждой группы, в пределах одной группы - параллельно.
 *
 * @return ничего
 */
def call(downstreamPipelines) {
    if (env.BRANCH_NAME == 'master')
        downstreamPipelines.each { group ->
            def groupPipelines = [:]
            group.each { pipeline ->
                groupPipelines[pipeline] = {
                    build(job: "/${pipeline}/${env.BRANCH_NAME}",
                            parameters: [booleanParam(name: 'RUN_AS_DEP', value: true)])
                }
            }
            groupPipelines.failFast = true
            parallel groupPipelines
        }
}