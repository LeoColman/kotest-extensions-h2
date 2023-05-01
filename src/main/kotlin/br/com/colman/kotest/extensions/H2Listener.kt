package br.com.colman.kotest.extensions

import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.h2.jdbcx.JdbcDataSource
import javax.sql.DataSource

public class H2Listener(private val url: String = "jdbc:h2:mem:") : TestListener {

  public val dataSource: DataSource = JdbcDataSource().apply { setUrl(url) }

  init {
    Class.forName("org.h2.Driver")  // Load H2 driver
  }

  override suspend fun afterTest(testCase: TestCase, result: TestResult) {
    dataSource.connection.createStatement().execute("SHUTDOWN")
  }
}
