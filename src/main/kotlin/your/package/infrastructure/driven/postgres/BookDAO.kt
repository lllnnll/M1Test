package org.example.your.`package`.infrastructure.driven.postgres

import org.example.your.`package`.domain.model.Book
import org.example.your.`package`.domain.port.BookRepository
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class BookDAO(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : BookRepository {

    override fun addBook(book: Book): Book {
        namedParameterJdbcTemplate.update(
            "INSERT INTO books (title, author, reserved) VALUES (:title, :author, :reserved)",
            mapOf("title" to book.title, "author" to book.author, "reserved" to book.reserved)
        )
        return book
    }

    override fun getAllBooks(): List<Book> {
        return namedParameterJdbcTemplate.query(
            "SELECT * FROM books",
            MapSqlParameterSource()
        ) { rs, _ ->
            Book(
                title = rs.getString("title"),
                author = rs.getString("author"),
                reserved = rs.getBoolean("reserved")
            )
        }
    }
}
