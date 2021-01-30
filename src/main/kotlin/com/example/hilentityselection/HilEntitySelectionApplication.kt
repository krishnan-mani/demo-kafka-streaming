package com.example.hilentityselection

import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.schema.registry.client.ConfluentSchemaRegistryClient
import org.springframework.cloud.schema.registry.client.EnableSchemaRegistryClient
import org.springframework.cloud.schema.registry.client.SchemaRegistryClient
import org.springframework.context.annotation.Bean
import reactor.core.publisher.Flux
import reactor.util.function.Tuple2
import reactor.util.function.Tuples
import java.util.function.Function


@SpringBootApplication
//@EnableSchemaRegistryClient
class HilEntitySelectionApplication{

	@Bean
	fun singleInputMultipleOutputs(): Function<Flux<GenericRecord>, Tuple2<Flux<GenericRecord>, Flux<GenericRecord>>>? {
		return Function { flux: Flux<GenericRecord> ->
			val connectedFlux = flux.publish().autoConnect(2)
			val passedFlux = connectedFlux.filter { it.get("score") as Int > 80 }
			val failedFlux = connectedFlux.filter { it.get("score") as Int <= 80 }
			print("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")
			Tuples.of(
				passedFlux, failedFlux
			)
		}
	}

	@Bean
	fun schemaRegistryClient(): SchemaRegistryClient {
		val client = ConfluentSchemaRegistryClient()
		client.setEndpoint("http://localhost:8081")
		return client
	}
}



fun main(args: Array<String>) {
	runApplication<HilEntitySelectionApplication>(*args)
}
