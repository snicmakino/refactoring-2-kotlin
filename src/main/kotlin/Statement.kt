import json.Invoice
import json.Play


fun statement(invoice: Invoice, plays: Map<String, Play>): String {
    return renderPlainText(StatementData(invoice.customer, invoice.performances, plays))
}

fun renderPlainText(data: StatementData): String {
    var result = "Statement for ${data.customer}\n"

    for (perf in data.performances) {
        // 注文の内訳を出力
        result += " ${perf.play.name}: ${usd(perf.amount)} (${perf.audience} seats)\n"
    }

    result += "Amount owed is ${usd(data.totalAmount)}\n"
    result += "You earned ${"%.0f".format(data.totalVolumeCredits)} credits\n"
    return result
}

fun htmlStatement(invoice: Invoice, plays: Map<String, Play>): String {
    return renderHtml(StatementData(invoice.customer, invoice.performances, plays))
}

fun renderHtml(data: StatementData): String {
    var result = "<h1>Statement for ${data.customer}</h1>\n"
    result += "<table>\n"
    result += "<tr><th>play</th><th>seats</th><th>cost</th></tr>"
    for (perf in data.performances) {
        result += "  <tr><td>${perf.play.name}</td><td>${perf.audience}</td>"
        result += "<td>${usd(perf.amount)}</td></tr>\n"
    }
    result += "</table>\n"
    result += "<p>Amount owed is <em>${usd(data.totalAmount)}</em></p>\n"
    result += "<p>You earned <em>${"%.0f".format(data.totalVolumeCredits)}</em> credits</p>\n"
    return result
}

fun usd(amount: Int): String {
    return "\$%.2f".format(amount.toFloat())
}
