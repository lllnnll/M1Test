package your.`package`.adapter.rest

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import org.example.your.`package`.domain.model.Book
import org.example.your.`package`.domain.usecase.BookUseCase
import org.example.your.`package`.infrastructure.driving.controller.BookController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(BookController::class)
class BookControllerIT : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    @MockkBean
    lateinit var bookUseCase: BookUseCase

    @Autowired
    lateinit var mockMvc: MockMvc

    init {
        describe("GET /books") {
            it("retourne la liste des livres triée par titre") {
                every { bookUseCase.getAllBooks() } returns listOf(
                    Book("Architecture Hexagonale", "Alistair Cockburn"),
                    Book("Clean Code", "Robert Martin")
                )

                mockMvc.get("/books")
                    .andExpect {
                        status { isOk() }
                        content { contentType(MediaType.APPLICATION_JSON) }
                        content {
                            json(
                                """
                                [
                                    {"title": "Architecture Hexagonale", "author": "Alistair Cockburn"},
                                    {"title": "Clean Code", "author": "Robert Martin"}
                                ]
                                """.trimIndent()
                            )
                        }
                    }
            }

            it("retourne une liste vide si aucun livre") {
                every { bookUseCase.getAllBooks() } returns emptyList()

                mockMvc.get("/books")
                    .andExpect {
                        status { isOk() }
                        content { json("[]") }
                    }
            }
        }

        describe("POST /books") {
            it("crée un livre et retourne 201") {
                every { bookUseCase.addBook(any(), any()) } returns Book("Clean Code", "Robert Martin")

                mockMvc.post("/books") {
                    contentType = MediaType.APPLICATION_JSON
                    content = """{"title": "Clean Code", "author": "Robert Martin"}"""
                }.andExpect {
                    status { isCreated() }
                }
            }

            it("retourne 400 si le corps de la requête est absent") {
                mockMvc.post("/books") {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            it("retourne 500 si le domaine lève une exception") {
                every { bookUseCase.addBook(any(), any()) } throws RuntimeException("Erreur interne")

                mockMvc.post("/books") {
                    contentType = MediaType.APPLICATION_JSON
                    content = """{"title": "Clean Code", "author": "Robert Martin"}"""
                }.andExpect {
                    status { is5xxServerError() }
                }
            }
        }
    }
}
