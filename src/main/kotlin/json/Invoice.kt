package json

class Invoice(
    val customer: String,
    val performances: List<Performance>
)

class Performance(
    val playId: String,
    val audience: Int
)
