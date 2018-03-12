/**
 * Возвращает номер версии, как он определен в <code>build.gradle</code>.
 * <p>В <code>extra.gradle</code> должен быть определен task следующего вида
 * <pre>
 * task printVersion {
 *      doLast {
 *            println "BUILD_VERSION ${cuba.artifact.version}"
 *      }
 * }
 * </pre>
 *
 * @return номер версии, как он указан в <code>build.gradle</code>
 */
def call() {
    sh(returnStdout: true, script: "./gradlew printVersion | grep BUILD_VERSION | awk '{print \$2}'").trim()
}