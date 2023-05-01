package br.com.colman.kotest.extensions

import io.kotest.assertions.withClue
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class H2ListenerTest : FunSpec(
  {

    val listener = listener(H2Listener())

    test("Creates and returns data") {
      val connection = listener.dataSource.connection

      connection.createStatement().execute("CREATE TABLE FOO(name VARCHAR);")
      connection.createStatement().execute("INSERT INTO FOO(name) VALUES ('BAR');")
      connection.createStatement().executeQuery("SELECT * FROM FOO;").apply {
        next()
        getString("name") shouldBe "BAR"
      }
    }

    test("Resets between tests") {
      val connection = listener.dataSource.connection
      withClue("I wouldn't be able to create a new table if it wasn't reset") {
        connection.createStatement().execute("CREATE TABLE FOO(name VARCHAR);")
      }
    }
  }
)
