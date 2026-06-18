package org.example.your.`package`.domain.usecase

import org.example.your.`package`.domain.port.BookRepository
import org.example.your.`package`.domain.model.Book

class BookUseCase(private val bookRepository: BookRepository) {

    fun addBook(title: String, author: String): Book {
        require(title.isNotBlank()) { "Le titre ne peut pas être vide" }
        require(author.isNotBlank()) { "L'auteur ne peut pas être vide" }

        val book = Book(title = title, author = author)
        return bookRepository.addBook(book)
    }

    fun getAllBooks(): List<Book> {
        return bookRepository.getAllBooks().sortedBy { it.title }
    }
}
