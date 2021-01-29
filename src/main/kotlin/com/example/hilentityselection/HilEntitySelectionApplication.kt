package com.example.hilentityselection

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.util.function.Function


@SpringBootApplication
class HilEntitySelectionApplication{

	@Bean fun toUpperCase() : Function<String, String> = Function { it.toUpperCase() }
}

data class Entity(val name: String, val score: Int)

fun main(args: Array<String>) {
	runApplication<HilEntitySelectionApplication>(*args)
}
