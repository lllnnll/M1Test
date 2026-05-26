package your.`package`.domain.usecase

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.list
import io.mockk.every
import io.mockk.mockk
import org.example.your.`package`.domain.model.Book
import org.example.your.`package`.domain.port.BookRepository
import org.example.your.`package`.domain.usecase.BookUseCase

class BookUseCasePropertyTest : DescribeSpec({

    val bookRepository = mockk<BookRepository>()
    val bookUseCase = BookUseCase(bookRepository)

    it("la liste retournée contient tous les éléments de la liste stockée") {
        checkAll(
            Arb.list(Arb.string(1..20), 0..50)
        ) { titles ->
            val books = titles.map { Book(title = it, author = "auteur") }
            every { bookRepository.getAllBooks() } returns books

            val result = bookUseCase.getAllBooks()

            result shouldContainAll books
        }
    }
})