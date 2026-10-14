package app.chesslock

/** Lichess format: FEN is before the opponent's move; moves[0] is the opponent's, then you, and so on. */
data class Puzzle(val fen: String, val moves: List<String>, val rating: Int)

object Puzzles {
    val all = listOf(
        Puzzle("6k1/5ppp/8/8/8/1n6/5PPP/R5K1 b - - 0 1", listOf("b3d4", "a1a8"), 400),
        Puzzle("r1bqkbnr/pppp1ppp/2n5/4p3/2B1P3/5Q2/PPPP1PPP/RNB1K1NR b KQkq - 0 1", listOf("a7a6", "f3f7"), 450),
        Puzzle("6k1/p4ppp/8/8/8/8/5PPP/3Q2K1 b - - 0 1", listOf("a7a6", "d1d8"), 400)
    )
    fun random(not: Puzzle? = null) = all.filter { it != not }.ifEmpty { all }.random()
}
