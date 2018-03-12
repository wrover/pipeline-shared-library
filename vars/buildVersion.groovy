/**
 * Определение номера версии в условиях CI/CD.
 * <p>Определяет, является ли сборка снепшотом или релизом (хот-фиксом) и собирает номер версии.
 * <ul>
 * <li>Приоритет имеет тег, который всегда означает "фиксированный" релиз. Версия есть сам тег, например: 5.6, 5.6RC1.
 * Версия в build.gradle должна совпадать с тегом
 * <li>Если тега нет, смотрим на ветку. Если это мастер, то это настоящий CD-release. Поскольку мы не ставим себе задачу
 * сделать эти релизы повторяемыми (может, зря, конечно) используем пока для этого SNAPSHOT. Также вот этот issue
 * не позволяет использовать диапазоны в Студии, не оставляя другого выхода <a href="https://youtrack.cuba-platform.com/issue/STUDIO-4417">STUDIO-4417</a>
 * В результате для master получим: isSnapshot=true, и qualifier '-SNAPSHOT' например: 5.7-SNAPSHOT
 * <li>Релизная ветка, именованная release-MAJOR.MINOR, при отсутствии тега, т.е. хот-фикс. Проверяется правильность
 * версии в build.gradle и в конец добавляется номер сборки, получается: 5.6.43
 * <li>Наконец, все прочие ветки - трактуются как feature-branch и собираются как степшоты и дополнительным qualifier
 * из имени ветки (/ меняется на -). Например: 5.7-feature-cool-feature-SNAPSHOT
 * </ul>
 *
 * @return мапа с полями isSnapshot и version, в случае ошибки проставляется поле error содержащее ошибку
 */
def call(version) {

    def res = [:]

    def buildNumber = env.BUILD_NUMBER
    def branch = env.BRANCH_NAME
    def tag = env.TAG_NAME

    if (tag?.trim()) { // приоритет имеет tag и всегда означает как бы "релиз"
        if (version != tag) {// tag всегда должен совпадать с версией, указанной в build.gradle
            res.error =  "version (${version}) should be consisted with tag (${tag})"
            return res
        }
        // mainline, release
        res.isSnapshot = false
        res.version = version
    } else { // тега нет
        if (branch == 'master') { // mainline, настоящий CD-release
            res.isSnapshot = true
            // Due to CUBA Gradle plugin peculiarity it should be done here
            res.version = version + '-SNAPSHOT'
        } else if (branch ==~ /release-\d+\.\d+/) { // release branch (hot-fix)
            if (version != branch.replaceAll("release-", "")) {
                res.error = "version (${version}) should be consisted with release branch (${branch})"
                return res
            }// release branch всегда должен совпадать с версией, указанной в build.gradle
            // суффикс с номером сборки
            res.isSnapshot = false
            res.version = version + (buildNumber ? '.' + "${buildNumber}" : '')
        } else { // branch by feature
            def mVer = branch.replaceAll("/", "-")
            echo "mVer=${mVer}"
            // Due to CUBA Gradle plugin peculiarity it should be done here
            res.version = version + "-${mVer}" + '-SNAPSHOT'
        }
    }
    res
}