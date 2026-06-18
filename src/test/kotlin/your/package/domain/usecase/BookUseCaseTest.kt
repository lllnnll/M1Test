package your.`package`.domain.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.your.`package`.domain.model.Book
import org.example.your.`package`.domain.port.BookRepository
import org.example.your.`package`.domain.usecase.BookUseCase

class BookUseCaseTest : DescribeSpec({

    val bookRepository = mockk<BookRepository>()
    val bookUseCase = BookUseCase(bookRepository)

    describe("addBook") {

        it("devrait ajouter un livre valide") {
            val book = Book(title = "Clean Code", author = "Robert Martin")
            every { bookRepository.addBook(any()) } returns book

            val result = bookUseCase.addBook("Clean Code", "Robert Martin")

            result.title shouldBe "Clean Code"
            result.author shouldBe "Robert Martin"
            verify { bookRepository.addBook(any()) }
        }

        it("devrait lever une exception si le titre est vide") {
            shouldThrow<IllegalArgumentException> {
                bookUseCase.addBook("", "Robert Martin")
            }.message shouldBe "Le titre ne peut pas être vide"
        }

        it("devrait lever une exception si l'auteur est vide") {
            shouldThrow<IllegalArgumentException> {
                bookUseCase.addBook("Clean Code", "")
            }.message shouldBe "L'auteur ne peut pas être vide"
        }
    }

    describe("getAllBooks") {

        it("devrait retourner la liste triée alphabétiquement par titre") {
            val books = listOf(
                Book(title = "Clean Code", author = "Robert Martin"),
                Book(title = "Architecture Hexagonale", author = "Alistair Cockburn"),
                Book(title = "Domain-Driven Design", author = "Eric Evans")
            )
            every { bookRepository.getAllBooks() } returns books

            val result = bookUseCase.getAllBooks()

            result.map { it.title } shouldBe listOf(
                "Architecture Hexagonale",
                "Clean Code",
                "Domain-Driven Design"
            )
        }

        it("devrait retourner une liste vide si aucun livre") {
            every { bookRepository.getAllBooks() } returns emptyList()

            val result = bookUseCase.getAllBooks()

            result shouldBe emptyList()
        }
    }
})
