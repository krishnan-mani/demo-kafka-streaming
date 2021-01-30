import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.2"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.4.21"
	kotlin("plugin.spring") version "1.4.21"

}

group = "com.example"
version = "0.0.1-SNAPSHOT"
val kotestVersion = "4.3.2"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven {url = uri("https://packages.confluent.io/maven/")}
}

extra["springCloudVersion"] = "2020.0.0"

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib")

	implementation("org.apache.kafka:kafka-streams")
	implementation("io.confluent:kafka-avro-serializer:5.3.0")

	implementation("org.springframework.cloud:spring-cloud-stream")
	implementation("org.springframework.cloud:spring-cloud-schema-registry-client")
	implementation("org.springframework.cloud:spring-cloud-function-kotlin")
	implementation("org.springframework.cloud:spring-cloud-starter-stream-kafka")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")



	testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
	testImplementation("io.kotest:kotest-extensions-spring:$kotestVersion")
	testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
	testImplementation("io.kotest:kotest-property:$kotestVersion")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.cloud:spring-cloud-stream") {
		artifact {
			name = "spring-cloud-stream"
			extension = "jar"
			type ="test-jar"
			classifier = "test-binder"
		}
	}
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
