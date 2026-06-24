package your.bookapp

import io.cucumber.java.Before
import io.cucumber.java.Scenario
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.path.json.JsonPath
import io.restassured.response.ValidatableResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class BookStepDefs {

    private var lastBooksResult: ValidatableResponse? = null

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    @Before
    fun setup(scenario: Scenario) {
        RestAssured.baseURI = "http://localhost:$port"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        jdbcTemplate.update("DELETE FROM books", emptyMap<String, Any>())
    }

    @Given("the user creates the book with title {string} and author {string}")
    fun createBook(title: String, author: String) {
        given()
            .contentType(ContentType.JSON)
            .and()
            .body(
                """
                {
                    "title": "$title",
                    "author": "$author"
                }
                """.trimIndent()
            )
            .`when`()
            .post("/books")
            .then()
            .statusCode(201)
    }

    @When("the user reserves the book with title {string}")
    fun reserveBook(title: String) {
        given()
            .`when`()
            .post("/books/$title/reserve")
            .then()
            .statusCode(200)
    }

    @When("the user gets all books")
    fun getAllBooks() {
        lastBooksResult = given()
            .`when`()
            .get("/books")
            .then()
            .statusCode(200)
    }

    @Then("the list should contains the following books")
    fun shouldHaveListOfBooks(payload: List<Map<String, Any>>) {
        val expectedResponse = payload.joinToString(separator = ",", prefix = "[", postfix = "]") { line ->
            line.entries.joinToString(separator = ",", prefix = "{", postfix = "}") {
                val raw = it.value.toString()
                val jsonValue = if (raw == "true" || raw == "false") raw else """"$raw""""
                """"${it.key}": $jsonValue"""
            }
        }
        lastBooksResult!!.extract().body().jsonPath().prettify() shouldBe JsonPath(expectedResponse).prettify()
    }
}
