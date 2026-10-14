package app.chesslock

import android.content.Context

/** Lichess format: FEN is before the opponent's move; moves[0] is the opponent's, then you, and so on. */
data class Puzzle(val fen: String, val moves: List<String>, val rating: Int)

object Puzzles {
    private val builtIn = listOf(
        Puzzle("6k1/5ppp/8/8/8/1n6/5PPP/R5K1 b - - 0 1", listOf("b3d4", "a1a8"), 400),
        Puzzle("r1bqkbnr/pppp1ppp/2n5/4p3/2B1P3/5Q2/PPPP1PPP/RNB1K1NR b KQkq - 0 1", listOf("a7a6", "f3f7"), 450),
        Puzzle("6k1/p4ppp/8/8/8/8/5PPP/3Q2K1 b - - 0 1", listOf("a7a6", "d1d8"), 400)
    )
    var all: List<Puzzle> = builtIn
        private set

    /** Reads assets/puzzles.txt (lines of `FEN|moves|rating`). Falls back to the built-in puzzles. */
    fun load(c: Context) {
        runCatching {
            val list = c.assets.open("puzzles.txt").bufferedReader().useLines { lines ->
                lines.mapNotNull { l ->
                    val p = l.split("|")
                    if (p.size < 3) null else Puzzle(p[0], p[1].trim().split(" "), p[2].trim().toIntOrNull() ?: 0)
                }.toList()
            }
            if (list.isNotEmpty()) all = list
        }
    }

    fun random(not: Puzzle? = null): Puzzle {
        var p = all.random()
        if (p == not && all.size > 1) p = all.random()
        return p
    }
}
