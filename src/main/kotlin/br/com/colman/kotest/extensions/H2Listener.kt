package br.com.colman.kotest.extensions

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import org.h2.jdbcx.JdbcDataSource
import java.sql.Connection
import javax.sql.DataSource

class H2Listener(private val url: String = "jdbc:h2:mem:") : TestListener {

  private var ds: DataSource? = null

  fun connection(): Connection = ds!!.connection

  override suspend fun beforeSpec(spec: Spec) {
    Class.forName("org.h2.Driver")
    ds = JdbcDataSource().apply {
      setURL(url)
    }
  }
}
