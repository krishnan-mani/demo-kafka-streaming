package com.example.hilentityselection

import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import reactor.core.publisher.Flux
import reactor.util.function.Tuple2
import reactor.util.function.Tuples


fun splitFailedPassedEntities(source: Flux<GenericRecord>) : Tuple2<Flux<GenericRecord>, Flux<GenericRecord>> {
    val connectedFlux = source.publish().autoConnect(3)

    connectedFlux.subscribe{logger.info("Received message{body: $it, schema: ${it.schema}}")}

    val passedEntities: Flux<GenericRecord> = connectedFlux
        .filter { it.get("score") as Int > 80 }
        .map { "Score more than 80".asHilEntityRecord() }

    val failedEntities: Flux<GenericRecord> = connectedFlux
        .filter { it.get("score") as Int <= 80 }
        .map { "Score less than 80".asHilEntityRecord() }

    return Tuples.of(passedEntities, failedEntities)
}

private fun String.asHilEntityRecord(): GenericData.Record {
    val record = GenericData.Record(entitySchema)
    record.put("reason", this)
    return record
}