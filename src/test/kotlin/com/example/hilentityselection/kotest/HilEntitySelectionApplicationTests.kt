package com.example.hilentityselection.kotest

import com.example.hilentityselection.Entity
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.cloud.stream.binder.test.OutputDestination
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Import
import org.springframework.messaging.support.GenericMessage

@SpringBootTest
@Import(TestChannelBinderConfiguration::class)
class HilEntitySelectionApplicationTests(
    inputDestination: InputDestination,
    outputDestination: OutputDestination,
    objectMapper: ObjectMapper
) : ExpectSpec({

    context("Entity Selector Tests") {

        context("Entity with less than 80 score") {
            inputDestination.send(GenericMessage("{\"name\":\"author\", \"score\":75}"))

            expect("Message published to Failed channel") {
                val received: Entity = objectMapper.readValue(
                    outputDestination.receive(0L, "author-evaluation-failed").payload,
                    Entity::class.java
                )

                received.name shouldBe "author"
                received.score shouldBe 75
            }
        }

        context("Entity with more than 80 score") {
            inputDestination.send(GenericMessage("{\"name\":\"author\", \"score\":90}"))

            expect("Message published to Passed channel") {
                val received: Entity = objectMapper.readValue(
                    outputDestination.receive(0L, "author-evaluation-passed").payload,
                    Entity::class.java
                )

                received.name shouldBe "author"
                received.score shouldBe 90
            }
        }
    }
})
