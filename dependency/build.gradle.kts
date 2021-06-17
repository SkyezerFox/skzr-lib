plugins {
	java
	id("io.freefair.lombok") version "6.0.0-m2"
}

dependencies {
	kotlin("stdlib")
	// okhttp
	implementation("com.squareup.okhttp3:okhttp:4.9.0")
	// testing
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
}
