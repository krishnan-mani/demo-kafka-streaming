package com.example.hilentityselection

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.spring.SpringAutowireConstructorExtension

class ProjectConfig : AbstractProjectConfig() {
    override fun extensions() = listOf(SpringAutowireConstructorExtension)
}