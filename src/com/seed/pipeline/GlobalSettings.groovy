package com.seed.pipeline

/**
 * Глобальные настройки, которые можно использовать во всех проектах.
 */
class GlobalSettings {

    // Docker registry settings
    public static final String DOCKER_REGISTRY = 'ci3.iseed.pw:5000'
    public static final String DOCKER_REGISTRY_URL = "http://$DOCKER_REGISTRY"
    public static final String DOCKER_CREDENTIALS = 'seed.registry'

}
