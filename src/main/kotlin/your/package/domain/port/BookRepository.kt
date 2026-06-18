package org.example.your.`package`.domain.port

import org.example.your.`package`.domain.model.Book

interface BookRepository{
    fun addBook(book: Book): Book
    fun getAllBooks(): List<Book>
    fun reserveBook(title: String): Book
}
