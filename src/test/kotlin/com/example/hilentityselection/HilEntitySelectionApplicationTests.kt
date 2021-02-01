package com.example.hilentityselection

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.Output
import org.springframework.cloud.stream.test.binder.MessageCollector
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.GenericMessage
import java.util.concurrent.TimeUnit

const val inSchemaStr =
    "{\"type\": \"record\", \"name\": \"Entity\", \"fields\": [{\"name\": \"name\", \"type\": \"string\"}, {\"name\": \"score\", \"type\": \"int\"}]}"
val inSchema = Schema.Parser().parse(inSchemaStr)

interface TestBindings {
    @Output("author-evaluation-passed")
    fun evaluationPassedChannel(): MessageChannel

    @Output("author-evaluation-failed")
    fun evaluationFailedChannel(): MessageChannel

    @Input("author-extracted")
    fun authorExtractedChannel(): MessageChannel
}

@SpringBootTest
@EnableBinding(TestBindings::class)
class HilEntitySelectionApplicationTests(
    processor: TestBindings,
    inputDestination: MessageCollector
) : ExpectSpec({

    context("Entity Selector Tests") {

        context("Entity with less than 80 score") {
            val payload: GenericRecord = mapOf("name" to "author", "score" to 75).asGenericRecord()
            processor.authorExtractedChannel().send(GenericMessage(payload))

            expect("Message published to Failed channel") {

                val message = inputDestination.forChannel(processor.evaluationFailedChannel())
                    .poll(1, TimeUnit.SECONDS).payload as GenericRecord

                message["reason"] shouldBe "Score less than 80"
            }
        }

        context("Entity with more than 80 score") {
            val payload: GenericRecord = mapOf("name" to "author", "score" to 85).asGenericRecord()
            processor.authorExtractedChannel().send(GenericMessage(payload))

            expect("Message published to Passed channel") {

                val message = inputDestination.forChannel(processor.evaluationPassedChannel())
                    .poll(1, TimeUnit.SECONDS).payload as GenericRecord

                message["reason"] shouldBe "Score more than 80"
            }
        }
    }
})

private fun Map<String, Any?>.asGenericRecord(): GenericRecord {
    val record = GenericData.Record(inSchema)
    record.put("name", this["name"])
    record.put("score", this["score"])
    return record
}
