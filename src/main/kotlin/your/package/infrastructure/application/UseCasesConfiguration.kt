package org.example.your.`package`.infrastructure.application

import org.example.your.`package`.domain.port.BookRepository
import org.example.your.`package`.domain.usecase.BookUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCasesConfiguration {

    @Bean
    fun bookUseCase(bookRepository: BookRepository): BookUseCase {
        return BookUseCase(bookRepository)
    }
}
