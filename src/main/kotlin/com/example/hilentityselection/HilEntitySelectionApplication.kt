package com.example.hilentityselection

import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.schema.registry.client.ConfluentSchemaRegistryClient
import org.springframework.cloud.schema.registry.client.SchemaRegistryClient
import org.springframework.context.annotation.Bean
import reactor.core.publisher.Flux
import java.util.function.Supplier

val schemaStr = "{\"type\": \"record\", \"name\": \"Entity\", \"fields\": [{\"name\": \"name\", \"type\": \"string\"}, {\"name\": \"score\", \"type\": \"int\"}]}"

@SpringBootApplication
class HilEntitySelectionApplication(val logger: Logger = LoggerFactory.getLogger(HilEntitySelectionApplication::class.java)) {

    @Bean
    fun singleInputMultipleOutputs(): Supplier<Flux<GenericRecord>>? {
        val record = GenericData.Record(Schema.parse(schemaStr))
        record.put("score", 99)
        record.put("name", "auth")
        return Supplier { Flux.just(record)}
    }
}

@Bean
fun schemaRegistryClient(): SchemaRegistryClient {
	val client = ConfluentSchemaRegistryClient()
	client.setEndpoint("http://localhost:8081")
	return client
}

fun main(args: Array<String>) {
    runApplication<HilEntitySelectionApplication>(*args)
}
