/**
 * Интерактивный promote
 */
def call(promotionTargets, imageTag, version, dockerRegistry) {

    // собираем параметры
    def params = []
    promotionTargets.each { targetKey, target ->
        def item = [:]
        item.$class = 'BooleanParameterDefinition'
        item.defaultValue = targetKey != 'dummy'
        item.name = targetKey
        item.description = target['description']
        params.add(item)
    }

    def targets = [:]
    try {
        timeout(time: 180, unit: 'SECONDS') {
            targets = input(id: 'Promote', message: 'Let\'s promote to Docker targets?',
                    parameters: params)
        }
    } catch (err) { // timeout reached or input false
    }

    targets.each { k, v ->
        if (v)
            stage(k) {
                target = promotionTargets[k]
                deployDockerSimple(imageTag, version, dockerRegistry, target)
            }
    }
}