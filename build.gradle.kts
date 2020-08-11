import org.apache.tools.ant.taskdefs.condition.Os

group = "org.example"
version = "1.0.0"

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.create("helm") {
    group = "deploy"
    description = "Helm Chart installation"
    doLast {
        val names = mutableMapOf<String, String>()
        fileTree("helm").visit {
            if (this.isDirectory && this.name !in listOf("charts", "templates"))
                names[this.name] = this.name
                    .replace("[.,;:_-]?".toRegex(), "")
                    .replace("chart", "")
        }
        names.forEach { (key, value) ->
            exec {
                if (Os.isFamily(Os.FAMILY_WINDOWS))
                    commandLine("cmd", "/c", "echo helm install $value $key")
                else
                    commandLine("sh", "-c", "echo helm install $value $key")
            }
        }
    }
}
