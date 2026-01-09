plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.allopen") version "2.2.21"
    id("io.quarkus")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    // 1. プラットフォーム定義を変数にしておく（見やすくするため）
    val quarkusPlatform = enforcedPlatform(
        "${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"
    )

    // 2. メインコードに BOM を適用
    implementation(quarkusPlatform)

    // ★ 3. テストコードにも BOM を適用（これが不足していました！）
    testImplementation(quarkusPlatform)

    // --- Quarkiverse (バージョン指定が必要) ---
    implementation("io.quarkiverse.reactivemessaging.nats-jetstream:quarkus-messaging-nats-jetstream:3.28.1")
    implementation("io.quarkiverse.minio:quarkus-minio:3.8.6")

    // --- Quarkus Core (バージョン指定不要) ---
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-messaging-rabbitmq")

    // --- テスト（明示バージョン指定：一部生成タスクがBOMを拾わないため） ---
    testImplementation("io.quarkus:quarkus-junit5:${quarkusPlatformVersion}")
    testImplementation("io.rest-assured:rest-assured:5.5.0")
    // SmallRye in-memory connector for tests
    testImplementation("io.smallrye.reactive:smallrye-reactive-messaging-in-memory:4.36.0")
}


group = "inuverse"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
}
allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
        javaParameters = true
    }
}
