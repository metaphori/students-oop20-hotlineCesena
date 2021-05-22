import de.aaschmid.gradle.plugins.cpd.Cpd

val javaFxVersion = "15.0.1"
val fxmodules = listOf("base", "controls", "fxml", "media", "graphics", "swing")
val platforms = listOf("win", "linux", "mac")

plugins {
    java
    application
    eclipse
    id("org.openjfx.javafxplugin") version "0.0.9" // https://github.com/openjfx/javafx-gradle-plugin

    /*
     * Adds tasks to export a runnable jar.
     * In order to create it, launch the "shadowJar" task.
     * The runnable jar will be found in build/libs/projectname-all.jar
     */
    id("com.github.johnrengelman.shadow") version "6.1.0"

    checkstyle
    pmd
    id("de.aaschmid.cpd")
    id("com.github.spotbugs")
    `build-dashboard`
}

repositories {
    jcenter()
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

javafx {
    version = javaFxVersion
    modules("javafx.base", "javafx.controls", "javafx.fxml", "javafx.media", "javafx.graphics", "javafx.swing")
}

dependencies {
    // Google Guava
    implementation("com.google.guava:guava:29.0-jre")

    // Apache Commons
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.apache.commons:commons-io:1.3.2")

    // Cross-platform JavaFX modules
    for (mod in fxmodules) {
        for (plat in platforms) {
            implementation("org.openjfx:javafx-${mod}:${javaFxVersion}:${plat}")
        }
    }

    // Jackson
    implementation("com.fasterxml.jackson:jackson-bom:2.12.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.8")

    // JUnit Jupiter
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    // TestFX for JavaFX testing
    testImplementation("org.testfx:testfx-core:4.0.16-alpha")
    testImplementation("org.testfx:testfx-junit5:4.0.16-alpha")

    // Hamcrest
    testImplementation("org.hamcrest:hamcrest:2.2")
}

application {
    mainClass.set("hotlinecesena.controller.core.Launcher")

    /*
     * mainClassName was deprecated by Gradle, but it is still required by John Engelman's Shadow plugin.
     * A pull request with a fix was already merged, but it hasn't been released yet;
     * see https://github.com/johnrengelman/shadow/issues/609 and https://github.com/johnrengelman/shadow/pull/612
     */
    @Suppress("DEPRECATION")
    mainClassName = mainClass.get()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}


tasks.withType<Test>() {
    ignoreFailures = true
    useJUnitPlatform()
}

spotbugs {
    setEffort("max")
    setReportLevel("low")
    showProgress.set(true)
    val excludeFile = File("${project.rootProject.projectDir}/config/spotbugs/excludes.xml")
    if (excludeFile.exists()) {
        excludeFilter.set(excludeFile)
    }
}

tasks.withType<com.github.spotbugs.snom.SpotBugsTask> {
    ignoreFailures = true
    reports {
        create("html") {
            enabled = true
        }
    }
}

pmd {
    ruleSets = listOf()
    ruleSetConfig = resources.text.fromFile("${project.rootProject.projectDir}/config/pmd/pmd.xml")
    isIgnoreFailures = true
}

cpd {
    isIgnoreFailures = true
}

tasks.withType<Cpd> {
    reports {
        xml.setEnabled(false)
        text.setEnabled(true)
    }
    language = "java"
    minimumTokenCount = 50
    ignoreFailures = true
    source = sourceSets["main"].allJava
}

checkstyle {
    isIgnoreFailures = true
}

typealias QAInfoContainer = Iterable<QAInfo>

interface QAInfo {
    val checker: String
    val lines: IntRange
    val details: String
    val file: String
    val blamedTo: Set<String>
}

fun List<String>.commandOutput(): String {
    val process = ProcessBuilder(this).redirectOutput(ProcessBuilder.Redirect.PIPE).start()
    process.waitFor(1, TimeUnit.MINUTES)
    return process.inputStream.bufferedReader().readText()
}

val authorMatch = Regex("^author\\s+(.+)$")
fun blameFor(file: String, lines: IntRange): Set<String> =
    listOf("git", "blame", "-L", "${lines.start},${lines.endInclusive}", "-p", file)
        .commandOutput().lines()
        .flatMap { line -> authorMatch.matchEntire(line)?.destructured?.toList() ?: emptyList() }
        .toSet()

data class QAInfoForChecker(
    override val checker: String,
    override val file: String,
    override val lines: IntRange = 1..File(file).readText().lines().size,
    override val details: String = "",
    private val blamed: Set<String>? = null,
) : QAInfo {
    override val blamedTo: Set<String> = blamed ?: blameFor(file, lines)
}

operator fun org.w3c.dom.Node.get(attribute: String): String =
    attributes?.getNamedItem(attribute)?.textContent
        ?: throw IllegalArgumentException("No attribute '$attribute' in $this")

fun org.w3c.dom.Node.childrenNamed(name: String): List<org.w3c.dom.Node> =
        childNodes.toIterable().filter { it.nodeName == name }

class PmdQAInfoExtractor(root: org.w3c.dom.Element) : QAInfoContainer by (
    root.childNodes.toIterable()
        .asSequence()
        .filter { it.nodeName == "file" }
        .flatMap { file -> file.childrenNamed("violation").map{ file to it } }
        .map { (file, violation) ->
            QAInfoForChecker(
                "Sub-optimal Java object-orientation",
                file["name"],
                violation["beginline"].toInt()..violation["endline"].toInt(),
                "[${violation["ruleset"].toUpperCase()}] ${violation.textContent.trim()}",
            )
        }
        .asIterable()
)

class CpdQAInfoExtractor(root: org.w3c.dom.Element) : QAInfoContainer by (
    root.childNodes.toIterable()
        .asSequence()
        .filter { it.nodeName == "duplication" }
        .map { duplication ->
            val files = duplication.childrenNamed("file")
            val filePaths = files.map { it["path"] }
            val lines = duplication["lines"].toInt()
            val shortFiles = files.map { "${File(it["path"]).name}:${it["line"]}" }
            val ranges = files.map {
                val begin = it["line"].toInt()
                begin..(begin + lines)
            }
            val blamed = filePaths.zip(ranges).flatMap { (file, lines) -> blameFor(file, lines) }.toSet()
            val description = "Duplication of $lines lines" +
                    " and ${duplication["tokens"]} tokens across ${filePaths.toSet().size}" +
                    " files: ${shortFiles.joinToString(prefix = "", postfix = "")}"
            QAInfoForChecker(
                    "Duplications and violations of the DRY principle",
                    files.first()["path"],
                    ranges.first(),
                    description,
                    blamed
            )
        }
        .asIterable()
)

class CheckstyleQAInfoExtractor(root: org.w3c.dom.Element) : QAInfoContainer by (
    root.childNodes.toIterable()
        .asSequence()
        .filter { it.nodeName == "file" }
        .flatMap { file -> file.childrenNamed("error").map{ file["name"] to it } }
        .map { (file, error) ->
            val line = error["line"].toInt()
            val lineRange = line..line
            QAInfoForChecker("Style errors", file, lineRange, error["message"])
        }
        .asIterable()
)

class SpotBugsQAInfoExtractor(root: org.w3c.dom.Element) : QAInfoContainer by (
    root.childNodes.let { childNodes ->
        val sourceDirs = childNodes.toIterable()
            .filter { it.nodeName == "Project" }
            .first()
            .childrenNamed("SrcDir")
        val sourceDir = if (sourceDirs.size == 1) {
                sourceDirs.first()
            } else {
                sourceDirs.find { it.textContent.endsWith("java") }
            } ?: throw IllegalStateException("Invalid source directories: ${sourceDirs.map { it.textContent }}")
        val sourcePath = sourceDir.textContent.trim()
        childNodes.toIterable()
            .asSequence()
            .filter { it.nodeName == "BugInstance" }
            .map { bugDescriptor ->
                val sourceLineDescriptor = bugDescriptor.childrenNamed("SourceLine").first()
                val category = bugDescriptor["category"].takeUnless { it == "STYLE" } ?: "UNSAFE"
                QAInfoForChecker(
                    "Potential bugs",
                    "$sourcePath${File.separator}${sourceLineDescriptor["sourcepath"]}",
                    sourceLineDescriptor["start"].toInt()..sourceLineDescriptor["end"].toInt(),
                    "[$category] ${bugDescriptor.childrenNamed("LongMessage").first().textContent.trim()}",
                )
            }
            .asIterable()
    }
)

fun org.w3c.dom.NodeList.toIterable() = Iterable {
    object : Iterator<org.w3c.dom.Node> {
        var index = 0;
        override fun hasNext(): Boolean = index < length - 1
        override fun next(): org.w3c.dom.Node = item(index++)
    }
}

fun String.endingWith(postfix: String): String = takeIf { endsWith(postfix) } ?: "$this$postfix"

tasks.register("blame") {
    val dependencies = tasks.withType<org.gradle.api.plugins.quality.Checkstyle>() +
            tasks.withType<org.gradle.api.plugins.quality.Pmd>() +
            tasks.withType<com.github.spotbugs.snom.SpotBugsTask>() +
            tasks.withType<de.aaschmid.gradle.plugins.cpd.Cpd>()
    dependsOn(dependencies)
    val output = "${project.buildDir}${File.separator}blame.md"
    outputs.file(output)
    doLast {
        val factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        val xmlParser = factory.newDocumentBuilder();
        val errors = dependencies
            .flatMap { task -> task.outputs.files.asIterable().filter { it.exists() && it.extension == "xml" } }
            .flatMap<File, QAInfo> {
                val root: org.w3c.dom.Element = xmlParser.parse(it).documentElement
                when (root.tagName) {
                    "pmd" -> PmdQAInfoExtractor(root)
                    "pmd-cpd" -> CpdQAInfoExtractor(root)
                    "checkstyle" -> CpdQAInfoExtractor(root)
                    "BugCollection" -> SpotBugsQAInfoExtractor(root)
                    else -> emptyList<QAInfo>().also { println("Unknown root type ${root.tagName}")}
                }
            }
        val errorsByStudentByChecker: Map<String, Map<String, List<QAInfo>>> = errors
            .flatMap { error -> error.blamedTo.map { it to error } }
            .groupBy { it.first }
            .mapValues { (_, errors) -> errors.map { it.second }.groupBy { it.checker } }
        val report = errorsByStudentByChecker.map { (student, errors) ->
            """
            |# $student
            |
            |${errors.map { it.value.size }.sum()} violations
            |${errors.map { (checker, violations) ->
                """
                |## $checker: ${violations.size} mistakes
                ${ violations.sortedBy { it.details }
                    .joinToString("") {
                        val fileName = File(it.file).name
                        "|* ${it.details.endingWith(".")} In: $fileName@[${it.lines}]\n"
                    }.trimEnd()
                }
                """
            }.joinToString(separator = "", prefix = "", postfix = "")}
            |
            """.trimMargin()
        }.joinToString(separator = "", prefix = "", postfix = "")
        println(report)
        file(output).writeText(report)
    }
}
