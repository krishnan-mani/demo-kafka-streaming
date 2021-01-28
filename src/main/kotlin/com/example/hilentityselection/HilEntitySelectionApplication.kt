package com.example.hilentityselection

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import reactor.core.publisher.Flux
import reactor.util.function.Tuple2
import reactor.util.function.Tuples
import java.util.function.Function


@SpringBootApplication
class HilEntitySelectionApplication{

	@Bean
	fun singleInputMultipleOutputs(): Function<Flux<Entity>, Tuple2<Flux<Entity>, Flux<Entity>>>? {
		return Function { flux: Flux<Entity> ->
			val connectedFlux = flux.publish().autoConnect(2)
			val passedFlux = connectedFlux.filter { it.score > 80 }
			val failedFlux = connectedFlux.filter { it.score <= 80 }

			Tuples.of(
				passedFlux, failedFlux
			)
		}
	}

	@Bean
	fun objectMapperBuilder(): ObjectMapper = ObjectMapper().registerKotlinModule()

}

data class Entity(val name: String, val score: Int)

fun main(args: Array<String>) {
	runApplication<HilEntitySelectionApplication>(*args)
}
