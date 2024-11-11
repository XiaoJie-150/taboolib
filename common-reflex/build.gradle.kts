import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    implementation("org.tabooproject.reflex:reflex:1.1.7")
    implementation("org.tabooproject.reflex:analyser:1.1.7")
}

tasks {
    withType<ShadowJar> {
        dependencies {
            include(dependency("org.tabooproject.reflex:reflex:1.1.7"))
            include(dependency("org.tabooproject.reflex:analyser:1.1.7"))
        }
        relocate("org.taboooproject", "taboolib.library")
    }
}