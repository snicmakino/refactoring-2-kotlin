import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import json.Invoice
import json.Performance
import json.Play

class StatementTest : StringSpec() {

    private val plays = mapOf<String, Play>(
        "hamlet" to Play("Hamlet", "tragedy"),
        "as-like" to Play("As You Like It", "comedy"),
        "othello" to Play("Othello", "tragedy")
    )

    private val invoice = Invoice(
        "BigCo",
        listOf(
            Performance("hamlet", 55),
            Performance("as-like", 35),
            Performance("othello", 40),
        )
    )

    init {
        "PlainText出力正常ケース" {
            statement(invoice, plays) shouldBe "Statement for BigCo\n" +
                    " Hamlet: \$650.00 (55 seats)\n" +
                    " As You Like It: \$580.00 (35 seats)\n" +
                    " Othello: \$500.00 (40 seats)\n" +
                    "Amount owed is \$1730.00\n" +
                    "You earned 47 credits\n"
        }

        "HTML出力正常ケース" {
            htmlStatement(invoice, plays) shouldBe "<h1>Statement for BigCo</h1>\n" +
                    "<table>\n" +
                    "<tr><th>play</th><th>seats</th><th>cost</th></tr>  <tr><td>Hamlet</td><td>55</td><td>\$650.00</td></tr>\n" +
                    "  <tr><td>As You Like It</td><td>35</td><td>\$580.00</td></tr>\n" +
                    "  <tr><td>Othello</td><td>40</td><td>\$500.00</td></tr>\n" +
                    "</table>\n" +
                    "<p>Amount owed is <em>\$1730.00</em></p>\n" +
                    "<p>You earned <em>47</em> credits</p>\n"
        }
    }
}
