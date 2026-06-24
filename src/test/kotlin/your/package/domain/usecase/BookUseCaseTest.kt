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

    describe("reserveBook") {

        it("devrait réserver un livre disponible") {
            val book = Book(title = "Clean Code", author = "Robert Martin", reserved = false)
            val reservedBook = Book(title = "Clean Code", author = "Robert Martin", reserved = true)
            every { bookRepository.getAllBooks() } returns listOf(book)
            every { bookRepository.reserveBook("Clean Code") } returns reservedBook

            val result = bookUseCase.reserveBook("Clean Code")

            result.reserved shouldBe true
            verify { bookRepository.reserveBook("Clean Code") }
        }

        it("devrait lever une exception si le livre est introuvable") {
            every { bookRepository.getAllBooks() } returns emptyList()

            shouldThrow<NoSuchElementException> {
                bookUseCase.reserveBook("Livre Inexistant")
            }.message shouldBe "Livre introuvable : Livre Inexistant"
        }

        it("devrait lever une exception si le livre est déjà réservé") {
            val book = Book(title = "Clean Code", author = "Robert Martin", reserved = true)
            every { bookRepository.getAllBooks() } returns listOf(book)

            shouldThrow<IllegalStateException> {
                bookUseCase.reserveBook("Clean Code")
            }.message shouldBe "Le livre est déjà réservé"
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
