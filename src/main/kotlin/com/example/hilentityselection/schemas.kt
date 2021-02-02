package com.example.hilentityselection

import org.apache.avro.Schema

const val entitySchemaStr = """
        {
       "type":"record",
       "name":"HilEntity",
       "fields":[
          {
             "name":"reason",
             "type":"string"
          }
       ]
    }
    """

val entitySchema: Schema = Schema.Parser().parse(entitySchemaStr)