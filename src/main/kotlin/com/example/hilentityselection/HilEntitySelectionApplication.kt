package com.example.hilentityselection

import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import reactor.core.publisher.Flux
import reactor.util.function.Tuple2
import reactor.util.function.Tuples
import java.util.function.Function

const val entitySchemaStr =
    "{\"type\": \"record\", \"name\": \"HilEntity\", \"fields\": [{\"name\": \"reason\", \"type\": \"string\"}]}"

val entitySchema: Schema = Schema.Parser().parse(entitySchemaStr)

@SpringBootApplication
class HilEntitySelectionApplication(val logger: Logger = LoggerFactory.getLogger(HilEntitySelectionApplication::class.java)) {

    @Bean
    fun singleInputMultipleOutputs(): Function<Flux<GenericRecord>, Tuple2<Flux<GenericRecord>, Flux<GenericRecord>>> {
        return Function { flux ->
            val connectedFlux = flux.publish().autoConnect(3)

            connectedFlux.subscribe{logger.info("Received message{body: $it, schema: ${it.schema}}")}

            val passedEntities: Flux<GenericRecord> = connectedFlux
                .filter { it.get("score") as Int > 80 }
                .map { "Score more than 80".asHilEntityRecord() }

            val failedEntities: Flux<GenericRecord> = connectedFlux
                .filter { it.get("score") as Int <= 80 }
                .map { "Score less than 80".asHilEntityRecord() }

            Tuples.of(passedEntities, failedEntities)

        }
    }
}

private fun String.asHilEntityRecord(): GenericData.Record {
    val record = GenericData.Record(entitySchema)
    record.put("reason", this)
    return record
}

fun main(args: Array<String>) {
    runApplication<HilEntitySelectionApplication>(*args)
}
