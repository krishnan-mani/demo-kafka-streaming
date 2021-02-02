package com.example.hilentityselection

import org.apache.avro.generic.GenericRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.support.beans
import reactor.core.publisher.Flux
import reactor.util.function.Tuple2
import java.util.function.Function

@SpringBootApplication
class HilEntitySelectionApplication

typealias SplitterFunction<T> = Function<T, Tuple2<T, T>>

val logger: Logger = LoggerFactory.getLogger("HilEntityApplication")

fun beans() = beans {
    bean<SplitterFunction<Flux<GenericRecord>>>("singleInputMultipleOutputs") {
        Function { splitFailedPassedEntities(it) }
    }
}

fun main(args: Array<String>) {
    SpringApplication(HilEntitySelectionApplication::class.java).apply {
        addInitializers(beans())
        run(*args)
    }
}
