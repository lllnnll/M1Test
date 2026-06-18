package org.example.your.`package`.infrastructure.driving.controller

import org.example.your.`package`.domain.usecase.BookUseCase
import org.example.your.`package`.infrastructure.driving.dto.BookDTO
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookController(private val bookUseCase: BookUseCase) {

    @GetMapping
    fun getAllBooks(): List<BookDTO> =
        bookUseCase.getAllBooks().map { BookDTO(it.title, it.author) }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addBook(@RequestBody dto: BookDTO) {
        bookUseCase.addBook(dto.title, dto.author)
    }

    @PostMapping("/{title}/reserve")
    fun reserveBook(@PathVariable title: String) {
        bookUseCase.reserveBook(title)
    }
}
