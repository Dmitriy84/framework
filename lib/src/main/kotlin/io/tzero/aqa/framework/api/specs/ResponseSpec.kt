package io.tzero.aqa.framework.api.specs

import io.restassured.builder.ResponseSpecBuilder

object ResponseSpec {
    val `200` = ResponseSpecBuilder()
        .expectStatusCode(200)
        .build()!!

    val `201` = ResponseSpecBuilder()
        .expectStatusCode(201)
        .build()!!

    val `400` = ResponseSpecBuilder()
        .expectStatusCode(400)
        .build()!!
}
