package lv.romstr.mobile

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*
import java.lang.Exception
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

private val database = mutableListOf<ShoppingItem>()

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        post("/items") {
            val item = call.receive<ShoppingItem>().copy(id = UUID.randomUUID().toString())
            database.add(item)
            call.respond(HttpStatusCode.Created, mapOf("id" to item.id))
        }

        get("/items") {
            call.respond(database)
        }

        get("items/{id}") {
            val item = database.firstOrNull { it.id == call.id }
            item?.let {
                call.respond(item)
            } ?: call.respond(HttpStatusCode.NotFound)
        }

        put("/items/{id}") {
            val item = database.firstOrNull { it.id == call.id }
            val newItem = call.receive<ShoppingItem>()
            item?.let {
                database[database.indexOf(item)] = newItem.copy(id = item.id)
                call.respond(HttpStatusCode.OK)
            } ?: call.respond(HttpStatusCode.NotFound)
        }

        delete("/items/{id}") {
            val item = database.firstOrNull { it.id == call.id }
            item?.let {
                database.remove(item)
                call.respond(HttpStatusCode.OK)
            } ?: call.respond(HttpStatusCode.NotFound)
        }

    }
}

private val ApplicationCall.id
    get() = try {
        UUID.fromString(parameters["id"]).toString()
    } catch (e: Exception) {
        null
    }

