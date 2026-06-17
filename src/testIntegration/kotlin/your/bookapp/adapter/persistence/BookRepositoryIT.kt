package your.bookapp.adapter.persistence

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import org.example.your.`package`.domain.model.Book
import org.example.your.`package`.domain.port.BookRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer
import your.`package`.TestBookApplication

@SpringBootTest(classes = [TestBookApplication::class])
@ActiveProfiles("testIntegration")
class BookRepositoryIT : FunSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var bookRepository: BookRepository

    @Autowired
    lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    companion object {
        private val container = PostgreSQLContainer<Nothing>("postgres:13-alpine")

        init {
            // Docker Desktop 29.x requires minimum API version 1.44 (default in docker-java is 1.24)
            System.setProperty("api.version", "1.44")
            container.start()
            System.setProperty("spring.datasource.url", container.jdbcUrl)
            System.setProperty("spring.datasource.username", container.username)
            System.setProperty("spring.datasource.password", container.password)
        }
    }

    init {
        afterSpec {
            container.stop()
        }

        beforeEach {
            jdbcTemplate.update("DELETE FROM books", emptyMap<String, Any>())
        }

        test("addBook persiste un livre en base") {
            val book = Book("Clean Code", "Robert Martin")

            val result = bookRepository.addBook(book)

            result.title shouldBe "Clean Code"
            result.author shouldBe "Robert Martin"
        }

        test("getAllBooks retourne tous les livres stockés") {
            bookRepository.addBook(Book("Architecture Hexagonale", "Alistair Cockburn"))
            bookRepository.addBook(Book("Domain-Driven Design", "Eric Evans"))

            val result = bookRepository.getAllBooks()

            result.map { it.title } shouldContainAll listOf("Architecture Hexagonale", "Domain-Driven Design")
        }

        test("getAllBooks retourne une liste vide si aucun livre") {
            val result = bookRepository.getAllBooks()

            result shouldBe emptyList()
        }
    }
}
