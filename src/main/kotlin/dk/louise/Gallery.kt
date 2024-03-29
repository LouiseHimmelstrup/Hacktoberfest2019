package dk.louise

import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import kotlinx.html.*

data class Image(
    val alt: String,
    val src: String
)

val images = arrayListOf(
    Image(
        "A goodboy and his bitch mummy/waifu",
        "https://preview.redd.it/i09rw72u1zq01.jpg?width=960&crop=smart&s=2a2bb09f77cf92b5ce06be22f6814c3ec088980a"
    ),
    Image(
        "A goodboy aggravated by the insufferable Chads",
        "https://i.redd.it/ljho9u4pdnh11.jpg"
    ),
    Image(
        "A goodboy showing off his Dewie collection",
        "https://pbs.twimg.com/media/C2dMmsyXgAEDqf7.jpg"
    ),
    Image(
        "A goodboy being spotted on the rare occasion of being outside his lair",
        "https://i.redd.it/qtzscvijp1411.jpg"
    ),
    Image(
        "A goodboy ready to revolt against the Chad tyranny",
        "https://i.redd.it/9rd9bkg0gnr31.jpg"
    )
)

fun DIV.image(theImage: Image, id: Int) {
    a(href = "/Gallery/$id", classes = "gallery-img") {
        img(classes = "media", alt = theImage.alt) {
            src = theImage.src
        }
    }
}

val imageComments = HashMap<Int, List<Comment>>()

fun Routing.gallery() {
    get("/Gallery") {
        call.respondHtml {
            myTemplate("The Goodboy Gallery") {
                h1(classes = "header first-header") {
                    +"This is the goodboy gallery"
                }

                for ((index, myImage) in images.withIndex()) {
                    image(myImage, index)
                }
            }
        }
    }

    get("/Gallery/{id}") {
        val id = call.parameters["id"]?.toInt()!!
        val picture = images[id]
        val comments = imageComments[id] ?: emptyList()

        call.respondHtml {
            myTemplate(picture.alt) {
                h1(classes = "header first-header") {
                    +picture.alt
                }
                image(picture, id)
                hr(classes = "comment-line")
                h2(classes = "header") {
                    +"Comment section"
                }
                for (comment in comments) {
                    div(classes = "posted-comment") {
                        +(comment.user)
                        +" said: "
                        br()
                        comment.message.split("\n").forEach { line ->
                            span (classes = "comment-text") { +(line) }
                            br()
                        }
                    }
                }

                form(classes = "comment-container", method = FormMethod.post, action = "/Gallery/comment/$id") {
                    div {
                        label(classes = "comment-label") {
                            +"Name"
                            br()

                            // The name property determines where we can find the data.
                            input(classes = "comment-form name-box", name = "yourName", type = InputType.text) {
                                placeholder = "Write your name here"
                            }

                            br()
                        }
                    }


                    div {
                        label(classes = "comment-label") {
                            +"Comment"
                            br()
                            // The name property determines where we can find the data.
                            textArea(classes = "comment-form comment-box") {
                                name = "yourComment"
                                placeholder = "Write your comment here"
                            }

                            br()
                        }
                    }


                    input(classes = "comment-form comment-submit", type = InputType.submit) {
                        value = "Submit comment!"
                    }
                }
            }
        }
    }

    post("/Gallery/comment/{id}") {
        // We can read data submitted via form like this:
        val parameters = call.receiveParameters()
        val id = call.parameters["id"]?.toInt()!!
        val yourName = parameters["yourName"].takeIf { !it.isNullOrBlank() } ?: "Anon"
        val yourComment = parameters["yourComment"] ?: ""

        val comments = imageComments[id] ?: emptyList()
        imageComments[id] = comments + Comment(yourName, yourComment)

        call.respondRedirect("/Gallery/$id")
    }
}